package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Chapter
import com.example.data.model.Character
import com.example.data.model.ChatMessage
import com.example.data.model.Novel
import com.example.data.model.PlotBeat
import com.example.data.model.WorldItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NovelDao {
    // --- Novels ---
    @Query("SELECT * FROM novels ORDER BY updatedAt DESC")
    fun getAllNovels(): Flow<List<Novel>>

    @Query("SELECT * FROM novels WHERE id = :id LIMIT 1")
    fun getNovelById(id: Long): Flow<Novel?>

    @Query("SELECT * FROM novels WHERE id = :id LIMIT 1")
    suspend fun getNovelByIdSync(id: Long): Novel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovel(novel: Novel): Long

    @Update
    suspend fun updateNovel(novel: Novel)

    @Delete
    suspend fun deleteNovel(novel: Novel)

    // --- Plot Beats ---
    @Query("SELECT * FROM plot_beats WHERE novelId = :novelId ORDER BY orderIndex ASC")
    fun getPlotBeats(novelId: Long): Flow<List<PlotBeat>>

    @Query("SELECT * FROM plot_beats WHERE novelId = :novelId ORDER BY orderIndex ASC")
    suspend fun getPlotBeatsSync(novelId: Long): List<PlotBeat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlotBeat(beat: PlotBeat): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlotBeats(beats: List<PlotBeat>)

    @Update
    suspend fun updatePlotBeat(beat: PlotBeat)

    @Delete
    suspend fun deletePlotBeat(beat: PlotBeat)

    @Query("DELETE FROM plot_beats WHERE novelId = :novelId")
    suspend fun clearPlotBeats(novelId: Long)

    // --- Characters ---
    @Query("SELECT * FROM characters WHERE novelId = :novelId ORDER BY id ASC")
    fun getCharacters(novelId: Long): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE novelId = :novelId ORDER BY id ASC")
    suspend fun getCharactersSync(novelId: Long): List<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: Character): Long

    @Update
    suspend fun updateCharacter(character: Character)

    @Delete
    suspend fun deleteCharacter(character: Character)

    // --- World Items ---
    @Query("SELECT * FROM world_items WHERE novelId = :novelId ORDER BY category ASC, name ASC")
    fun getWorldItems(novelId: Long): Flow<List<WorldItem>>

    @Query("SELECT * FROM world_items WHERE novelId = :novelId ORDER BY category ASC, name ASC")
    suspend fun getWorldItemsSync(novelId: Long): List<WorldItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorldItem(item: WorldItem): Long

    @Update
    suspend fun updateWorldItem(item: WorldItem)

    @Delete
    suspend fun deleteWorldItem(item: WorldItem)

    // --- Chapters ---
    @Query("SELECT * FROM chapters WHERE novelId = :novelId ORDER BY chapterNumber ASC")
    fun getChapters(novelId: Long): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE novelId = :novelId ORDER BY chapterNumber ASC")
    suspend fun getChaptersSync(novelId: Long): List<Chapter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Update
    suspend fun updateChapter(chapter: Chapter)

    @Delete
    suspend fun deleteChapter(chapter: Chapter)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages WHERE novelId = :novelId ORDER BY timestamp ASC")
    fun getChatMessages(novelId: Long): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE novelId = :novelId")
    suspend fun clearChatMessages(novelId: Long)
}
