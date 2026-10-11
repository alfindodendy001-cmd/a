package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
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
data class Chapter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val novelId: Long,
    val chapterNumber: Int,
    val title: String,
    val povCharacter: String = "",
    val settingLocation: String = "",
    val sceneGoal: String = "", // Goal: What does the protagonist intend to achieve?
    val centralConflict: String = "", // Conflict: What immediate obstacle confronts them?
    val disasterOrTwist: String = "", // Disaster: How does the scene go wrong or evolve?
    val hookOrCliffhanger: String = "", // Hook: Unresolved question or ticking clock cliffhanger
    val sceneBeats: String = "", // Bullet points of actions and dialogue exchanges
    val wordCountEstimate: Int = 2500,
    val actualWordCount: Int = 0,
    val status: String = "Outlined" // "Idea", "Outlined", "Drafting", "Polished", "Complete"
)
