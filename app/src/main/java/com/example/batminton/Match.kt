package com.example.batminton

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerA: String,
    val playerB: String,
    val scoreA: Int,
    val scoreB: Int,
    val date: String
)