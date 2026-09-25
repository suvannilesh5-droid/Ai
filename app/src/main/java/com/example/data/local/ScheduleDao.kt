package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_config WHERE id = 1 LIMIT 1")
    fun getScheduleConfigFlow(): Flow<ScheduleConfigEntity?>

    @Query("SELECT * FROM schedule_config WHERE id = 1 LIMIT 1")
    suspend fun getScheduleConfig(): ScheduleConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: ScheduleConfigEntity)

    @Update
    suspend fun updateConfig(config: ScheduleConfigEntity)
}
