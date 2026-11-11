package com.example.data.local

import androidx.room.*
import com.example.data.model.CalcHistory
import kotlinx.coroutines.flow.Flow


@Dao
interface CalcHistoryDao {
    @Query("SELECT * FROM calc_history ORDER BY timestampMillis DESC LIMIT 100")
    fun getHistory(): Flow<List<CalcHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CalcHistory)

    @Query("DELETE FROM calc_history")
    suspend fun clearHistory()
}

@Database(entities = [CalcHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calcHistoryDao(): CalcHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "toolbox_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
