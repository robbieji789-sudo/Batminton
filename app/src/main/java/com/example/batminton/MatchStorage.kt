package com.example.batminton

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 比赛记录数据模型
 * playerC 和 playerD 使用 String?，表示在单打模式下它们可以为 null
 */
data class MatchRecord(
    val playerA: String,
    val playerB: String,
    val playerC: String? = null,
    val playerD: String? = null,
    val scoreA: Int,
    val scoreB: Int,
    val timestamp: Long
)

/**
 * 比赛记录持久化工具类
 */
class MatchStorage {

    companion object {
        private const val PREFS_NAME = "match_history_prefs"
        private const val KEY_HISTORY = "history_data"
        private val gson = Gson()

        /**
         * 保存完整的比赛记录列表到本地
         */
        fun saveMatches(context: Context, historyList: List<MatchRecord>) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonStr = gson.toJson(historyList)
            sharedPreferences.edit().putString(KEY_HISTORY, jsonStr).apply()
        }

        /**
         * 从本地加载所有比赛记录
         * 如果没有记录则返回空列表
         */
        fun loadMatches(context: Context): List<MatchRecord> {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonStr = sharedPreferences.getString(KEY_HISTORY, null)

            return if (jsonStr != null) {
                val type = object : TypeToken<List<MatchRecord>>() {}.type
                gson.fromJson(jsonStr, type)
            } else {
                emptyList()
            }
        }
    }
}