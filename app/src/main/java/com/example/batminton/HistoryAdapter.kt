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

    class ViewHolder(val binding: ItemMatchHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matches[position]

        // 1. 判断并显示单打或双打格式
        val isDouble = !match.playerC.isNullOrEmpty() && !match.playerD.isNullOrEmpty()
        holder.binding.tvPlayers.text = if (isDouble) {
            "${match.playerA}/${match.playerC} vs ${match.playerB}/${match.playerD}"
        } else {
            "${match.playerA} vs ${match.playerB}"
        }

        // 2. 显示比分
        holder.binding.tvScoreResult.text = "${match.scoreA} : ${match.scoreB}"

        // 3. 格式化并显示日期
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(match.timestamp))

        // 4. 绑定删除区域点击事件（红色正方形区域）
        holder.binding.deleteRegion.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onDeleteClick(currentPos)
            }
        }
    }

    override fun getItemCount(): Int = matches.size
}