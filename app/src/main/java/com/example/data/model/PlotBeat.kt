package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plot_beats",
    foreignKeys = [
        ForeignKey(
            entity = Novel::class,
            parentColumns = ["id"],
            childColumns = ["novelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("novelId")]
)
data class PlotBeat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val novelId: Long,
    val framework: String = "3-Act Structure",
    val act: String,
    val beatName: String,
    val targetPercentage: String = "",
    val description: String,
    val userNotes: String = "",
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false
)
