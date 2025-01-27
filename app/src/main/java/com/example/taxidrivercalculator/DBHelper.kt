package com.example.taxidrivercalculator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE_COl + " TEXT," +
                TIME_COL + " REAL," +
                EARNINGS_COL + " REAL," +
                WASH_COL + " REAL," +
                FUEL_COL + " REAL," +
                MILEAGE_COL + " REAL," +
                PROFIT_COL + " REAL" + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // This method is for adding data in our database
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

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)


        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    fun getShift(): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

    }

    fun updateId (index: Int) {
        val db = this.writableDatabase
        DELETE_INDEX = index
        db.execSQL("UPDATE shifts_table SET _id = _id - 1 WHERE _id > $index")
        db.close()

    }
    /*fun updateId () {
        val db = this.writableDatabase
        var i = 0
        db.run {
            execSQL("UPDATE shifts_table SET _id = $i++ WHERE _id < SIZE+1")
            close()
        }
    }*/

    fun editShift(index: Int) {
        val db = this.writableDatabase

        //db.delete(TABLE_NAME, "_id =$index", null)
        //TODO: make edit function

        db.close()
    }

    fun deleteShift(index: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "_id =$index", null)

        /*val cv = ContentValues()
        cv.put("id", index-1)
        val cursor = this.getShift()
        cursor!!.moveToPosition(index)
        cv.put("date", cursor.getString(cursor.getColumnIndex(DATE_COl)+0))
        cv.put("time", cursor.getString(cursor.getColumnIndex(TIME_COL)+0))
        cv.put("earnings", cursor.getDouble(cursor.getColumnIndex(EARNINGS_COL)+0))
        cv.put("wash", cursor.getDouble(cursor.getColumnIndex(WASH_COL)+0))
        cv.put("fuel", cursor.getDouble(cursor.getColumnIndex(FUEL_COL)+0))
        cv.put("mileage", cursor.getDouble(cursor.getColumnIndex(MILEAGE_COL)+0))
        cv.put("profit", cursor.getDouble(cursor.getColumnIndex(PROFIT_COL)+0))
        db.replace(TABLE_NAME, "id = $index", cv)*/
        //db.execSQL("")
        db.close()

        /*val cursor = this.getShift()
        cursor!!.moveToPosition(index)
        if (cursor.isLast)
        {
            return
        }
        updateId(index)*/

    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM "+ TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
        db.close()
    }


    companion object {
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "SHIFTS"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "shifts_table"

        // below is the variable for id column
        val ID_COL = "_id"

        // below is the variable for name column
        val DATE_COl = "date"

        // below is the variable for age column
        val TIME_COL = "time"

        val EARNINGS_COL = "earnings"
        val WASH_COL = "wash"
        val FUEL_COL = "fuel"
        val MILEAGE_COL = "mileage"
        val PROFIT_COL = "profit"
        var DELETE_INDEX = 0
    }
}