package com.example

import com.example.data.db.InitialData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun starterData_isPopulated() {
        assertEquals("Aetherium: The Clockwork Veil", InitialData.starterNovel.title)
        assertTrue(InitialData.starterCharacters.isNotEmpty())
        assertTrue(InitialData.starterWorldItems.isNotEmpty())
        assertTrue(InitialData.starterChapters.isNotEmpty())
    }

    @Test
    fun frameworkTemplates_areValid() {
        val threeActBeats = InitialData.getBeatsForFramework(1L, "3-Act Structure")
        assertEquals(8, threeActBeats.size)

        val heroBeats = InitialData.getBeatsForFramework(1L, "Hero's Journey")
        assertEquals(12, heroBeats.size)

        val webNovelBeats = InitialData.getBeatsForFramework(1L, "Web Novel Arc")
        assertEquals(7, webNovelBeats.size)
    }
}
