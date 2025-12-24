package com.example.batminton

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MatchDao {
    @Insert
    suspend fun insert(match: Match)

    @Query("SELECT * FROM matches ORDER BY id DESC")
    suspend fun getAllMatches(): List<Match>
}