package com.example.data.repository

import com.example.data.dao.NovelDao
import com.example.data.db.InitialData
import com.example.data.model.Chapter
import com.example.data.model.Character
import com.example.data.model.ChatMessage
import com.example.data.model.Novel
import com.example.data.model.PlotBeat
import com.example.data.model.WorldItem
import kotlinx.coroutines.flow.Flow

class NovelRepository(private val novelDao: NovelDao) {

    val allNovels: Flow<List<Novel>> = novelDao.getAllNovels()

    fun getNovel(id: Long): Flow<Novel?> = novelDao.getNovelById(id)
    suspend fun getNovelSync(id: Long): Novel? = novelDao.getNovelByIdSync(id)

    suspend fun createNovel(novel: Novel): Long {
        val novelId = novelDao.insertNovel(novel)
        // Auto-seed default plot beats based on chosen framework
        val defaultBeats = InitialData.getBeatsForFramework(novelId, novel.structureType)
        novelDao.insertPlotBeats(defaultBeats)
        // Add welcome message in chat
        val welcomeMsg = ChatMessage(
            novelId = novelId,
            sender = "assistant",
            content = "👋 Greetings! I'm your Co-Writer for **${novel.title}** (${novel.genre}).\n\nWhat stage of planning are you currently in?\n- 💡 **Brainstorming:** High-concept hooks, loglines, themes, and conflicts\n- 🌍 **World-Building:** Magic systems, technology, factions, and settings\n- 👤 **Characters:** Fatal flaws, internal arcs, and relationship dynamics\n- 📐 **Plot & Chapters:** Structuring beats and high-stakes cliffhangers\n\nTell me about your core story concept or ask me a question to get started!",
            stage = "Brainstorming"
        )
        novelDao.insertChatMessage(welcomeMsg)
        return novelId
    }

    suspend fun updateNovel(novel: Novel) = novelDao.updateNovel(novel)
    suspend fun deleteNovel(novel: Novel) = novelDao.deleteNovel(novel)

    // Plot Beats
    fun getPlotBeats(novelId: Long): Flow<List<PlotBeat>> = novelDao.getPlotBeats(novelId)
    suspend fun getPlotBeatsSync(novelId: Long): List<PlotBeat> = novelDao.getPlotBeatsSync(novelId)
    suspend fun savePlotBeat(beat: PlotBeat): Long = novelDao.insertPlotBeat(beat)
    suspend fun updatePlotBeat(beat: PlotBeat) = novelDao.updatePlotBeat(beat)
    suspend fun deletePlotBeat(beat: PlotBeat) = novelDao.deletePlotBeat(beat)

    suspend fun switchFramework(novelId: Long, framework: String) {
        novelDao.clearPlotBeats(novelId)
        val beats = InitialData.getBeatsForFramework(novelId, framework)
        novelDao.insertPlotBeats(beats)
        val novel = novelDao.getNovelByIdSync(novelId)
        if (novel != null) {
            novelDao.updateNovel(novel.copy(structureType = framework, updatedAt = System.currentTimeMillis()))
        }
    }

    // Characters
    fun getCharacters(novelId: Long): Flow<List<Character>> = novelDao.getCharacters(novelId)
    suspend fun getCharactersSync(novelId: Long): List<Character> = novelDao.getCharactersSync(novelId)
    suspend fun saveCharacter(character: Character): Long = novelDao.insertCharacter(character)
    suspend fun updateCharacter(character: Character) = novelDao.updateCharacter(character)
    suspend fun deleteCharacter(character: Character) = novelDao.deleteCharacter(character)

    // World Items
    fun getWorldItems(novelId: Long): Flow<List<WorldItem>> = novelDao.getWorldItems(novelId)
    suspend fun getWorldItemsSync(novelId: Long): List<WorldItem> = novelDao.getWorldItemsSync(novelId)
    suspend fun saveWorldItem(item: WorldItem): Long = novelDao.insertWorldItem(item)
    suspend fun updateWorldItem(item: WorldItem) = novelDao.updateWorldItem(item)
    suspend fun deleteWorldItem(item: WorldItem) = novelDao.deleteWorldItem(item)

    // Chapters
    fun getChapters(novelId: Long): Flow<List<Chapter>> = novelDao.getChapters(novelId)
    suspend fun getChaptersSync(novelId: Long): List<Chapter> = novelDao.getChaptersSync(novelId)
    suspend fun saveChapter(chapter: Chapter): Long = novelDao.insertChapter(chapter)
    suspend fun updateChapter(chapter: Chapter) = novelDao.updateChapter(chapter)
    suspend fun deleteChapter(chapter: Chapter) = novelDao.deleteChapter(chapter)

    // Chat
    fun getChatMessages(novelId: Long): Flow<List<ChatMessage>> = novelDao.getChatMessages(novelId)
    suspend fun saveChatMessage(message: ChatMessage): Long = novelDao.insertChatMessage(message)
    suspend fun clearChatMessages(novelId: Long) = novelDao.clearChatMessages(novelId)
}
