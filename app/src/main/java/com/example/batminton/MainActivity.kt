package com.example.batminton

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batminton.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var matchStorage: MatchStorage
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据存储工具
        matchStorage = MatchStorage(this)

        // 设置底部的历史记录列表
        setupRecyclerView()

        // 设置“保存对局”按钮的点击事件
        binding.btnSaveMatch.setOnClickListener {
            saveMatchClick()
        }
    }

    // 配置 RecyclerView
    private fun setupRecyclerView() {
        // 1. 获取历史数据
        val historyData = matchStorage.getHistory()
        // 2. 创建适配器
        historyAdapter = HistoryAdapter(historyData)
        // 3. 设置给 RecyclerView
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter
    }

    // 点击保存按钮后的逻辑
    private fun saveMatchClick() {
        // 获取输入内容
        val playerA = binding.etPlayerA.text.toString().trim()
        val playerB = binding.etPlayerB.text.toString().trim()
        val scoreAStr = binding.etScoreA.text.toString().trim()
        val scoreBStr = binding.etScoreB.text.toString().trim()

        // 简单的输入校验
        if (playerA.isEmpty() || playerB.isEmpty()) {
            Toast.makeText(this, "请输入双方选手名字", Toast.LENGTH_SHORT).show()
            return
        }
        if (scoreAStr.isEmpty() || scoreBStr.isEmpty()) {
            Toast.makeText(this, "请输入比分", Toast.LENGTH_SHORT).show()
            return
        }

        // 将比分转为数字
        val scoreA = scoreAStr.toIntOrNull() ?: 0
        val scoreB = scoreBStr.toIntOrNull() ?: 0

        // 创建记录对象
        val newRecord = MatchRecord(playerA, playerB, scoreA, scoreB, System.currentTimeMillis())

        // 1. 保存到本地
        matchStorage.saveMatch(newRecord)

        // 2. 刷新界面上的历史记录列表
        historyAdapter.updateData(matchStorage.getHistory())
        // 让列表滚动到最顶部显示最新记录
        binding.rvHistory.scrollToPosition(0)

        // 3. 清空比分输入框，方便下一局
        binding.etScoreA.text.clear()
        binding.etScoreB.text.clear()
        // 可选：清空名字输入框
        // binding.etPlayerA.text.clear()
        // binding.etPlayerB.text.clear()

        Toast.makeText(this, "对局已保存！", Toast.LENGTH_SHORT).show()
        // 隐藏键盘
        binding.root.clearFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}