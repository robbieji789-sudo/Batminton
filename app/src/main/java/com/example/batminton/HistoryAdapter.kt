package com.example.batminton

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batminton.databinding.ItemMatchHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val matches: MutableList<MatchRecord>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    // 跟踪当前滑动的position
    private var _swipedPosition = -1

    // 提供公开的只读属性
    val swipedPosition: Int
        get() = _swipedPosition

    inner class ViewHolder(val binding: ItemMatchHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matches[position]

        // 判定单双打显示逻辑
        val isDouble = !match.playerC.isNullOrEmpty() && !match.playerD.isNullOrEmpty()
        holder.binding.tvPlayers.text = if (isDouble) {
            "${match.playerA}/${match.playerC} vs ${match.playerB}/${match.playerD}"
        } else {
            "${match.playerA} vs ${match.playerB}"
        }

        holder.binding.tvScoreResult.text = "${match.scoreA} : ${match.scoreB}"

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(match.timestamp))

        // 设置删除按钮点击事件
        holder.binding.ivDelete.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onDeleteClick(currentPos)
                resetSwipedPosition()
            }
        }

        // 根据滑动状态设置前景层的平移
        if (position == _swipedPosition) {
            holder.binding.viewForeground.translationX = -120f // 向左平移120dp完全露出删除区域
        } else {
            holder.binding.viewForeground.translationX = 0f // 恢复原位
        }
    }

    override fun getItemCount(): Int = matches.size

    // 设置滑动状态
    fun setSwipedPosition(position: Int) {
        val oldPosition = _swipedPosition
        _swipedPosition = position

        // 更新UI
        if (oldPosition != -1 && oldPosition < itemCount) {
            notifyItemChanged(oldPosition)
        }
        if (position != -1 && position < itemCount) {
            notifyItemChanged(position)
        }
    }

    // 重置滑动状态
    fun resetSwipedPosition() {
        val oldPosition = _swipedPosition
        _swipedPosition = -1

        if (oldPosition != -1 && oldPosition < itemCount) {
            notifyItemChanged(oldPosition)
        }
    }

    // 移除项目
    fun removeItem(position: Int) {
        if (position >= 0 && position < matches.size) {
            matches.removeAt(position)
            if (_swipedPosition == position) {
                _swipedPosition = -1
            } else if (_swipedPosition > position) {
                _swipedPosition--
            }
            notifyItemRemoved(position)
        }
    }
}