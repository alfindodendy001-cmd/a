package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
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
data class Character(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val novelId: Long,
    val name: String,
    val role: String, // Protagonist, Antagonist, Mentor, Foil, Love Interest, Sidekick
    val archetype: String = "",
    val motivation: String = "", // What they want (External Goal)
    val need: String = "", // What they actually need spiritually
    val fatalFlaw: String = "", // Fatal flaw / weakness / blindspot
    val theLie: String = "", // The Lie they believe about the world/themselves
    val theTruth: String = "", // The Truth they must embrace
    val backstory: String = "",
    val relationshipDynamics: String = "",
    val physicalDescription: String = "",
    val tagColorHex: String = "#F59E0B"
)
