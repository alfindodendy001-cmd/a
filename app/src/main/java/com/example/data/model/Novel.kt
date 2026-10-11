package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "novels")
data class Novel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val genre: String,
    val tagline: String = "",
    val logline: String = "",
    val targetWordCount: Int = 80000,
    val currentWordCount: Int = 0,
    val status: String = "Outlining",
    val structureType: String = "3-Act Structure",
    val povStyle: String = "Third Person Limited",
    val tone: String = "Cinematic, Immersive, High Stakes",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
