package com.hsact.taxilog.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hsact.taxilog.data.db.converters.Converters
import com.hsact.taxilog.data.db.entities.CarSnapshotEntity
import com.hsact.taxilog.data.db.entities.DateTimePeriodEntity
import com.hsact.taxilog.data.db.entities.ShiftEntity
import com.hsact.taxilog.data.db.entities.ShiftMoneyEntity
import com.hsact.taxilog.data.db.entities.ShiftTimeEntity

@Database(
    entities = [
        ShiftEntity::class,
        CarSnapshotEntity::class,
        ShiftTimeEntity::class,
        ShiftMoneyEntity::class,
        DateTimePeriodEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shiftDao(): ShiftDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "taxilog_db"
            ).build()
        }
    }
}