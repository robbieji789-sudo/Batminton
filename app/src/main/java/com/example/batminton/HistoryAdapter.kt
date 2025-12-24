package com.example.batminton

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.batminton.databinding.ItemMatchHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private var matches: List<MatchRecord>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    // 视图持有者，持有每个列表项的 Binding 对象
    class ViewHolder(val binding: ItemMatchHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    // 创建新视图
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 绑定数据到视图
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matches[position]
        // 设置选手名字
        holder.binding.tvPlayers.text = "${match.playerA} vs ${match.playerB}"
        // 设置比分
        holder.binding.tvScoreResult.text = "${match.scoreA} : ${match.scoreB}"

        // 格式化并设置时间
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(match.timestamp))
    }

    // 返回列表项总数
    override fun getItemCount() = matches.size

    // 用于更新列表数据的方法
    fun updateData(newMatches: List<MatchRecord>) {
        matches = newMatches
        notifyDataSetChanged() // 通知列表刷新
    }
}