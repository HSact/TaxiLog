package com.example.taxidrivercalculator.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE_COl + " TEXT," +
                TIME_COL + " REAL," +
                EARNINGS_COL + " REAL," +
                WASH_COL + " REAL," +
                FUEL_COL + " REAL," +
                MILEAGE_COL + " REAL," +
                PROFIT_COL + " REAL" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addShift(
        date: String, time: Double, earnings: Double, wash: Double,
        fuel: Double, mileage: Double, profit: Double) {

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(DATE_COl, date)
        values.put(TIME_COL, time)
        values.put(EARNINGS_COL, earnings)
        values.put(WASH_COL, wash)
        values.put(FUEL_COL, fuel)
        values.put(MILEAGE_COL, mileage)
        values.put(PROFIT_COL, profit)

        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun recreateDB(shifts: MutableList<Shift>) {
        clearTable()
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            for (currentShift in shifts) {
                val values = ContentValues().apply {
                    put(DATE_COl, currentShift.date)
                    put(TIME_COL, currentShift.time.toDouble())
                    put(EARNINGS_COL, currentShift.earnings)
                    put(WASH_COL, currentShift.wash)
                    put(FUEL_COL, currentShift.fuelCost)
                    put(MILEAGE_COL, currentShift.mileage)
                    put(PROFIT_COL, currentShift.profit)
                }
                db.insert(TABLE_NAME, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    private fun clearTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='$TABLE_NAME'")
        db.close()
    }

    fun getShift(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    fun deleteShift(index: Int) {
        val db = this.writableDatabase
        val shifts = ShiftHelper.makeArray(this)
        shifts.removeAt(index-1)
        recreateDB(shifts)
        db.close()
    }

    fun deleteAll() {
        recreateDB(mutableListOf())
    }

    companion object {
        private val DATABASE_NAME = "SHIFTS"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "shifts_table"
        val ID_COL = "_id"
        val DATE_COl = "date"
        val TIME_COL = "time"
        val EARNINGS_COL = "earnings"
        val WASH_COL = "wash"
        val FUEL_COL = "fuel"
        val MILEAGE_COL = "mileage"
        val PROFIT_COL = "profit"
        var DELETE_INDEX = 0
    }
}