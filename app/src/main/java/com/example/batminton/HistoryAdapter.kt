package com.example.batminton

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batminton.databinding.ItemMatchHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val matches: List<MatchRecord>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

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

        // 核心逻辑：根据是否有选手 3 和 4 来决定显示格式
        val isDouble = !match.playerC.isNullOrEmpty() && !match.playerD.isNullOrEmpty()

        val playerText = if (isDouble) {
            // 双打显示格式：选手1/选手3 vs 选手2/选手4
            "${match.playerA}/${match.playerC} vs ${match.playerB}/${match.playerD}"
        } else {
            // 单打显示格式：选手1 vs 选手2
            "${match.playerA} vs ${match.playerB}"
        }

        holder.binding.tvPlayers.text = playerText
        holder.binding.tvScoreResult.text = "${match.scoreA} : ${match.scoreB}"

        // 格式化时间显示
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(match.timestamp))
    }

    override fun getItemCount(): Int = matches.size
}