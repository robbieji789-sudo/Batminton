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
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    // 左滑：切换滑动状态
                    if (adapter.swipedPosition == position) {
                        adapter.resetSwipedPosition()
                    } else {
                        adapter.setSwipedPosition(position)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                // 获取删除按钮宽度（120dp转换成像素）
                val buttonWidth = 120 * resources.displayMetrics.density
                val maxSwipeDistance = -buttonWidth

                // 限制滑动距离
                val translationX = if (dX < maxSwipeDistance) maxSwipeDistance else dX

                // 设置前景层的平移
                val holder = viewHolder as HistoryAdapter.ViewHolder
                holder.binding.viewForeground.translationX = translationX

                // 调用父类方法进行默认绘制
                super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                // 重置前景层位置
                val holder = viewHolder as HistoryAdapter.ViewHolder
                holder.binding.viewForeground.translationX = 0f

                // 调用父类方法
                super.clearView(recyclerView, viewHolder)
            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                // 当滑动超过删除按钮宽度的一半时触发onSwiped
                return 0.5f
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvHistory)
    }

    private fun saveMatch() {
        val p1 = binding.etPlayerA.text.toString().trim()
        val p2 = binding.etPlayerB.text.toString().trim()
        val p3 = binding.etPlayerC.text.toString().trim()
        val p4 = binding.etPlayerD.text.toString().trim()
        val sA = binding.etScoreA.text.toString().toIntOrNull() ?: 0
        val sB = binding.etScoreB.text.toString().toIntOrNull() ?: 0

        // 验证输入 - 修复的验证逻辑
        val isSingles = p3.isEmpty() && p4.isEmpty()
        val isDoubles = p3.isNotEmpty() && p4.isNotEmpty()

        if (!isSingles && !isDoubles) {
            // 双打时只填写了一个选手
            Toast.makeText(this, "双打必须同时填写选手3和选手4", Toast.LENGTH_SHORT).show()
            return
        }

        // 验证选手姓名不能相同
        val players = listOf(p1, p2, p3, p4).filter { it.isNotEmpty() }
        if (players.distinct().size != players.size) {
            Toast.makeText(this, "选手姓名不能重复", Toast.LENGTH_SHORT).show()
            return
        }

        // 验证比分合理性（可选比赛，比如不能都是0分）
        if (sA == 0 && sB == 0) {
            Toast.makeText(this, "比分不能都是0", Toast.LENGTH_SHORT).show()
            return
        }

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
            // 使用适配器的removeItem方法
            adapter.removeItem(position)
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
        val p3 = binding.etPlayerC.text.toString().trim()
        val p4 = binding.etPlayerD.text.toString().trim()
        val sA = binding.etScoreA.text.toString().trim()
        val sB = binding.etScoreB.text.toString().trim()

        // 修复的验证逻辑
        val isSingles = p3.isEmpty() && p4.isEmpty()
        val isDoubles = p3.isNotEmpty() && p4.isNotEmpty()

        val isValid = when {
            isSingles -> {
                // 单打：只需要选手1、2和比分
                p1.isNotEmpty() && p2.isNotEmpty() && sA.isNotEmpty() && sB.isNotEmpty()
            }
            isDoubles -> {
                // 双打：需要所有4个选手和比分
                p1.isNotEmpty() && p2.isNotEmpty() && p3.isNotEmpty() && p4.isNotEmpty() &&
                        sA.isNotEmpty() && sB.isNotEmpty()
            }
            else -> {
                // 双打只填写了一个选手的情况
                false
            }
        }

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
        validateInputs() // 清空后更新按钮状态
        binding.etPlayerA.requestFocus()
    }
}