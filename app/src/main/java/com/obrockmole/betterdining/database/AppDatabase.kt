package com.obrockmole.betterdining.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FavoriteItem::class, RenamedItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteItemDao(): FavoriteItemDao
    abstract fun renamedItemDao(): RenamedItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE `renamed_items` (`itemId` TEXT NOT NULL, `customName` TEXT NOT NULL, PRIMARY KEY(`itemId`))")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "better_purdue_dining_database"
                ).addMigrations(MIGRATION_1_2).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
