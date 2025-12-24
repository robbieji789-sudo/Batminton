package com.example.batminton

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val player1: String,
    val player2: String?, // ? 表示可以为空（单打时）
    val player3: String,
    val player4: String?,
    val scoreA: Int,
    val scoreB: Int,
    val date: String
)