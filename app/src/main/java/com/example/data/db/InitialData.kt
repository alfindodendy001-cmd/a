package com.example.data.db

import com.example.data.model.Chapter
import com.example.data.model.Character
import com.example.data.model.ChatMessage
import com.example.data.model.Novel
import com.example.data.model.PlotBeat
import com.example.data.model.WorldItem

object InitialData {

    val starterNovel = Novel(
        id = 1,
        title = "Aetherium: The Clockwork Veil",
        genre = "Fantasy / Steampunk",
        tagline = "In a city suspended by clockwork gods, truth is the most lethal gear.",
        logline = "When a disgraced chronomancer discovers that the floating metropolis of Oros is falling out of orbit, she must ally with an outlaw sky-corsair before the Clockwork Council sacrifices the lower ward to stay aloft.",
        targetWordCount = 90000,
        currentWordCount = 8500,
        status = "Outlining",
        structureType = "3-Act Structure",
        povStyle = "Third Person Limited (Dual POV)",
        tone = "Atmospheric, High Stakes, Clockpunk Intrigue"
    )

    fun getBeatsForFramework(novelId: Long, framework: String): List<PlotBeat> {
        return when (framework) {
            "Hero's Journey" -> listOf(
                PlotBeat(novelId = novelId, framework = framework, act = "Departure", beatName = "1. Ordinary World", targetPercentage = "1-10%", description = "Establish protagonist's baseline life, flaws, and what they lack.", orderIndex = 0),
                PlotBeat(novelId = novelId, framework = framework, act = "Departure", beatName = "2. Call to Adventure", targetPercentage = "10-12%", description = "An inciting event disrupts normal life with a challenge.", orderIndex = 1),
                PlotBeat(novelId = novelId, framework = framework, act = "Departure", beatName = "3. Refusal of the Call", targetPercentage = "12-15%", description = "Protagonist hesitates due to fear, duty, or previous trauma.", orderIndex = 2),
                PlotBeat(novelId = novelId, framework = framework, act = "Departure", beatName = "4. Meeting the Mentor", targetPercentage = "15-20%", description = "A guide imparts wisdom, training, or a critical tool.", orderIndex = 3),
                PlotBeat(novelId = novelId, framework = framework, act = "Departure", beatName = "5. Crossing the First Threshold", targetPercentage = "20-25%", description = "Protagonist leaves the familiar world and steps into the unknown.", orderIndex = 4),
                PlotBeat(novelId = novelId, framework = framework, act = "Initiation", beatName = "6. Tests, Allies & Enemies", targetPercentage = "25-50%", description = "New rules learned, companions recruited, rivals encountered.", orderIndex = 5),
                PlotBeat(novelId = novelId, framework = framework, act = "Initiation", beatName = "7. Approach to the Inmost Cave", targetPercentage = "50-60%", description = "Preparation for the central confrontation and highest danger.", orderIndex = 6),
                PlotBeat(novelId = novelId, framework = framework, act = "Initiation", beatName = "8. The Supreme Ordeal", targetPercentage = "60-75%", description = "A life-or-death confrontation with the greatest fear or foe.", orderIndex = 7),
                PlotBeat(novelId = novelId, framework = framework, act = "Initiation", beatName = "9. The Reward (Seizing the Sword)", targetPercentage = "75-80%", description = "Gaining the elixir, truth, or victory, but consequences loom.", orderIndex = 8),
                PlotBeat(novelId = novelId, framework = framework, act = "Return", beatName = "10. The Road Back", targetPercentage = "80-88%", description = "The antagonistic forces pursue or a second catastrophe emerges.", orderIndex = 9),
                PlotBeat(novelId = novelId, framework = framework, act = "Return", beatName = "11. Resurrection & Final Test", targetPercentage = "88-95%", description = "Final climactic test where the protagonist proves inner transformation.", orderIndex = 10),
                PlotBeat(novelId = novelId, framework = framework, act = "Return", beatName = "12. Return with the Elixir", targetPercentage = "95-100%", description = "New normal established; world or protagonist permanently changed.", orderIndex = 11)
            )
            "Web Novel Arc" -> listOf(
                PlotBeat(novelId = novelId, framework = framework, act = "Hook Arc", beatName = "1. The High-Concept Hook", targetPercentage = "Ch 1-3", description = "Instant shock/intrigue hook, unique cheat/power or dire predicament. Zero slow exposition.", orderIndex = 0),
                PlotBeat(novelId = novelId, framework = framework, act = "Hook Arc", beatName = "2. The Underdog's Reversal", targetPercentage = "Ch 4-7", description = "Oppressed or cornered protagonist uses hidden knowledge or daring gamble to shock adversaries.", orderIndex = 1),
                PlotBeat(novelId = novelId, framework = framework, act = "Ascent Arc", beatName = "3. Escalating Stakes & Rivals", targetPercentage = "Ch 8-15", description = "Success draws attention of higher-tier factions; power system rules tested.", orderIndex = 2),
                PlotBeat(novelId = novelId, framework = framework, act = "Ascent Arc", beatName = "4. The Crucible Dungeon/Trial", targetPercentage = "Ch 16-25", description = "High-pressure environment with high-tier rewards and life-threatening betrayal.", orderIndex = 3),
                PlotBeat(novelId = novelId, framework = framework, act = "Climax Arc", beatName = "5. The Catastrophic Betrayal", targetPercentage = "Ch 26-30", description = "The safety net snaps; an unexpected enemy reveals deeper conspiracy.", orderIndex = 4),
                PlotBeat(novelId = novelId, framework = framework, act = "Climax Arc", beatName = "6. Overwhelming Climax", targetPercentage = "Ch 31-38", description = "Protagonist unleashes mastered mechanic in high-stakes confrontation.", orderIndex = 5),
                PlotBeat(novelId = novelId, framework = framework, act = "Hook to Arc 2", beatName = "7. Mega Cliffhanger & Tier Breakthrough", targetPercentage = "Ch 39-40", description = "Triumphant arc wrap-up immediately shattered by revelation of a wider world or greater threat.", orderIndex = 6)
            )
            else -> listOf(
                // 3-Act Structure (Default)
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 1: The Setup (0% - 25%)",
                    beatName = "1. Opening Hook & Status Quo",
                    targetPercentage = "0% - 10%",
                    description = "Introduce the protagonist, the flawed world, the primary setting, and the burning dissatisfaction.",
                    userNotes = "Lyra calibrations at the Zenith Spire; clockwork gear slippage hides the city dropping 3 feet.",
                    orderIndex = 0,
                    isCompleted = true
                ),
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 1: The Setup (0% - 25%)",
                    beatName = "2. Inciting Incident",
                    targetPercentage = "10% - 12%",
                    description = "A destabilizing event turns the protagonist's world upside down; can no longer remain passive.",
                    userNotes = "Lyra's mentor is assassinated after handing her an unbroken silver chronometer with encrypted celestial coordinates.",
                    orderIndex = 1,
                    isCompleted = true
                ),
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 1: The Setup (0% - 25%)",
                    beatName = "3. Plot Point 1 / Point of No Return",
                    targetPercentage = "20% - 25%",
                    description = "Protagonist commits fully to the adventure. Crossing into Act 2; old life is closed off.",
                    userNotes = "Branded a traitor by the Clockwork Council, Lyra leaps onto Corvan's smuggler skyship to evade Enforcers.",
                    orderIndex = 2,
                    isCompleted = false
                ),
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 2: The Confrontation (25% - 75%)",
                    beatName = "4. Rising Obstacles & Subplot",
                    targetPercentage = "25% - 50%",
                    description = "Protagonist faces escalating trials, navigates new alliances, and uncovers deeper enemy motives.",
                    userNotes = "Navigating the undercity smog caverns; uneasy trust building between Lyra and Corvan over his sister's ransom.",
                    orderIndex = 3,
                    isCompleted = false
                ),
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 2: The Confrontation (25% - 75%)",
                    beatName = "5. The Midpoint Shift",
                    targetPercentage = "50%",
                    description = "Major revelation shifts protagonist from reactive survival to proactive counter-attack. The true stakes are revealed.",
                    userNotes = "Discovery that the Council is not trying to fix Oros—they intend to jettison the lower district into the abyss to save themselves.",
                    orderIndex = 4,
                    isCompleted = false
                ),
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 2: The Confrontation (25% - 75%)",
                    beatName = "6. Dark Night of the Soul",
                    targetPercentage = "70% - 75%",
                    description = "All seems lost. The plan collapses, an ally is captured or betrays, and protagonist confronts fatal flaw.",
                    userNotes = "Corvan's skyship is crippled; Lyra's chronometer is seized by Inquisitor Valen; despair sets in.",
                    orderIndex = 5,
                    isCompleted = false
                ),
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 3: The Resolution (75% - 100%)",
                    beatName = "7. Climax & Confrontation",
                    targetPercentage = "80% - 90%",
                    description = "The decisive battle. Protagonist applies lessons learned, overcomes flaw, and faces antagonist directly.",
                    userNotes = "Final battle atop the Grand Chrono-Pillar as the detachment gears begin grinding.",
                    orderIndex = 6,
                    isCompleted = false
                ),
                PlotBeat(
                    novelId = novelId,
                    framework = "3-Act Structure",
                    act = "Act 3: The Resolution (75% - 100%)",
                    beatName = "8. Resolution & New World Order",
                    targetPercentage = "90% - 100%",
                    description = "The dust settles. Show the transformed protagonist and the altered world state.",
                    userNotes = "Oros stabilizes at lower altitude; new alliance between Sky-Guild and Lower Wards; open horizons.",
                    orderIndex = 7,
                    isCompleted = false
                )
            )
        }
    }

    val starterCharacters = listOf(
        Character(
            id = 1,
            novelId = 1,
            name = "Lyra Vance",
            role = "Protagonist",
            archetype = "The Reluctant Seeker",
            motivation = "Restore her family's honor by mastering the Grand Orrery and uncovering the truth behind her father's exile.",
            need = "Accept that some broken systems must be dismantled rather than perfected.",
            fatalFlaw = "Paralyzing perfectionism and distrust of anyone outside her scientific sphere.",
            theLie = "\"If I follow the mathematical formulas flawlessly, no one I love will ever get hurt.\"",
            theTruth = "\"Courage means acting in the face of unpredictable chaos.\"",
            backstory = "Daughter of the exiled High Horologist. Apprenticed in secret under Master Caleb.",
            relationshipDynamics = "Tense friction with Corvan; mutual respect growing into protective loyalty.",
            physicalDescription = "Sharp amber eyes, ink-stained fingertips, brass-rimmed magnifying monocle tucked in leather coat.",
            tagColorHex = "#F59E0B"
        ),
        Character(
            id = 2,
            novelId = 1,
            name = "Corvan Hale",
            role = "Deuteragonist / Foil",
            archetype = "The Rogue with a Heart",
            motivation = "Secure enough aether fuel to buy out his crew's indentured debt from the Sky Syndicate.",
            need = "Find a cause worth sacrificing his cynical self-preservation for.",
            fatalFlaw = "Reckless cynicism; assumes everyone will sell him out first.",
            theLie = "\"Every person has a price, and loyalty is just an unpaid invoice.\"",
            theTruth = "\"Real freedom is standing shoulder-to-shoulder with people who won't abandon you.\"",
            backstory = "Former fleet pilot cashiered after refusing to bomb a striking miner enclave.",
            relationshipDynamics = "Bickers constantly with Lyra's rigid plans; acts as her shield during combat.",
            physicalDescription = "Tall, wind-burned face, aviator leather coat with patchwork repairs, twin magnetic grapple hooks.",
            tagColorHex = "#8B5CF6"
        ),
        Character(
            id = 3,
            novelId = 1,
            name = "Grand Inquisitor Valen",
            role = "Antagonist",
            archetype = "The Fanatical Order",
            motivation = "Preserve the floating sanctity of the Zenith Ward at whatever human cost necessary.",
            need = "Recognize that tyrannical control is not salvation.",
            fatalFlaw = "Total lack of empathy; views lower ward citizens as expendable ballast.",
            theLie = "\"A bleeding heart is a broken gear.\"",
            theTruth = "\"Sacrificing others to save oneself leaves nothing worth saving.\"",
            backstory = "Rose through the Clockwork Inquisition by exposing heretical research.",
            relationshipDynamics = "Hunts Lyra ruthlessly, seeing her as the embodiment of dangerous instability.",
            physicalDescription = "Porcelain-smooth countenance, immaculate gold-threaded vestments, aether-powered rapier.",
            tagColorHex = "#EF4444"
        )
    )

    val starterWorldItems = listOf(
        WorldItem(
            id = 1,
            novelId = 1,
            category = "Setting & Location",
            name = "Oros, The Floating Metropolis",
            summary = "A colossal tiered city suspended five thousand meters above the poisoned cloud-sea by eight gigantic Aether-Keels.",
            rulesAndLimitations = "Requires constant calibration of the central Clockwork Spine. Weight distribution must remain strictly balanced.",
            costsAndConsequences = "Lower Wards (The Rust Sinks) endure toxic exhaust runoff from the upper Zenith Ward.",
            sensoryDetails = "Rhythmic sub-bass thrum of seismic pistons, smell of ozone and hot brass oil, amber-tinted perpetual fog.",
            secretsAndForeshadowing = "The Aether-Keels are not mechanical creations—they are powered by slumbering Leviathan hearts."
        ),
        WorldItem(
            id = 2,
            novelId = 1,
            category = "Magic / Tech System",
            name = "Chronomancy & Gear-Weaving",
            summary = "The art of using resonant brass gears tuned to planetary frequencies to manipulate localized temporal dilation.",
            rulesAndLimitations = "Rule 1: Time cannot be rewound, only slowed or accelerated in a localized bubble. Rule 2: Conservation of Momentum—accelerated objects transfer accumulated kinetic backlash to the user.",
            costsAndConsequences = "Overuse causes 'Chronocirrhosis'—premature graying, micro-seizures, and desynchronization from real-time perception.",
            sensoryDetails = "High-pitched chiming sound, optical ripple like heat waves, scent of burnt cinnamon and cold ozone.",
            secretsAndForeshadowing = "A broken gear creates a permanent temporal fracture that cannot be healed."
        ),
        WorldItem(
            id = 3,
            novelId = 1,
            category = "Faction & Society",
            name = "The Clockwork Council",
            summary = "The autocratic governing body of Oros, composed of twelve High Horologists and Guild Masters.",
            rulesAndLimitations = "Governed by the Code of the First Gear. Any decree requires a three-quarters clock-vote.",
            costsAndConsequences = "Suppresses all technological innovation that threatens their monopoly on aether distribution.",
            sensoryDetails = "Echoing halls of black marble, ticking pendulums four stories high, silver-masked functionaries.",
            secretsAndForeshadowing = "They already know the city has only 48 days of altitude remaining."
        )
    )

    val starterChapters = listOf(
        Chapter(
            id = 1,
            novelId = 1,
            chapterNumber = 1,
            title = "The Gear That Wept Oil",
            povCharacter = "Lyra Vance",
            settingLocation = "The Zenith Orrery Spire",
            sceneGoal = "Complete the seasonal calibration of the 7th Aether-Keel without alerting Council overseers.",
            centralConflict = "The main gyroscope is shuddering violently; someone has tampered with the primary balance weights.",
            disasterOrTwist = "Lyra discovers her mentor dying in the gear chamber, his notebook shredded and his chronometer burning.",
            hookOrCliffhanger = "Master Caleb grips her wrist with his dying breath: 'It's not falling by accident, Lyra. They're dropping us on purpose.'",
            sceneBeats = "• Lyra climbs the exterior spire catwalk in freezing wind\n• Measures seismic frequency anomaly with her tuning fork\n• Enters Caleb's sanctum to find the bloody scene\n• Inquisitor sirens echo across the courtyard outside",
            wordCountEstimate = 3200,
            actualWordCount = 3200,
            status = "Drafting"
        ),
        Chapter(
            id = 2,
            novelId = 1,
            chapterNumber = 2,
            title = "Smuggler's Parallax",
            povCharacter = "Corvan Hale",
            settingLocation = "The Cloud-Docks of Rust Sinks",
            sceneGoal = "Secure a cargo hold full of contraband refined aether and slip past harbor patrols before dawn.",
            centralConflict = "Enforcers lockdown the perimeter, searching for a fugitive chronomancer matching Lyra's description.",
            disasterOrTwist = "A desperate young woman with an illegal chronometer drops from the crane straight onto Corvan's cargo deck.",
            hookOrCliffhanger = "Before Corvan can draw his pistol, Council searchlights pin the skyship, and an incendiary harpoon pierces their starboard balloon.",
            sceneBeats = "• Corvan inspects the valves on the *Aero-Vagabond*\n• Harbor Master demands extortionate bribe\n• Sudden explosion on upper sky-bridge creates panic\n• Lyra crash-lands into the hold with armed Enforcers in pursuit",
            wordCountEstimate = 2800,
            actualWordCount = 2800,
            status = "Drafting"
        ),
        Chapter(
            id = 3,
            novelId = 1,
            chapterNumber = 3,
            title = "Descent into the Smog-Well",
            povCharacter = "Lyra Vance",
            settingLocation = "The Lower Abyss under Oros",
            sceneGoal = "Repair the ruptured balloon while navigating the treacherous magnetic downdrafts beneath the city.",
            centralConflict = "Corvan wants to eject the cargo and Lyra to lighten ship; Lyra needs the cargo to power her temporal seal.",
            disasterOrTwist = "To seal the hull, Lyra uses an unauthorized temporal bubble, suffering kinetic backlash and revealing her identity.",
            hookOrCliffhanger = "As the ship levels out in the toxic under-cloud, Corvan stares at the glowing crest on her coat: 'You're Vance's daughter. You're the reason they're hunting my ship.'",
            sceneBeats = "• Terrifying freefall through jagged pipe clusters\n• Lyra uses chronomantic gear to freeze the tearing fabric\n• Corvan manages emergency burn on secondary thrusters\n• Confrontation in the engine room over secrets and survival",
            wordCountEstimate = 3100,
            actualWordCount = 2500,
            status = "Outlined"
        )
    )

    val starterChatMessages = listOf(
        ChatMessage(
            id = 1,
            novelId = 1,
            sender = "assistant",
            content = "👋 Greetings! I'm your Novel Planning & Co-Writing Assistant. What stage of planning are you currently in (brainstorming a new concept, world-building lore & magic, developing deep characters, or outlining chapter-by-chapter)?\n\nTell me about your core story concept or pick one of the quick actions below, and let's craft an unforgettable story together!",
            stage = "Brainstorming",
            timestamp = System.currentTimeMillis() - 60000
        )
    )
}
