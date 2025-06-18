package com.example.androidexpensetrackerapp.db
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.androidexpensetrackerapp.model.Expense

class DBHelper(context: Context) : SQLiteOpenHelper(context, "expenses.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                amount REAL,
                category TEXT,
                date TEXT,
                notes TEXT
            )""".trimMargin()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS expenses")
        onCreate(db)
    }

    fun insertExpense(expense: Expense): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", expense.title)
            put("amount", expense.amount)
            put("category", expense.category)
            put("date", expense.date)
            put("notes", expense.notes)
        }
        return db.insert("expenses", null, values) > 0
    }

    fun updateExpense(expense: Expense): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", expense.title)
            put("amount", expense.amount)
            put("category", expense.category)
            put("date", expense.date)
            put("notes", expense.notes)
        }
        return db.update("expenses", values, "id=?", arrayOf(expense.id.toString())) > 0
    }

    fun deleteExpenseById(id: Int): Boolean {
        val db = writableDatabase
        return db.delete("expenses", "id=?", arrayOf(id.toString())) > 0
    }

    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM expenses ORDER BY date DESC", null)
        val list = mutableListOf<Expense>()
        while (cursor.moveToNext()) {
            list.add(
                Expense(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    amount = cursor.getFloat(2),
                    category = cursor.getString(3),
                    date = cursor.getString(4),
                    notes = cursor.getString(5)
                )
            )
        }
        cursor.close()
        return list
    }
}

