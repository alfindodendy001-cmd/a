package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Chapter
import com.example.ui.NovelViewModel

@Composable
fun ChaptersScreen(
    viewModel: NovelViewModel,
    onNavigateToCoWriter: (prompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentNovel by viewModel.currentNovel.collectAsState()
    val chapters by viewModel.chapters.collectAsState()

    var chapterToEdit by remember { mutableStateOf<Chapter?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val totalEstimatedWords = chapters.sumOf { it.wordCountEstimate }
    val totalActualWords = chapters.sumOf { it.actualWordCount }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Stats summary card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chapter Outlines",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        Text(
                            text = "${chapters.size} Chapters Planned",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Est. Words: $totalEstimatedWords",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Drafted: $totalActualWords words",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            if (chapters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No chapters outlined yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button to break down your story into goal-conflict-cliffhanger chapters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chapters, key = { it.id }) { chapter ->
                        ChapterCard(
                            chapter = chapter,
                            onEdit = { chapterToEdit = chapter },
                            onDelete = { viewModel.deleteChapter(chapter) },
                            onAskCoWriter = {
                                onNavigateToCoWriter("Help me polish and structure Chapter ${chapter.chapterNumber}: '${chapter.title}' for novel '${currentNovel?.title}'. POV: ${chapter.povCharacter}. Goal: ${chapter.sceneGoal}. Conflict: ${chapter.centralConflict}. Disaster: ${chapter.disasterOrTwist}. Suggest 3 higher-stakes cliffhangers or scene beats!")
                            }
                        )
                    }
                }
            }
        }

        // Add Chapter FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_chapter_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Chapter")
        }
    }

    if (showAddDialog || chapterToEdit != null) {
        val existing = chapterToEdit
        val nextNumber = (chapters.maxOfOrNull { it.chapterNumber } ?: 0) + 1
        ChapterEditDialog(
            chapter = existing,
            novelId = currentNovel?.id ?: 1L,
            defaultChapterNumber = nextNumber,
            onDismiss = {
                showAddDialog = false
                chapterToEdit = null
            },
            onSave = { updated ->
                viewModel.saveChapter(updated)
                showAddDialog = false
                chapterToEdit = null
            }
        )
    }
}

