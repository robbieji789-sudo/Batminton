package com.example.batminton

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        // 绑定保存按钮点击事件
        binding.btnSaveMatch.setOnClickListener {
            saveMatch()
        }
    }

    private fun setupRecyclerView() {
        // 从本地加载历史记录
        matchHistory.addAll(MatchStorage.loadMatches(this))
        adapter = HistoryAdapter(matchHistory)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun setupInputListeners() {
        // 定义通用的监听器
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateInputs() // 每次输入后都检查一遍条件
            }
        }

        // 给所有相关的输入框添加监听
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
        val p3 = binding.etPlayerC.text.toString().trim()
        val p4 = binding.etPlayerD.text.toString().trim()
        val sA = binding.etScoreA.text.toString().trim()
        val sB = binding.etScoreB.text.toString().trim()

        val hasScores = sA.isNotEmpty() && sB.isNotEmpty()

        // 情况1：单打 (1和2有值，3和4必须是空的，且有比分)
        val isSingleValid = p1.isNotEmpty() && p2.isNotEmpty() && p3.isEmpty() && p4.isEmpty() && hasScores

        // 情况2：双打 (1,2,3,4 全都有值，且有比分)
        val isDoubleValid = p1.isNotEmpty() && p2.isNotEmpty() && p3.isNotEmpty() && p4.isNotEmpty() && hasScores

        if (isSingleValid || isDoubleValid) {
            // 满足条件：变紫色，启用按钮
            binding.btnSaveMatch.isEnabled = true
            binding.btnSaveMatch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6200EE"))
        } else {
            // 不满足条件：变灰色，禁用按钮
            binding.btnSaveMatch.isEnabled = false
            binding.btnSaveMatch.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        }
    }

    private fun saveMatch() {
        val p1 = binding.etPlayerA.text.toString().trim()
        val p2 = binding.etPlayerB.text.toString().trim()
        val p3 = binding.etPlayerC.text.toString().trim()
        val p4 = binding.etPlayerD.text.toString().trim()
        val sA = binding.etScoreA.text.toString().toInt()
        val sB = binding.etScoreB.text.toString().toInt()

        // 创建新记录 (选手3和4如果没有填，就存为null)
        val newRecord = MatchRecord(
            playerA = p1,
            playerB = p2,
            playerC = if (p3.isEmpty()) null else p3,
            playerD = if (p4.isEmpty()) null else p4,
            scoreA = sA,
            scoreB = sB,
            timestamp = System.currentTimeMillis()
        )

        // 保存到内存并更新列表
        matchHistory.add(0, newRecord)
        adapter.notifyItemInserted(0)
        binding.rvHistory.scrollToPosition(0)

        // 持久化到本地存储
        MatchStorage.saveMatches(this, matchHistory)

        // 清空输入框，按钮会自动变回灰色
        clearInputs()
        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show()
    }

    private fun clearInputs() {
        binding.etPlayerA.text.clear()
        binding.etPlayerB.text.clear()
        binding.etPlayerC.text.clear()
        binding.etPlayerD.text.clear()
        binding.etScoreA.text.clear()
        binding.etScoreB.text.clear()
        // 失去焦点，防止键盘一直弹着
        binding.etScoreB.clearFocus()
    }
}