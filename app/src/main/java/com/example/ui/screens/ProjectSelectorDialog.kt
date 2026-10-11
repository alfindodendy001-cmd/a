package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Novel
import com.example.ui.NovelViewModel

@Composable
fun ProjectSelectorDialog(
    viewModel: NovelViewModel,
    onDismiss: () -> Unit
) {
    val allNovels by viewModel.allNovels.collectAsState()
    val currentNovel by viewModel.currentNovel.collectAsState()

    var showCreateNovelDialog by remember { mutableStateOf(false) }
    var novelToEdit by remember { mutableStateOf<Novel?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Novel Projects",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)
                )
                OutlinedButton(
                    onClick = { showCreateNovelDialog = true },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Novel", style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select an active novel binder to work on with your Co-Writer:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allNovels, key = { it.id }) { novel ->
                        val isSelected = novel.id == currentNovel?.id
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectNovel(novel.id)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = novel.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${novel.genre} • ${novel.status} (${novel.targetWordCount} words)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { novelToEdit = novel },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Novel Info",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                if (allNovels.size > 1) {
                                    IconButton(
                                        onClick = { viewModel.deleteNovel(novel) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Novel",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

    if (showCreateNovelDialog) {
        CreateNovelDialog(
            onDismiss = { showCreateNovelDialog = false },
            onCreate = { title, genre, tagline, logline, targetWords, structure ->
                viewModel.createNovel(title, genre, tagline, logline, targetWords, structure)
                showCreateNovelDialog = false
                onDismiss()
            }
        )
    }

    if (novelToEdit != null) {
        val novel = novelToEdit!!
        EditNovelDialog(
            novel = novel,
            onDismiss = { novelToEdit = null },
            onSave = { updated ->
                viewModel.updateNovel(updated)
                novelToEdit = null
            }
        )
    }
}

@Composable
fun CreateNovelDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, genre: String, tagline: String, logline: String, targetWords: Int, structure: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Fantasy") }
    var tagline by remember { mutableStateOf("") }
    var logline by remember { mutableStateOf("") }
    var targetWordsText by remember { mutableStateOf("80000") }
    var structure by remember { mutableStateOf("3-Act Structure") }

    val genres = listOf("Fantasy", "Sci-Fi", "Thriller", "LitRPG / Web Novel", "Romance", "Mystery", "Horror", "Historical")
    val structures = listOf("3-Act Structure", "Hero's Journey", "Web Novel Arc")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Novel Project") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Novel Title *") },
                    placeholder = { Text("e.g. The Obsidian Key") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Genre:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(genres) { g ->
                        FilterChip(
                            selected = genre == g,
                            onClick = { genre = g },
                            label = { Text(g, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Text("Plot Framework:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(structures) { s ->
                        FilterChip(
                            selected = structure == s,
                            onClick = { structure = s },
                            label = { Text(s, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Tagline / Hook") },
                    placeholder = { Text("In a world of silence, one whisper kills.") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = logline,
                    onValueChange = { logline = it },
                    label = { Text("Core Premise / Logline") },
                    placeholder = { Text("When [Inciting Incident], a [Protagonist] must [Goal] before [Catastrophe].") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = targetWordsText,
                    onValueChange = { targetWordsText = it },
                    label = { Text("Target Word Count") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val words = targetWordsText.toIntOrNull() ?: 80000
                        onCreate(title, genre, tagline, logline, words, structure)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create Novel")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditNovelDialog(
    novel: Novel,
    onDismiss: () -> Unit,
    onSave: (Novel) -> Unit
) {
    var title by remember { mutableStateOf(novel.title) }
    var genre by remember { mutableStateOf(novel.genre) }
    var tagline by remember { mutableStateOf(novel.tagline) }
    var logline by remember { mutableStateOf(novel.logline) }
    var status by remember { mutableStateOf(novel.status) }
    var targetWordsText by remember { mutableStateOf(novel.targetWordCount.toString()) }
    var currentWordsText by remember { mutableStateOf(novel.currentWordCount.toString()) }

    val statuses = listOf("Brainstorming", "World-Building", "Outlining", "Drafting", "Revision", "Completed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Novel Metadata") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Novel Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Production Status:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(statuses) { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Tagline") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = logline,
                    onValueChange = { logline = it },
                    label = { Text("Logline / Core Concept") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentWordsText,
                        onValueChange = { currentWordsText = it },
                        label = { Text("Current Words") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetWordsText,
                        onValueChange = { targetWordsText = it },
                        label = { Text("Target Words") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val targetW = targetWordsText.toIntOrNull() ?: novel.targetWordCount
                val currentW = currentWordsText.toIntOrNull() ?: novel.currentWordCount
                onSave(
                    novel.copy(
                        title = title,
                        genre = genre,
                        tagline = tagline,
                        logline = logline,
                        status = status,
                        targetWordCount = targetW,
                        currentWordCount = currentW
                    )
                )
            }) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
