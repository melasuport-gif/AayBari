package com.package.aaybari.sync

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [QueueEntity::class], version = 2, exportSchema = false)
abstract class SyncDatabase : RoomDatabase() {
    abstract fun queueDao(): QueueDao

    companion object {
        @Volatile
        private var INSTANCE: SyncDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add attempts column (default 0) and last_error column (nullable)
                database.execSQL("ALTER TABLE sync_queue ADD COLUMN attempts INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE sync_queue ADD COLUMN last_error TEXT")
            }
        }

        fun getInstance(context: Context): SyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyncDatabase::class.java,
                    "aaybari_sync_db"
                ).addMigrations(MIGRATION_1_2)
                 .fallbackToDestructiveMigrationOnDowngrade()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
