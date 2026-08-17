package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TelegramChatEntity::class,
        TelegramMediaEntity::class,
        ExportRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TeleVaultDatabase : RoomDatabase() {

    abstract fun teleVaultDao(): TeleVaultDao

    companion object {
        @Volatile
        private var INSTANCE: TeleVaultDatabase? = null

        fun getDatabase(context: Context): TeleVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TeleVaultDatabase::class.java,
                    "televault_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
