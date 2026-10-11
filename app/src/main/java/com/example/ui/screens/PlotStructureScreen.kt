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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.PlotBeat
import com.example.ui.NovelViewModel

@Composable
fun PlotStructureScreen(
    viewModel: NovelViewModel,
    onNavigateToCoWriter: (prompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentNovel by viewModel.currentNovel.collectAsState()
    val plotBeats by viewModel.plotBeats.collectAsState()

    var showFrameworkDialog by remember { mutableStateOf(false) }
    var selectedBeatForEdit by remember { mutableStateOf<PlotBeat?>(null) }
    var showAddBeatDialog by remember { mutableStateOf(false) }

    val frameworks = listOf("3-Act Structure", "Hero's Journey", "Web Novel Arc")
    val completedCount = plotBeats.count { it.isCompleted }
    val totalCount = plotBeats.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    val beatsByAct = remember(plotBeats) {
        plotBeats.groupBy { it.act }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Framework Selector and Progress Header
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Structure Framework",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currentNovel?.structureType ?: "3-Act Structure",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        OutlinedButton(
                            onClick = { showFrameworkDialog = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Switch Template", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Plot Progress: $completedCount of $totalCount Beats",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }

            // List of Acts and Beats
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                beatsByAct.forEach { (actName, beatsInAct) ->
                    item {
                        Text(
                            text = actName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }

                    items(beatsInAct, key = { it.id }) { beat ->
                        PlotBeatCard(
                            beat = beat,
                            onToggleCompleted = { viewModel.toggleBeatCompleted(beat) },
                            onEdit = { selectedBeatForEdit = beat },
                            onAskCoWriter = {
                                onNavigateToCoWriter("Help me brainstorm and structure the beat '${beat.beatName}' (${beat.act}) for my novel '${currentNovel?.title}'. The guide is: ${beat.description}. Current notes: ${beat.userNotes}")
                            },
                            onDelete = { viewModel.deletePlotBeat(beat) }
                        )
                    }
                }
            }
        }

        // Add custom beat FAB
        FloatingActionButton(
            onClick = { showAddBeatDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_plot_beat_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Beat")
        }
    }

    // Switch Framework Dialog
    if (showFrameworkDialog) {
        AlertDialog(
            onDismissRequest = { showFrameworkDialog = false },
            title = { Text("Choose Story Structure") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Selecting a new framework will populate proven narrative milestone beats for your novel:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    frameworks.forEach { fw ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (fw == currentNovel?.structureType)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.switchPlotFramework(fw)
                                    showFrameworkDialog = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = fw,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (fw == currentNovel?.structureType)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (fw) {
                                        "3-Act Structure" -> "Classic 8-sequence model: Inciting incident, Plot Point 1, Midpoint, Dark Night, Climax."
                                        "Hero's Journey" -> "Monomyth: 12 stages from Ordinary World to Supreme Ordeal and Resurrection."
                                        else -> "Web Novel Serial Arc: High-retention serial format with rapid escalation and hook payoff."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFrameworkDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Edit Plot Beat Dialog
    if (selectedBeatForEdit != null) {
        val beat = selectedBeatForEdit!!
        var notesText by remember { mutableStateOf(beat.userNotes) }

        AlertDialog(
            onDismissRequest = { selectedBeatForEdit = null },
            title = { Text("Edit Beat: ${beat.beatName}") },
            text = {
                Column {
                    Text(
                        text = "Guideline: ${beat.description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Your Story Plot Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updatePlotBeat(beat.copy(userNotes = notesText))
                    selectedBeatForEdit = null
                }) {
                    Text("Save Notes")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBeatForEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Plot Beat Dialog
    if (showAddBeatDialog) {
        var actName by remember { mutableStateOf("Act 2: The Confrontation") }
        var beatTitle by remember { mutableStateOf("") }
        var beatDesc by remember { mutableStateOf("") }
        var beatNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddBeatDialog = false },
            title = { Text("Add Custom Plot Beat") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = actName,
                        onValueChange = { actName = it },
                        label = { Text("Act Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = beatTitle,
                        onValueChange = { beatTitle = it },
                        label = { Text("Beat Title (e.g. Rival Ambush)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = beatDesc,
                        onValueChange = { beatDesc = it },
                        label = { Text("Narrative Guideline") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = beatNotes,
                        onValueChange = { beatNotes = it },
                        label = { Text("Your Plot Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (beatTitle.isNotBlank()) {
                            viewModel.addPlotBeat(actName, beatTitle, beatDesc, beatNotes)
                            showAddBeatDialog = false
                        }
                    },
                    enabled = beatTitle.isNotBlank()
                ) {
                    Text("Add Beat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBeatDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlotBeatCard(
    beat: PlotBeat,
    onToggleCompleted: () -> Unit,
    onEdit: () -> Unit,
    onAskCoWriter: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (beat.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = beat.isCompleted,
                    onCheckedChange = { onToggleCompleted() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = beat.beatName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = if (beat.isCompleted)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        if (beat.targetPercentage.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = beat.targetPercentage,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = beat.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Notes",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (beat.userNotes.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp, top = 6.dp, end = 4.dp)
                        .clickable { onEdit() }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "✍️ Author Notes:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = beat.userNotes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp, top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAskCoWriter,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Brainstorm with Co-Writer", style = MaterialTheme.typography.labelSmall)
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Beat",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