@Composable
fun ChapterCard(
    chapter: Chapter,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAskCoWriter: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Ch Number, Title, Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Ch. ${chapter.chapterNumber}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chapter.povCharacter.isNotBlank()) {
                            Text(
                                text = "POV: ${chapter.povCharacter}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (chapter.settingLocation.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${chapter.settingLocation}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (chapter.status) {
                        "Complete" -> MaterialTheme.colorScheme.tertiaryContainer
                        "Drafting" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = chapter.status,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Chapter",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Goal, Conflict, Disaster Box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (chapter.sceneGoal.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "🎯 Goal: ",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = chapter.sceneGoal,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (chapter.centralConflict.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "⚡ Conflict: ",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = chapter.centralConflict,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (chapter.disasterOrTwist.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "🌪️ Twist: ",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = chapter.disasterOrTwist,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Hook / Cliffhanger (High-Retention Highlight)
            if (chapter.hookOrCliffhanger.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Ending Hook / Cliffhanger:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = chapter.hookOrCliffhanger,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }
                    }
                }
            }

            // Scene beats preview
            if (chapter.sceneBeats.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scene Beats:\n${chapter.sceneBeats}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Est. ${chapter.wordCountEstimate} words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = onAskCoWriter,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Polish Beats & Cliffhanger", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun ChapterEditDialog(
    chapter: Chapter?,
    novelId: Long,
    defaultChapterNumber: Int,
    onDismiss: () -> Unit,
    onSave: (Chapter) -> Unit
) {
    var chapterNumber by remember { mutableStateOf((chapter?.chapterNumber ?: defaultChapterNumber).toString()) }
    var title by remember { mutableStateOf(chapter?.title ?: "") }
    var povCharacter by remember { mutableStateOf(chapter?.povCharacter ?: "") }
    var settingLocation by remember { mutableStateOf(chapter?.settingLocation ?: "") }
    var sceneGoal by remember { mutableStateOf(chapter?.sceneGoal ?: "") }
    var centralConflict by remember { mutableStateOf(chapter?.centralConflict ?: "") }
    var disasterOrTwist by remember { mutableStateOf(chapter?.disasterOrTwist ?: "") }
    var hookOrCliffhanger by remember { mutableStateOf(chapter?.hookOrCliffhanger ?: "") }
    var sceneBeats by remember { mutableStateOf(chapter?.sceneBeats ?: "") }
    var status by remember { mutableStateOf(chapter?.status ?: "Outlined") }
    var wordCountEstimate by remember { mutableStateOf((chapter?.wordCountEstimate ?: 2500).toString()) }
    var actualWordCount by remember { mutableStateOf((chapter?.actualWordCount ?: 0).toString()) }

    val statusOptions = listOf("Idea", "Outlined", "Drafting", "Polished", "Complete")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (chapter == null) "Outline New Chapter" else "Edit Chapter ${chapter.chapterNumber}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = chapterNumber,
                        onValueChange = { chapterNumber = it },
                        label = { Text("Ch #") },
                        modifier = Modifier.width(80.dp)
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Chapter Title *") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = povCharacter,
                        onValueChange = { povCharacter = it },
                        label = { Text("POV Character") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = settingLocation,
                        onValueChange = { settingLocation = it },
                        label = { Text("Setting / Location") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Status:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statusOptions.take(3).forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = sceneGoal,
                    onValueChange = { sceneGoal = it },
                    label = { Text("1. Scene Goal (What does POV character want?)") },
                    placeholder = { Text("e.g. Infiltrate the archives before the eclipse") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = centralConflict,
                    onValueChange = { centralConflict = it },
                    label = { Text("2. Central Conflict (What opposes them?)") },
                    placeholder = { Text("e.g. Alarm glyph triggers; guardian automaton awakens") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = disasterOrTwist,
                    onValueChange = { disasterOrTwist = it },
                    label = { Text("3. Disaster / Outcome (How does it go wrong?)") },
                    placeholder = { Text("e.g. Escapes, but the cylinder contains a death warrant") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hookOrCliffhanger,
                    onValueChange = { hookOrCliffhanger = it },
                    label = { Text("🔥 4. Hook / Cliffhanger Ending (Retention)") },
                    placeholder = { Text("Unresolved revelation, ticking clock, or immediate ambush") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = sceneBeats,
                    onValueChange = { sceneBeats = it },
                    label = { Text("Scene Beats (Step-by-Step Progression)") },
                    placeholder = { Text("• Beat 1: Arrival\n• Beat 2: Tension rises\n• Beat 3: Twist") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wordCountEstimate,
                        onValueChange = { wordCountEstimate = it },
                        label = { Text("Est. Words") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = actualWordCount,
                        onValueChange = { actualWordCount = it },
                        label = { Text("Drafted Words") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val chNum = chapterNumber.toIntOrNull() ?: defaultChapterNumber
                        val estWords = wordCountEstimate.toIntOrNull() ?: 2500
                        val actWords = actualWordCount.toIntOrNull() ?: 0

                        onSave(
                            (chapter ?: Chapter(novelId = novelId, chapterNumber = chNum, title = title)).copy(
                                chapterNumber = chNum,
                                title = title,
                                povCharacter = povCharacter,
                                settingLocation = settingLocation,
                                sceneGoal = sceneGoal,
                                centralConflict = centralConflict,
                                disasterOrTwist = disasterOrTwist,
                                hookOrCliffhanger = hookOrCliffhanger,
                                sceneBeats = sceneBeats,
                                status = status,
                                wordCountEstimate = estWords,
                                actualWordCount = actWords
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save Chapter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
