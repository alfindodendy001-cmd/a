package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.NovelDatabase
import com.example.data.model.Chapter
import com.example.data.model.Character
import com.example.data.model.ChatMessage
import com.example.data.model.Novel
import com.example.data.model.PlotBeat
import com.example.data.model.WorldItem
import com.example.data.repository.NovelRepository
import com.example.network.GeminiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NovelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NovelRepository
    private val geminiService = GeminiService()

    init {
        val db = NovelDatabase.getInstance(application)
        repository = NovelRepository(db.novelDao())
    }

    val allNovels: StateFlow<List<Novel>> = repository.allNovels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedNovelId = MutableStateFlow<Long?>(1L)
    val selectedNovelId: StateFlow<Long?> = _selectedNovelId.asStateFlow()

    val currentNovel: StateFlow<Novel?> = _selectedNovelId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getNovel(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val plotBeats: StateFlow<List<PlotBeat>> = _selectedNovelId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getPlotBeats(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val characters: StateFlow<List<Character>> = _selectedNovelId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getCharacters(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val worldItems: StateFlow<List<WorldItem>> = _selectedNovelId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getWorldItems(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapters: StateFlow<List<Chapter>> = _selectedNovelId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getChapters(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = _selectedNovelId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getChatMessages(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    private val _selectedTab = MutableStateFlow(0) // 0: Co-Writer, 1: Plot, 2: Characters, 3: World, 4: Chapters
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _currentStage = MutableStateFlow("Brainstorming")
    val currentStage: StateFlow<String> = _currentStage.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun selectStage(stage: String) {
        _currentStage.value = stage
    }

    fun selectNovel(novelId: Long) {
        _selectedNovelId.value = novelId
    }

    // Novel operations
    fun createNovel(
        title: String,
        genre: String,
        tagline: String,
        logline: String,
        targetWords: Int,
        structure: String
    ) {
        viewModelScope.launch {
            val newNovel = Novel(
                title = title,
                genre = genre,
                tagline = tagline,
                logline = logline,
                targetWordCount = targetWords,
                structureType = structure
            )
            val newId = repository.createNovel(newNovel)
            _selectedNovelId.value = newId
        }
    }

    fun updateNovel(novel: Novel) {
        viewModelScope.launch {
            repository.updateNovel(novel.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNovel(novel: Novel) {
        viewModelScope.launch {
            repository.deleteNovel(novel)
            val remaining = allNovels.value.filter { it.id != novel.id }
            _selectedNovelId.value = remaining.firstOrNull()?.id
        }
    }

    fun switchPlotFramework(framework: String) {
        val novelId = _selectedNovelId.value ?: return
        viewModelScope.launch {
            repository.switchFramework(novelId, framework)
        }
    }

    // Plot Beat operations
    fun toggleBeatCompleted(beat: PlotBeat) {
        viewModelScope.launch {
            repository.updatePlotBeat(beat.copy(isCompleted = !beat.isCompleted))
        }
    }

    fun updatePlotBeat(beat: PlotBeat) {
        viewModelScope.launch {
            repository.updatePlotBeat(beat)
        }
    }

    fun addPlotBeat(act: String, name: String, desc: String, notes: String) {
        val novelId = _selectedNovelId.value ?: return
        viewModelScope.launch {
            val currentList = plotBeats.value
            val nextIndex = (currentList.maxOfOrNull { it.orderIndex } ?: 0) + 1
            repository.savePlotBeat(
                PlotBeat(
                    novelId = novelId,
                    framework = currentNovel.value?.structureType ?: "Custom",
                    act = act,
                    beatName = name,
                    description = desc,
                    userNotes = notes,
                    orderIndex = nextIndex
                )
            )
        }
    }

    fun deletePlotBeat(beat: PlotBeat) {
        viewModelScope.launch {
            repository.deletePlotBeat(beat)
        }
    }

    // Character operations
    fun saveCharacter(character: Character) {
        viewModelScope.launch {
            if (character.id == 0L) {
                repository.saveCharacter(character)
            } else {
                repository.updateCharacter(character)
            }
        }
    }

    fun deleteCharacter(character: Character) {
        viewModelScope.launch {
            repository.deleteCharacter(character)
        }
    }

    // World Item operations
    fun saveWorldItem(item: WorldItem) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.saveWorldItem(item)
            } else {
                repository.updateWorldItem(item)
            }
        }
    }

    fun deleteWorldItem(item: WorldItem) {
        viewModelScope.launch {
            repository.deleteWorldItem(item)
        }
    }

    // Chapter operations
    fun saveChapter(chapter: Chapter) {
        viewModelScope.launch {
            if (chapter.id == 0L) {
                repository.saveChapter(chapter)
            } else {
                repository.updateChapter(chapter)
            }
        }
    }

    fun deleteChapter(chapter: Chapter) {
        viewModelScope.launch {
            repository.deleteChapter(chapter)
        }
    }

    // Co-Writer Interaction
    fun sendMessageToCoWriter(userText: String, stage: String = _currentStage.value) {
        val novelId = _selectedNovelId.value ?: return
        if (userText.isBlank()) return

        viewModelScope.launch {
            // Save user message
            val userMsg = ChatMessage(
                novelId = novelId,
                sender = "user",
                content = userText,
                stage = stage
            )
            repository.saveChatMessage(userMsg)

            _isGenerating.value = true
            try {
                val novel = currentNovel.value
                val chars = characters.value
                val lore = worldItems.value

                val response = geminiService.generateCoWriterResponse(
                    prompt = userText,
                    novel = novel,
                    characters = chars,
                    worldItems = lore,
                    stage = stage
                )

                val aiMsg = ChatMessage(
                    novelId = novelId,
                    sender = "assistant",
                    content = response,
                    stage = stage
                )
                repository.saveChatMessage(aiMsg)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun clearChat() {
        val novelId = _selectedNovelId.value ?: return
        viewModelScope.launch {
            repository.clearChatMessages(novelId)
            // Post fresh greeting
            val novel = currentNovel.value
            val greeting = ChatMessage(
                novelId = novelId,
                sender = "assistant",
                content = "👋 Clean slate! I'm your Co-Writer for **${novel?.title ?: "your novel"}**.\n\nWhat would you like to explore next? Choose a planning stage or tell me your ideas!",
                stage = _currentStage.value
            )
            repository.saveChatMessage(greeting)
        }
    }

    // Export Story Bible & Outline to Markdown
    fun generateStoryBibleMarkdown(): String {
        val novel = currentNovel.value ?: return "# No Novel Selected"
        val chars = characters.value
        val lore = worldItems.value
        val beats = plotBeats.value
        val chaps = chapters.value

        return buildString {
            appendLine("# 📖 ${novel.title}")
            appendLine("**Genre:** ${novel.genre} | **Target Words:** ${novel.targetWordCount} | **Status:** ${novel.status}")
            if (novel.tagline.isNotBlank()) appendLine("**Tagline:** *${novel.tagline}*")
            if (novel.logline.isNotBlank()) appendLine("\n### 🎯 Logline\n${novel.logline}")
            appendLine("\n**Tone & POV:** ${novel.tone} (${novel.povStyle})")
            appendLine("\n---\n")

            // Plot Structure
            appendLine("## 📐 Plot Structure: ${novel.structureType}")
            beats.forEach { beat ->
                val check = if (beat.isCompleted) "✅" else "⬜"
                appendLine("### $check ${beat.beatName} (${beat.act})")
                if (beat.targetPercentage.isNotBlank()) appendLine("*Pacing Target:* ${beat.targetPercentage}")
                appendLine("*Guideline:* ${beat.description}")
                if (beat.userNotes.isNotBlank()) appendLine("**Plot Notes:** ${beat.userNotes}")
                appendLine()
            }
            appendLine("---\n")

            // Characters
            appendLine("## 👤 Character Roster (${chars.size})")
            chars.forEach { c ->
                appendLine("### ${c.name} — *${c.role}*")
                if (c.archetype.isNotBlank()) appendLine("**Archetype:** ${c.archetype}")
                if (c.fatalFlaw.isNotBlank()) appendLine("**Fatal Flaw:** ${c.fatalFlaw}")
                if (c.theLie.isNotBlank()) appendLine("**The Lie Believed:** ${c.theLie}")
                if (c.theTruth.isNotBlank()) appendLine("**The Truth Needed:** ${c.theTruth}")
                if (c.motivation.isNotBlank()) appendLine("**Motivation / Desire:** ${c.motivation}")
                if (c.backstory.isNotBlank()) appendLine("**Backstory:** ${c.backstory}")
                if (c.relationshipDynamics.isNotBlank()) appendLine("**Relationships:** ${c.relationshipDynamics}")
                appendLine()
            }
            appendLine("---\n")

            // World-Building Lore
            appendLine("## 🌍 World-Building Codex (${lore.size})")
            lore.groupBy { it.category }.forEach { (cat, items) ->
                appendLine("### Category: $cat")
                items.forEach { item ->
                    appendLine("#### ❖ ${item.name}")
                    appendLine(item.summary)
                    if (item.rulesAndLimitations.isNotBlank()) appendLine("**Rules & Hard Limits:** ${item.rulesAndLimitations}")
                    if (item.costsAndConsequences.isNotBlank()) appendLine("**Cost / Backlash:** ${item.costsAndConsequences}")
                    if (item.secretsAndForeshadowing.isNotBlank()) appendLine("**Secret Foreshadowing:** ${item.secretsAndForeshadowing}")
                    appendLine()
                }
            }
            appendLine("---\n")

            // Chapters Outline
            appendLine("## 📚 Chapter-by-Chapter Outline (${chaps.size})")
            chaps.forEach { ch ->
                appendLine("### Chapter ${ch.chapterNumber}: ${ch.title} [${ch.status}]")
                if (ch.povCharacter.isNotBlank()) appendLine("**POV:** ${ch.povCharacter} | **Setting:** ${ch.settingLocation}")
                if (ch.sceneGoal.isNotBlank()) appendLine("**Scene Goal:** ${ch.sceneGoal}")
                if (ch.centralConflict.isNotBlank()) appendLine("**Central Conflict:** ${ch.centralConflict}")
                if (ch.disasterOrTwist.isNotBlank()) appendLine("**Disaster / Twist:** ${ch.disasterOrTwist}")
                if (ch.hookOrCliffhanger.isNotBlank()) appendLine("**🔥 Cliffhanger Ending:** ${ch.hookOrCliffhanger}")
                if (ch.sceneBeats.isNotBlank()) {
                    appendLine("**Scene Progression:**\n${ch.sceneBeats}")
                }
                appendLine()
            }
        }
    }
}
