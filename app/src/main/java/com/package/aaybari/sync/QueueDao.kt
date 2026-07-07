package com.package.aaybari.sync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QueueDao {
    @Insert
    suspend fun insert(entry: QueueEntity): Long

    @Query("SELECT * FROM sync_queue WHERE status = :status ORDER BY created_at ASC LIMIT :limit")
    suspend fun getPending(status: String = "PENDING", limit: Int = 50): List<QueueEntity>

    @Update
    suspend fun update(entry: QueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Long)
}
