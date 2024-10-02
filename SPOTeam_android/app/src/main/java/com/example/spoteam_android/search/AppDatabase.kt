package com.example.spoteam_android.search

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room

@Database(entities = [SearchQuery::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchQueryDao(): SearchQueryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "search_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}