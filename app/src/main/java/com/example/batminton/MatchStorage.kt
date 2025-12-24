package com.example.batminton

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 统一数据模型
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

class MatchStorage {
    companion object {
        private const val PREFS_NAME = "match_history_prefs"
        private const val KEY_HISTORY = "history_data"
        private val gson = Gson()

        fun saveMatches(context: Context, historyList: List<MatchRecord>) {
            try {
                val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val jsonStr = gson.toJson(historyList)
                sharedPreferences.edit().putString(KEY_HISTORY, jsonStr).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun loadMatches(context: Context): List<MatchRecord> {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonStr = sharedPreferences.getString(KEY_HISTORY, null)

            return try {
                if (!jsonStr.isNullOrEmpty()) {
                    val type = object : TypeToken<MutableList<MatchRecord>>() {}.type
                    gson.fromJson(jsonStr, type)
                } else {
                    mutableListOf()
                }
            } catch (e: Exception) {
                // 如果解析出错（比如改了数据结构），返回空列表并清除错误数据
                sharedPreferences.edit().remove(KEY_HISTORY).apply()
                mutableListOf()
            }
        }
    }
}