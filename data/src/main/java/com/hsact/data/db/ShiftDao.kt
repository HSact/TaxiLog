package com.hsact.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hsact.data.db.entities.ShiftEntity

@Dao
interface ShiftDao {

    @Query("SELECT * FROM shiftentity ORDER BY id DESC")
    suspend fun getAllShifts(): List<ShiftEntity>

    @Query("SELECT * FROM ShiftEntity WHERE (:start IS NULL OR period_start >= :start) AND (:end IS NULL OR period_end <= :end)")
    suspend fun getShiftsInRange(start: String?, end: String?): List<ShiftEntity>

    @Query("SELECT * FROM shiftentity WHERE id = :id LIMIT 1")
    suspend fun getShiftById(id: Int): ShiftEntity?

    @Query("SELECT * FROM shiftentity ORDER BY id DESC LIMIT 1")
    suspend fun getLastShift(): ShiftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: ShiftEntity)

    @Delete
    suspend fun deleteShift(shift: ShiftEntity)

    @Update
    suspend fun updateShift(shift: ShiftEntity)

    @Query("DELETE FROM shiftentity")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name='shiftentity'")
    suspend fun resetPrimaryKey()
}