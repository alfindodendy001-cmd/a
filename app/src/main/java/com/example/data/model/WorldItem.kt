package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "world_items",
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
data class WorldItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val novelId: Long,
    val category: String, // "Setting & Location", "Magic / Tech System", "Faction & Society", "Lore & History", "Relic & Artifact"
    val name: String,
    val summary: String,
    val rulesAndLimitations: String = "", // Crucial Sanderson's law rules / hard constraints
    val costsAndConsequences: String = "", // What price must be paid to use or survive this?
    val sensoryDetails: String = "", // Sight, sound, smell, atmosphere
    val secretsAndForeshadowing: String = "" // Hidden truths to reveal later
)
