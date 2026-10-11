package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.model.WorldItem
import com.example.ui.NovelViewModel

@Composable
fun WorldBuildingScreen(
    viewModel: NovelViewModel,
    onNavigateToCoWriter: (prompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentNovel by viewModel.currentNovel.collectAsState()
    val worldItems by viewModel.worldItems.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var itemToEdit by remember { mutableStateOf<WorldItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "All",
        "Setting & Location",
        "Magic / Tech System",
        "Faction & Society",
        "Lore & History",
        "Relic & Artifact"
    )

    val filteredItems = remember(worldItems, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") worldItems
        else worldItems.filter { it.category == selectedCategoryFilter }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = category },
                        label = { Text(category, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No lore entries in this category.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + to add a setting, magic rules, faction, or historical event to your story bible.",
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
                    items(filteredItems, key = { it.id }) { item ->
                        WorldItemCard(
                            item = item,
                            onEdit = { itemToEdit = item },
                            onDelete = { viewModel.deleteWorldItem(item) },
                            onAskCoWriter = {
                                onNavigateToCoWriter("Please stress-test this lore entry: '${item.name}' [${item.category}] for my novel '${currentNovel?.title}'. Summary: ${item.summary}. Established Rules & Limitations: ${item.rulesAndLimitations}. Costs: ${item.costsAndConsequences}. Are there any plot holes or vulnerabilities?")
                            }
                        )
                    }
                }
            }
        }

        // Add lore item FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_world_item_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Lore Item")
        }
    }

    if (showAddDialog || itemToEdit != null) {
        val existing = itemToEdit
        WorldItemEditDialog(
            item = existing,
            novelId = currentNovel?.id ?: 1L,
            onDismiss = {
                showAddDialog = false
                itemToEdit = null
            },
            onSave = { updated ->
                viewModel.saveWorldItem(updated)
                showAddDialog = false
                itemToEdit = null
            }
        )
    }
}

@Composable
fun WorldItemCard(
    item: WorldItem,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Hard Rules & Limitations (Sanderson's First Law)
            if (item.rulesAndLimitations.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Hard Rules & Limitations (Plot Hole Prevention):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = item.rulesAndLimitations,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Costs & Consequences
            if (item.costsAndConsequences.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚖️ Cost / Price: ${item.costsAndConsequences}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Sensory details
            if (item.sensoryDetails.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🌫️ Atmosphere: ${item.sensoryDetails}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Secrets & Foreshadowing
            if (item.secretsAndForeshadowing.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🤫 Hidden Secret: ${item.secretsAndForeshadowing}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onAskCoWriter,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Stress-Test with Co-Writer", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun WorldItemEditDialog(
    item: WorldItem?,
    novelId: Long,
    onDismiss: () -> Unit,
    onSave: (WorldItem) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "Setting & Location") }
    var summary by remember { mutableStateOf(item?.summary ?: "") }
    var rulesAndLimitations by remember { mutableStateOf(item?.rulesAndLimitations ?: "") }
    var costsAndConsequences by remember { mutableStateOf(item?.costsAndConsequences ?: "") }
    var sensoryDetails by remember { mutableStateOf(item?.sensoryDetails ?: "") }
    var secretsAndForeshadowing by remember { mutableStateOf(item?.secretsAndForeshadowing ?: "") }

    val categoryOptions = listOf(
        "Setting & Location",
        "Magic / Tech System",
        "Faction & Society",
        "Lore & History",
        "Relic & Artifact"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "New World-Building Entry" else "Edit: ${item.name}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name / Concept *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categoryOptions) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Description & Summary *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = rulesAndLimitations,
                    onValueChange = { rulesAndLimitations = it },
                    label = { Text("Hard Rules & Limitations (Crucial)") },
                    placeholder = { Text("What can this NEVER do? What are the boundaries?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = costsAndConsequences,
                    onValueChange = { costsAndConsequences = it },
                    label = { Text("Costs, Risks & Consequences") },
                    placeholder = { Text("What price must be paid to use or maintain this?") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sensoryDetails,
                    onValueChange = { sensoryDetails = it },
                    label = { Text("Sensory Details & Atmosphere") },
                    placeholder = { Text("Smell, sound, lighting, weather, tactile feel") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = secretsAndForeshadowing,
                    onValueChange = { secretsAndForeshadowing = it },
                    label = { Text("Hidden Secrets & Foreshadowing (For Author Eyes)") },
                    placeholder = { Text("Unrevealed truth to payoff in later arcs") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            (item ?: WorldItem(novelId = novelId, category = category, name = name, summary = summary)).copy(
                                name = name,
                                category = category,
                                summary = summary,
                                rulesAndLimitations = rulesAndLimitations,
                                costsAndConsequences = costsAndConsequences,
                                sensoryDetails = sensoryDetails,
                                secretsAndForeshadowing = secretsAndForeshadowing
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save Lore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
