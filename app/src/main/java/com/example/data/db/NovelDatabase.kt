package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.NovelDao
import com.example.data.model.Chapter
import com.example.data.model.Character
import com.example.data.model.ChatMessage
import com.example.data.model.Novel
import com.example.data.model.PlotBeat
import com.example.data.model.WorldItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Novel::class,
        PlotBeat::class,
        Character::class,
        WorldItem::class,
        Chapter::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NovelDatabase : RoomDatabase() {

    abstract fun novelDao(): NovelDao

    companion object {
        @Volatile
        private var INSTANCE: NovelDatabase? = null

        fun getInstance(context: Context): NovelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NovelDatabase::class.java,
                    "novelcraft_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).novelDao()
                                dao.insertNovel(InitialData.starterNovel)
                                val beats = InitialData.getBeatsForFramework(1L, "3-Act Structure")
                                dao.insertPlotBeats(beats)
                                InitialData.starterCharacters.forEach { dao.insertCharacter(it) }
                                InitialData.starterWorldItems.forEach { dao.insertWorldItem(it) }
                                InitialData.starterChapters.forEach { dao.insertChapter(it) }
                                InitialData.starterChatMessages.forEach { dao.insertChatMessage(it) }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
