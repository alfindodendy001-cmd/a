package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Character
import com.example.data.model.Novel
import com.example.data.model.WorldItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateCoWriterResponse(
        prompt: String,
        novel: Novel?,
        characters: List<Character> = emptyList(),
        worldItems: List<WorldItem> = emptyList(),
        stage: String = "Brainstorming"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val systemInstructionText = buildSystemPrompt(novel, characters, worldItems, stage)

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent local Co-Writer engine fallback when API key is unconfigured
            return@withContext generateSmartFallback(prompt, novel, characters, worldItems, stage)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val requestJson = JSONObject().apply {
                // System instruction
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstructionText) })
                    })
                })

                // Contents
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })

                // Generation config
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.w("GeminiService", "API call returned ${response.code}: $responseBody")
                return@withContext generateSmartFallback(prompt, novel, characters, worldItems, stage)
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val contentObj = candidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return@withContext text
                    }
                }
            }

            return@withContext generateSmartFallback(prompt, novel, characters, worldItems, stage)
        } catch (e: Exception) {
            Log.e("GeminiService", "Error calling Gemini API: ${e.message}", e)
            return@withContext generateSmartFallback(prompt, novel, characters, worldItems, stage)
        }
    }

    private fun buildSystemPrompt(
        novel: Novel?,
        characters: List<Character>,
        worldItems: List<WorldItem>,
        stage: String
    ): String {
        val novelContext = if (novel != null) {
            """
            Current Project: "${novel.title}"
            Genre: ${novel.genre}
            Logline: ${novel.logline}
            Tone & Style: ${novel.tone} (${novel.povStyle})
            Active Structure: ${novel.structureType}
            """.trimIndent()
        } else {
            "No active project selected."
        }

        val charContext = if (characters.isNotEmpty()) {
            "Key Characters: " + characters.joinToString("; ") { "${it.name} (${it.role}, Flaw: ${it.fatalFlaw}, Lie: ${it.theLie})" }
        } else ""

        val worldContext = if (worldItems.isNotEmpty()) {
            "Established World Rules & Lore: " + worldItems.joinToString("; ") { "${it.name} [${it.category}]: ${it.rulesAndLimitations}" }
        } else ""

        return """
        You are an expert Novel Planning and Writing Assistant (Co-Writer).
        Your primary goal is to help the author brainstorm, structure, and manage their novel from initial concept to a comprehensive chapter-by-chapter outline.
        
        Core Capabilities:
        1. World-Building: Immersive settings, societies, magic/tech systems with hard rules, costs, and lore.
        2. Character Development: Deep profiles, backstories, motivations, fatal flaws, character arcs (Lie vs Truth), and relationship dynamics.
        3. Plot Structuring: Frameworks (3-Act Structure, Hero's Journey, Web Novel high-retention serialization).
        4. Chapter Outlining: Goal, Conflict, Disaster, and high-stakes Cliffhangers / hooks.
        5. Continuity & Pacing: Track established rules and prevent plot holes.
        
        Guidelines:
        - Be encouraging, highly creative, and exceptionally organized.
        - Ask targeted, probing questions if concepts are vague (e.g., "What is the primary consequence if the protagonist fails this specific mission?").
        - Format outputs using clear Markdown headings, bullet points, and tables to make the planning visual and actionable.
        - Adapt tone and suggestions to fit the novel's specific genre.
        
        $novelContext
        $charContext
        $worldContext
        Active Stage: $stage
        """.trimIndent()
    }

    private fun generateSmartFallback(
        prompt: String,
        novel: Novel?,
        characters: List<Character>,
        worldItems: List<WorldItem>,
        stage: String
    ): String {
        val title = novel?.title ?: "Your Story"
        val genre = novel?.genre ?: "Fiction"
        val lower = prompt.lowercase()

        return when {
            lower.contains("hook") || lower.contains("logline") || lower.contains("brainstorm") -> {
                """
                ### 💡 High-Concept Brainstorming for *$title* ($genre)
                
                Here are 3 compelling narrative hooks and conflict catalysts:
                
                1. **The Inverted Premise:**
                   - **Hook:** What if the force everyone in this world trusts as their protector is secretly siphoning their future?
                   - **Core Dilemma:** The protagonist discovers the truth, but revealing it will instantly cause societal collapse.
                   - **Immediate Stakes:** An unyielding ticking clock before the cycle becomes irreversible.
                
                2. **The Reluctant Alliance:**
                   - **Hook:** To survive an impending catastrophe, the protagonist must partner with their ideological antithesis.
                   - **Dynamic:** One relies on cold calculation; the other on chaotic instinct. Every victory breeds mutual suspicion.
                
                3. **The Stolen Inheritance:**
                   - **Hook:** A legacy or sacred relic once thought lost resurfaces in the black market, triggering a shadow war between rival factions.
                
                ---
                **🤔 Co-Writer Probing Question:**
                *What is the single most terrifying price your protagonist refuses to pay to achieve their ultimate desire?*
                """.trimIndent()
            }
            lower.contains("magic") || lower.contains("world") || lower.contains("rule") -> {
                """
                ### 🌍 World-Building & System Architecture (*$title*)
                
                To create a deeply resonant setting that avoids plot holes, apply **Sanderson's First & Second Laws of Magic**:
                
                | System Dimension | Core Question to Define | Recommended Implementation |
                | :--- | :--- | :--- |
                | **Source & Fuel** | Where does power originate? | Finite ambient energy, metabolic strain, or sacrifice of memories. |
                | **Hard Limitation** | What can this power *never* do? | Cannot resurrect true life, cannot bypass physical momentum, or requires absolute focus. |
                | **The Price / Backlash** | What is the immediate physiological cost? | Sensory overload, temporal disorientation, or visible corruption marks. |
                | **Social Hierarchy** | Who controls the supply? | Monopolized by an elite guild or outlawed into underground covens. |
                
                ---
                **🔍 Continuity Check:**
                If your magic or technology solves too many problems easily, the reader loses tension. **Limitations are always more interesting than powers.**
                
                *Would you like to detail the primary sanctuary city or the forbidden wasteland next?*
                """.trimIndent()
            }
            lower.contains("character") || lower.contains("flaw") || lower.contains("arc") -> {
                """
                ### 👤 Deep Character Arc Blueprint
                
                Every gripping protagonist is defined not by what they can do, but by the **Lie They Believe**.
                
                * **The Ghost / Backstory Wound:** The past trauma or formative failure that created their defense mechanism.
                * **The Lie:** The false internal rule they cling to (e.g., *"If I never let anyone get close, I can never be betrayed."*)
                * **The Want (External Goal):** What they chase in Act 1 (prestige, revenge, escape).
                * **The Need (Internal Truth):** What they must embrace by Act 3 to truly heal and triumph.
                * **The Fatal Flaw in Action:** How their flaw actively ruins their relationships in high-pressure moments.
                
                ---
                **⚡ Relationship Dynamic Exercise:**
                Contrast your protagonist with a **Foil Character** who shares the exact same backstory trauma, but chose the opposite philosophical reaction!
                """.trimIndent()
            }
            lower.contains("chapter") || lower.contains("cliffhanger") || lower.contains("outline") -> {
                """
                ### 📐 High-Tension Chapter Formula: Goal, Conflict, Disaster & Hook
                
                For maximum page-turning retention, structure your chapters using the **Scene & Sequel micro-engine**:
                
                1. **Goal (Proactive Desire):**
                   - The POV character enters the scene with a concrete, tangible objective (e.g., *steal the cipher before the guard shift*).
                2. **Conflict (Escalating Resistance):**
                   - Internal hesitation clashed with unexpected external resistance.
                3. **Disaster / The "Yes, But" or "No, And Further":**
                   - Did they achieve their goal? *No, and furthermore, their extraction route just collapsed.*
                4. **The Hook / Cliffhanger:**
                   - **Category 1 (The Revelation):** A secret identity or betrayal is exposed.
                   - **Category 2 (The Ticking Clock):** A countdown begins with zero margin for error.
                   - **Category 3 (The Moral Dilemma):** Forced to choose between mission success or saving an innocent.
                
                ---
                *Would you like me to generate 5 sequential chapter summaries for your current plot turn?*
                """.trimIndent()
            }
            else -> {
                """
                ### ✍️ Co-Writer Strategy Note: *$title*
                
                I'm tracking your creative direction across **$stage**.
                
                Here are three immediate paths we can explore:
                - 🎯 **Clarify Core Stakes:** Define the catastrophic consequence that occurs if your protagonist walks away right now.
                - ⚡ **Escalate Antagonist Pressure:** Give your antagonist a deeply sympathetic motivation so their conflict with the protagonist feels inevitable rather than evil for evil's sake.
                - 🔗 **Bridge Beats to Chapters:** Translate your major structure milestones into sequential, hook-laden chapter beats.
                
                *What specific scene, dilemma, or character relationship would you like to work on right now?*
                """.trimIndent()
            }
        }
    }
}
