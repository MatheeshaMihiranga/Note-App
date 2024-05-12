package com.example.thetaskapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.thetaskapp.model.Task
import java.util.concurrent.locks.Lock

@Database(entities = [Task::class], version = 3)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: TaskDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: createDatabase(
                        context
                    ).also {
                        instance = it
                    }

            }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                "task_db"
            )
                .addMigrations(MIGRATION_1_2) // Migration from version 1 to 2
                .addMigrations(MIGRATION_2_3) // Migration from version 2 to 3
                .build()

        // Migration from version 1 to 2
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(Task: SupportSQLiteDatabase) {
                Task.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tasks_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `taskTitle` TEXT NOT NULL, `taskDesc` TEXT NOT NULL, `priority` TEXT NOT NULL)"
                )
                // Copy data from the old table to the new table
                Task.execSQL(
                    "INSERT INTO tasks_new (id, taskTitle, taskDesc, priority) SELECT id, taskTitle, taskDesc, '' AS priority FROM tasks"
                )
                // Drop the old table
                Task.execSQL("DROP TABLE IF EXISTS `tasks`")
                // Rename the new table to match the original table name
                Task.execSQL("ALTER TABLE `tasks_new` RENAME TO `tasks`")
                // For example, you can create a new table with the updated schema and copy data from the old table
            }
        }

        // Migration from version 2 to 3
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(Task: SupportSQLiteDatabase) {
                // Create a new table with the desired schema
                Task.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tasks_new` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`taskTitle` TEXT NOT NULL, " +
                            "`taskDesc` TEXT NOT NULL, " +
                            "`priority` TEXT NOT NULL, " +
                            "`date` TEXT NOT NULL" + // Add the missing 'date' column
                            ")"
                )
                // Copy data from the old table to the new table
                Task.execSQL(
                    "INSERT INTO tasks_new (id, taskTitle, taskDesc, priority, date) " +
                            "SELECT id, taskTitle, taskDesc, priority, '' AS date FROM tasks"
                )
                // Drop the old table
                Task.execSQL("DROP TABLE IF EXISTS `tasks`")
                // Rename the new table to match the original table name
                Task.execSQL("ALTER TABLE `tasks_new` RENAME TO `tasks`")
            }
        }

    }
}
