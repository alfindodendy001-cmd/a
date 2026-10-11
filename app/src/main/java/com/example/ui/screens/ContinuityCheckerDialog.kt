package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.NovelViewModel

@Composable
fun ContinuityCheckerDialog(
    viewModel: NovelViewModel,
    onNavigateToCoWriter: (prompt: String) -> Unit,
    onDismiss: () -> Unit
) {
    val novel by viewModel.currentNovel.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val characters by viewModel.characters.collectAsState()
    val lore by viewModel.worldItems.collectAsState()
    val beats by viewModel.plotBeats.collectAsState()

    // Continuity audits
    val chaptersWithoutCliffhanger = chapters.filter { it.hookOrCliffhanger.isBlank() }
    val charactersWithoutFlaw = characters.filter { it.fatalFlaw.isBlank() || it.theLie.isBlank() }
    val magicWithoutLimitations = lore.filter {
        it.category.contains("Magic") && (it.rulesAndLimitations.isBlank() || it.costsAndConsequences.isBlank())
    }
    val uncompletedBeats = beats.filter { !it.isCompleted }

    val totalIssues = chaptersWithoutCliffhanger.size + charactersWithoutFlaw.size + magicWithoutLimitations.size

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (totalIssues == 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (totalIssues == 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Story Continuity & Plot Hole Audit",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Score card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (totalIssues == 0)
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (totalIssues == 0)
                                "✅ Robust Narrative Integrity"
                            else
                                "⚠️ $totalIssues Potential Plot Holes / Pacing Gaps Found",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (totalIssues == 0) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Checking against industry best practices: Sanderson's laws of magic, character arc lies, and chapter retention cliffhangers.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Audit 1: Chapter Cliffhangers
                AuditCheckItem(
                    title = "Chapter Retention & Cliffhangers",
                    isWarning = chaptersWithoutCliffhanger.isNotEmpty(),
                    detail = if (chaptersWithoutCliffhanger.isEmpty())
                        "All ${chapters.size} chapters contain a high-stakes hook or cliffhanger."
                    else
                        "${chaptersWithoutCliffhanger.size} chapters are missing an ending cliffhanger: " +
                                chaptersWithoutCliffhanger.joinToString { "Ch ${it.chapterNumber}" }
                )

                // Audit 2: Character Fatal Flaws & Lies
                AuditCheckItem(
                    title = "Character Arc Depth (Fatal Flaws & Lies)",
                    isWarning = charactersWithoutFlaw.isNotEmpty(),
                    detail = if (charactersWithoutFlaw.isEmpty())
                        "All ${characters.size} characters have defined internal flaws and core lies."
                    else
                        "${charactersWithoutFlaw.size} characters lack a fatal flaw or internal lie: " +
                                charactersWithoutFlaw.joinToString { it.name }
                )

                // Audit 3: Magic / Tech Hard Limitations
                AuditCheckItem(
                    title = "Magic & Power System Boundaries",
                    isWarning = magicWithoutLimitations.isNotEmpty(),
                    detail = if (magicWithoutLimitations.isEmpty())
                        "Established magic systems have hard limits and costs to avoid plot armor."
                    else
                        "${magicWithoutLimitations.size} systems lack strict limits or backlash costs: " +
                                magicWithoutLimitations.joinToString { it.name }
                )

                // Audit 4: Plot Beats Progress
                AuditCheckItem(
                    title = "Plot Milestone Completion",
                    isWarning = uncompletedBeats.size > beats.size / 2,
                    detail = "${beats.size - uncompletedBeats.size} of ${beats.size} framework beats locked in."
                )

                if (totalIssues > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    FilledTonalButton(
                        onClick = {
                            onDismiss()
                            onNavigateToCoWriter(
                                "Please help me resolve continuity and plot hole vulnerabilities in '${novel?.title}':\n" +
                                        "- Chapters needing cliffhangers: ${chaptersWithoutCliffhanger.joinToString { "Ch ${it.chapterNumber}: ${it.title}" }}\n" +
                                        "- Characters needing fatal flaws: ${charactersWithoutFlaw.joinToString { it.name }}\n" +
                                        "- Magic systems needing limits: ${magicWithoutLimitations.joinToString { it.name }}\n" +
                                        "How should I fix these to maximize reader tension?"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fix with Co-Writer", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close Audit")
            }
        }
    )
}

@Composable
fun AuditCheckItem(
    title: String,
    isWarning: Boolean,
    detail: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(18.dp).padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
