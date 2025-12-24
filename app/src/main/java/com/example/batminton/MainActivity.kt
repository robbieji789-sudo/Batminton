package com.example.batminton

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
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

        // 这里就是第 26 行左右的调用点
        binding.btnSaveMatch.setOnClickListener {
            saveMatch()
        }
    }

    private fun setupRecyclerView() {
        matchHistory.clear()
        matchHistory.addAll(MatchStorage.loadMatches(this))

        adapter = HistoryAdapter(matchHistory) { position ->
            deleteMatch(position)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 滑动后通过刷新来保持“露出”状态
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val buttonWidth = 240f
                val translationX = if (-dX > buttonWidth) -buttonWidth else dX
                val foregroundView = (viewHolder as HistoryAdapter.ViewHolder).binding.viewForeground
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, translationX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                val foregroundView = (viewHolder as HistoryAdapter.ViewHolder).binding.viewForeground
                getDefaultUIUtil().clearView(foregroundView)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvHistory)
    }

    // --- 核心修复：确保下面这些函数存在于类中 ---

    private fun saveMatch() {
        val p1 = binding.etPlayerA.text.toString().trim()
        val p2 = binding.etPlayerB.text.toString().trim()
        val p3 = binding.etPlayerC.text.toString().trim()
        val p4 = binding.etPlayerD.text.toString().trim()
        val sA = binding.etScoreA.text.toString().toIntOrNull() ?: 0
        val sB = binding.etScoreB.text.toString().toIntOrNull() ?: 0

        val newRecord = MatchRecord(
            playerA = p1,
            playerB = p2,
            playerC = if (p3.isEmpty()) null else p3,
            playerD = if (p4.isEmpty()) null else p4,
            scoreA = sA,
            scoreB = sB,
            timestamp = System.currentTimeMillis()
        )

        matchHistory.add(0, newRecord)
        adapter.notifyItemInserted(0)
        binding.rvHistory.scrollToPosition(0)
        MatchStorage.saveMatches(this, matchHistory)

        clearInputs()
        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show()
    }

    private fun deleteMatch(position: Int) {
        if (position >= 0 && position < matchHistory.size) {
            matchHistory.removeAt(position)
            adapter.notifyItemRemoved(position)
            MatchStorage.saveMatches(this, matchHistory)
            Toast.makeText(this, "记录已删除", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupInputListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateInputs() }
        }
        binding.etPlayerA.addTextChangedListener(watcher)
        binding.etPlayerB.addTextChangedListener(watcher)
        binding.etPlayerC.addTextChangedListener(watcher)
        binding.etPlayerD.addTextChangedListener(watcher)
        binding.etScoreA.addTextChangedListener(watcher)
        binding.etScoreB.addTextChangedListener(watcher)
    }

    private fun validateInputs() {
        val p1 = binding.etPlayerA.text.toString().trim()
        val p2 = binding.etPlayerB.text.toString().trim()
        val sA = binding.etScoreA.text.toString().trim()
        val sB = binding.etScoreB.text.toString().trim()

        val isValid = p1.isNotEmpty() && p2.isNotEmpty() && sA.isNotEmpty() && sB.isNotEmpty()

        binding.btnSaveMatch.isEnabled = isValid
        binding.btnSaveMatch.backgroundTintList = ColorStateList.valueOf(
            if (isValid) Color.parseColor("#6200EE") else Color.GRAY
        )
    }

    private fun clearInputs() {
        binding.etPlayerA.text.clear()
        binding.etPlayerB.text.clear()
        binding.etPlayerC.text.clear()
        binding.etPlayerD.text.clear()
        binding.etScoreA.text.clear()
        binding.etScoreB.text.clear()
        binding.etScoreB.clearFocus()
    }
}