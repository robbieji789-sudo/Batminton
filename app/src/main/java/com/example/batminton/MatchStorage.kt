package com.example.batminton

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// 定义一条比赛记录包含哪些数据
data class MatchRecord(
    val playerA: String,
    val playerB: String,
    val scoreA: Int,
    val scoreB: Int,
    val timestamp: Long // 记录比赛时间
)

// 这个类负责数据的存取
class MatchStorage(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("match_history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 保存一条新记录
    fun saveMatch(record: MatchRecord) {
        // 1. 先取出已有的历史记录
        val historyList = getHistory().toMutableList()
        // 2. 把新记录加到最前面
        historyList.add(0, record)
        // 3. 把列表转换成 JSON 字符串
        val jsonStr = gson.toJson(historyList)
        // 4. 保存到 SharedPreferences
        sharedPreferences.edit().putString("history_data", jsonStr).apply()
    }

    // 读取所有历史记录
    fun getHistory(): List<MatchRecord> {
        // 1. 读取 JSON 字符串
        val jsonStr = sharedPreferences.getString("history_data", null)
        return if (jsonStr != null) {
            // 2. 如果有数据，把它转回 List<MatchRecord>
            val type = object : TypeToken<List<MatchRecord>>() {}.type
            gson.fromJson(jsonStr, type)
        } else {
            // 3. 如果没数据，返回一个空列表
            emptyList()
        }
    }
}