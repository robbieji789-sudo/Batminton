package com.example.batminton

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.batminton.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HistoryAdapter
    private val matchHistory = mutableListOf<MatchRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupInputListeners()

        binding.btnSaveMatch.setOnClickListener {
            saveMatch()
        }
    }

    private fun setupRecyclerView() {
        // 从本地存储加载数据
        matchHistory.clear()
        matchHistory.addAll(MatchStorage.loadMatches(this))

        // 初始化适配器，传入删除回调逻辑
        adapter = HistoryAdapter(matchHistory) { position ->
            showDeleteConfirmDialog(position)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        // 配置滑动删除辅助工具
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 左滑到底时直接触发确认弹窗
                showDeleteConfirmDialog(viewHolder.adapterPosition)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvHistory)
    }

    private fun setupInputListeners() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = validateInputs()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        val inputs = listOf(
            binding.etPlayerA, binding.etPlayerB, binding.etPlayerC, binding.etPlayerD,
            binding.etScoreA, binding.etScoreB
        )
        inputs.forEach { it.addTextChangedListener(watcher) }
    }

    private fun validateInputs() {
        val p1 = binding.etPlayerA.text.toString().trim()
        val p2 = binding.etPlayerB.text.toString().trim()
        val p3 = binding.etPlayerC.text.toString().trim()
        val p4 = binding.etPlayerD.text.toString().trim()
        val sA = binding.etScoreA.text.toString().trim()
        val sB = binding.etScoreB.text.toString().trim()

        val hasScores = sA.isNotEmpty() && sB.isNotEmpty()
        val isSingleValid = p1.isNotEmpty() && p2.isNotEmpty() && p3.isEmpty() && p4.isEmpty() && hasScores
        val isDoubleValid = p1.isNotEmpty() && p2.isNotEmpty() && p3.isNotEmpty() && p4.isNotEmpty() && hasScores

        if (isSingleValid || isDoubleValid) {
            binding.btnSaveMatch.isEnabled = true
            binding.btnSaveMatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6200EE"))
        } else {
            binding.btnSaveMatch.isEnabled = false
            binding.btnSaveMatch.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        }
    }

    private fun saveMatch() {
        val p1 = binding.etPlayerA.text.toString().trim()
        val p2 = binding.etPlayerB.text.toString().trim()
        val p3 = binding.etPlayerC.text.toString().trim()
        val p4 = binding.etPlayerD.text.toString().trim()
        val sA = binding.etScoreA.text.toString().toIntOrNull() ?: 0
        val sB = binding.etScoreB.text.toString().toIntOrNull() ?: 0

        val newRecord = MatchRecord(
            p1, p2,
            if (p3.isEmpty()) null else p3,
            if (p4.isEmpty()) null else p4,
            sA, sB, System.currentTimeMillis()
        )

        matchHistory.add(0, newRecord)
        adapter.notifyItemInserted(0)
        binding.rvHistory.scrollToPosition(0)
        MatchStorage.saveMatches(this, matchHistory)

        binding.etScoreA.text.clear()
        binding.etScoreB.text.clear()
        hideKeyboard()
        Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("确认删除")
            .setMessage("确定要删除这条记录吗？")
            .setPositiveButton("删除") { _, _ ->
                matchHistory.removeAt(position)
                adapter.notifyItemRemoved(position)
                MatchStorage.saveMatches(this, matchHistory)
            }
            .setNegativeButton("取消") { _, _ ->
                adapter.notifyItemChanged(position) // 滑开的卡片弹回去
            }
            .setCancelable(false)
            .show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}