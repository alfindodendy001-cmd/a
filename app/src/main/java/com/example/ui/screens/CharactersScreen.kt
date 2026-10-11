package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Character
import com.example.ui.NovelViewModel

@Composable
fun CharactersScreen(
    viewModel: NovelViewModel,
    onNavigateToCoWriter: (prompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentNovel by viewModel.currentNovel.collectAsState()
    val characters by viewModel.characters.collectAsState()

    var selectedRoleFilter by remember { mutableStateOf("All") }
    var characterToEdit by remember { mutableStateOf<Character?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val roles = listOf("All", "Protagonist", "Antagonist", "Deuteragonist / Foil", "Mentor", "Love Interest", "Supporting")

    val filteredCharacters = remember(characters, selectedRoleFilter) {
        if (selectedRoleFilter == "All") characters
        else characters.filter { it.role.contains(selectedRoleFilter, ignoreCase = true) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Role Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(roles) { role ->
                    val isSelected = selectedRoleFilter == role
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRoleFilter = role },
                        label = { Text(role, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            if (filteredCharacters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No characters in this category yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button below to create a deep profile with fatal flaw and character arc.",
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
                    items(filteredCharacters, key = { it.id }) { character ->
                        CharacterCard(
                            character = character,
                            onEdit = { characterToEdit = character },
                            onDelete = { viewModel.deleteCharacter(character) },
                            onAskCoWriter = {
                                onNavigateToCoWriter("Help me develop the character arc and fatal flaw for '${character.name}' (${character.role}, ${character.archetype}) in '${currentNovel?.title}'. Lie: ${character.theLie}. Truth needed: ${character.theTruth}. Flaw: ${character.fatalFlaw}. How can I test this flaw in high-stakes scenes?")
                            }
                        )
                    }
                }
            }
        }

        // Add character FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_character_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Character")
        }
    }

    if (showAddDialog || characterToEdit != null) {
        val existing = characterToEdit
        CharacterEditDialog(
            character = existing,
            novelId = currentNovel?.id ?: 1L,
            onDismiss = {
                showAddDialog = false
                characterToEdit = null
            },
            onSave = { updated ->
                viewModel.saveCharacter(updated)
                showAddDialog = false
                characterToEdit = null
            }
        )
    }
}

@Composable
fun CharacterCard(
    character: Character,
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
            // Header Row: Avatar, Name, Role badge, Edit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            try {
                                Color(android.graphics.Color.parseColor(character.tagColorHex))
                            } catch (e: Exception) {
                                MaterialTheme.colorScheme.primary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = character.name.take(1),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = character.role,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (character.archetype.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${character.archetype}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Character",
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

            // The Lie vs The Truth Arc
            if (character.theLie.isNotBlank() || character.theTruth.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (character.theLie.isNotBlank()) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "🔴 The Lie: ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = character.theLie,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (character.theTruth.isNotBlank()) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "🟢 The Truth Needed: ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = character.theTruth,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Fatal Flaw
            if (character.fatalFlaw.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Fatal Flaw: ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = character.fatalFlaw,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Motivation
            if (character.motivation.isNotBlank()) {
                Text(
                    text = "🎯 Desire / Motivation: ${character.motivation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Relationship Dynamics
            if (character.relationshipDynamics.isNotBlank()) {
                Text(
                    text = "🔗 Dynamics: ${character.relationshipDynamics}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Ask Co-Writer Button
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
                Text("Co-Writer Arc Brainstorm", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun CharacterEditDialog(
    character: Character?,
    novelId: Long,
    onDismiss: () -> Unit,
    onSave: (Character) -> Unit
) {
    var name by remember { mutableStateOf(character?.name ?: "") }
    var role by remember { mutableStateOf(character?.role ?: "Protagonist") }
    var archetype by remember { mutableStateOf(character?.archetype ?: "The Reluctant Hero") }
    var motivation by remember { mutableStateOf(character?.motivation ?: "") }
    var need by remember { mutableStateOf(character?.need ?: "") }
    var fatalFlaw by remember { mutableStateOf(character?.fatalFlaw ?: "") }
    var theLie by remember { mutableStateOf(character?.theLie ?: "") }
    var theTruth by remember { mutableStateOf(character?.theTruth ?: "") }
    var backstory by remember { mutableStateOf(character?.backstory ?: "") }
    var relationshipDynamics by remember { mutableStateOf(character?.relationshipDynamics ?: "") }
    var physicalDescription by remember { mutableStateOf(character?.physicalDescription ?: "") }

    val roleOptions = listOf("Protagonist", "Antagonist", "Deuteragonist / Foil", "Mentor", "Love Interest", "Supporting")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (character == null) "New Character Profile" else "Edit Character: ${character.name}") },
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
                    label = { Text("Character Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Role & Dynamic:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(roleOptions) { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = archetype,
                    onValueChange = { archetype = it },
                    label = { Text("Archetype (e.g. Reluctant Seeker, The Shadow)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fatalFlaw,
                    onValueChange = { fatalFlaw = it },
                    label = { Text("Fatal Flaw / Blindspot *") },
                    placeholder = { Text("e.g. Paralyzing cynicism, pride, fear of vulnerability") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = theLie,
                    onValueChange = { theLie = it },
                    label = { Text("The Lie They Believe (Character Arc Start)") },
                    placeholder = { Text("e.g. \"I can only depend on myself.\"") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = theTruth,
                    onValueChange = { theTruth = it },
                    label = { Text("The Truth They Must Learn (Arc Climax)") },
                    placeholder = { Text("e.g. \"True strength is vulnerable trust.\"") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = motivation,
                    onValueChange = { motivation = it },
                    label = { Text("External Want / Motivation") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = relationshipDynamics,
                    onValueChange = { relationshipDynamics = it },
                    label = { Text("Relationship Dynamics") },
                    placeholder = { Text("Tense rivalry with X; protective toward Y") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = backstory,
                    onValueChange = { backstory = it },
                    label = { Text("Backstory & Formative Wound") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val colorHex = when (role) {
                            "Protagonist" -> "#F59E0B"
                            "Antagonist" -> "#EF4444"
                            "Mentor" -> "#10B981"
                            "Love Interest" -> "#EC4899"
                            else -> "#8B5CF6"
                        }
                        onSave(
                            (character ?: Character(novelId = novelId, name = name, role = role)).copy(
                                name = name,
                                role = role,
                                archetype = archetype,
                                motivation = motivation,
                                need = need,
                                fatalFlaw = fatalFlaw,
                                theLie = theLie,
                                theTruth = theTruth,
                                backstory = backstory,
                                relationshipDynamics = relationshipDynamics,
                                physicalDescription = physicalDescription,
                                tagColorHex = colorHex
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
