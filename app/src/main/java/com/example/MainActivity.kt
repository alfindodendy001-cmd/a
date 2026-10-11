package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.NovelViewModel
import com.example.ui.components.NovelHeaderStats
import com.example.ui.components.NovelTopBar
import com.example.ui.screens.CharactersScreen
import com.example.ui.screens.ChaptersScreen
import com.example.ui.screens.CoWriterScreen
import com.example.ui.screens.ContinuityCheckerDialog
import com.example.ui.screens.PlotStructureScreen
import com.example.ui.screens.ProjectSelectorDialog
import com.example.ui.screens.StoryBibleDialog
import com.example.ui.screens.WorldBuildingScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NovelCraftApp()
            }
        }
    }
}

@Composable
fun NovelCraftApp(viewModel: NovelViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentNovel by viewModel.currentNovel.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val characters by viewModel.characters.collectAsState()
    val worldItems by viewModel.worldItems.collectAsState()

    var showStoryBibleDialog by remember { mutableStateOf(false) }
    var showContinuityDialog by remember { mutableStateOf(false) }
    var showProjectSelectorDialog by remember { mutableStateOf(false) }

    // Handle back button: if on a secondary tab, return to Co-Writer tab (Tab 0)
    BackHandler(enabled = selectedTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                NovelTopBar(
                    novel = currentNovel,
                    onOpenProjectSelector = { showProjectSelectorDialog = true },
                    onOpenStoryBible = { showStoryBibleDialog = true },
                    onOpenContinuityCheck = { showContinuityDialog = true }
                )
                // Stats summary header for active novel
                if (selectedTab != 0) {
                    NovelHeaderStats(
                        novel = currentNovel,
                        chaptersCount = chapters.size,
                        characterCount = characters.size,
                        loreCount = worldItems.size
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("novel_bottom_navigation"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Co-Writer") },
                    label = { Text("Co-Writer") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_cowriter")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = "Plot & Beats") },
                    label = { Text("Plot Beats") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_plot")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Characters") },
                    label = { Text("Characters") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_characters")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.Public, contentDescription = "World Codex") },
                    label = { Text("World") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_world")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Chapters") },
                    label = { Text("Chapters") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_chapters")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Crossfade(targetState = selectedTab, label = "ScreenTransition") { tabIndex ->
                when (tabIndex) {
                    0 -> CoWriterScreen(viewModel = viewModel)
                    1 -> PlotStructureScreen(
                        viewModel = viewModel,
                        onNavigateToCoWriter = { prompt ->
                            viewModel.selectStage("Outlining")
                            viewModel.selectTab(0)
                            viewModel.sendMessageToCoWriter(prompt, "Outlining")
                        }
                    )
                    2 -> CharactersScreen(
                        viewModel = viewModel,
                        onNavigateToCoWriter = { prompt ->
                            viewModel.selectStage("Characters")
                            viewModel.selectTab(0)
                            viewModel.sendMessageToCoWriter(prompt, "Characters")
                        }
                    )
                    3 -> WorldBuildingScreen(
                        viewModel = viewModel,
                        onNavigateToCoWriter = { prompt ->
                            viewModel.selectStage("World-Building")
                            viewModel.selectTab(0)
                            viewModel.sendMessageToCoWriter(prompt, "World-Building")
                        }
                    )
                    4 -> ChaptersScreen(
                        viewModel = viewModel,
                        onNavigateToCoWriter = { prompt ->
                            viewModel.selectStage("Outlining")
                            viewModel.selectTab(0)
                            viewModel.sendMessageToCoWriter(prompt, "Outlining")
                        }
                    )
                }
            }
        }
    }

    // Story Bible Dialog
    if (showStoryBibleDialog) {
        StoryBibleDialog(
            viewModel = viewModel,
            onDismiss = { showStoryBibleDialog = false }
        )
    }

    // Continuity Checker Dialog
    if (showContinuityDialog) {
        ContinuityCheckerDialog(
            viewModel = viewModel,
            onNavigateToCoWriter = { prompt ->
                showContinuityDialog = false
                viewModel.selectStage("Continuity")
                viewModel.selectTab(0)
                viewModel.sendMessageToCoWriter(prompt, "Continuity")
            },
            onDismiss = { showContinuityDialog = false }
        )
    }

    // Project Switcher Dialog
    if (showProjectSelectorDialog) {
        ProjectSelectorDialog(
            viewModel = viewModel,
            onDismiss = { showProjectSelectorDialog = false }
        )
    }
}
