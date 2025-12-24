package com.example.batminton

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.batminton.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // 点击添加对局
        binding.btnAddMatch.setOnClickListener {
            showAddMatchDialog()
        }

        // 点击查看历史
        binding.btnViewHistory.setOnClickListener {
            showHistoryDialog()
        }
    }

    // 弹窗输入比赛结果
    private fun showAddMatchDialog() {
        // 这里为了简单，直接通过代码生成简单的输入框
        // 实际开发中会跳转到另一个专门的界面
        lifecycleScope.launch {
            val newMatch = Match(
                playerA = "玩家1",
                playerB = "玩家2",
                scoreA = 21,
                scoreB = 18,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            db.matchDao().insert(newMatch)
            Toast.makeText(this@MainActivity, "对局已保存！", Toast.LENGTH_SHORT).show()
        }
    }

    // 弹窗展示历史记录
    private fun showHistoryDialog() {
        lifecycleScope.launch {
            val matches = db.matchDao().getAllMatches()
            val historyText = matches.joinToString("\n") {
                "${it.date}: ${it.playerA} (${it.scoreA}) vs ${it.playerB} (${it.scoreB})"
            }

            AlertDialog.Builder(this@MainActivity)
                .setTitle("历史对局")
                .setMessage(if(historyText.isEmpty()) "暂无数据" else historyText)
                .setPositiveButton("确定", null)
                .show()
        }
    }
}