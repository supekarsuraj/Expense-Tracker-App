package com.example.androidexpensetrackerapp.uiDesign

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidexpensetrackerapp.databinding.ActivityAddExpenseBinding
import com.example.androidexpensetrackerapp.db.DBHelper
import com.example.androidexpensetrackerapp.model.Expense
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var dbHelper: DBHelper
    private val categoryList = listOf("Food", "Travel", "Shopping", "Other")
    private val calendar = Calendar.getInstance()
    private var isEditMode = false
    private var expenseId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        setupUI()
        setupCategorySpinner()
        setupDatePicker()
        checkEditMode()
        setupSaveButton()
    }

    private fun setupUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set current date as default
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etDate.setText(dateFormat.format(calendar.time))
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.etDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun checkEditMode() {
        if (intent.hasExtra("expense_id")) {
            isEditMode = true
            expenseId = intent.getIntExtra("expense_id", 0)

            supportActionBar?.title = "Edit Expense"
            binding.btnSaveExpense.text = "Update Expense"

            populateFields()
        } else {
            supportActionBar?.title = "Add Expense"
        }
    }

    private fun populateFields() {
        binding.etTitle.setText(intent.getStringExtra("title"))
        binding.etAmount.setText(intent.getFloatExtra("amount", 0f).toString())
        binding.etDate.setText(intent.getStringExtra("date"))
        binding.etNotes.setText(intent.getStringExtra("notes"))

        val category = intent.getStringExtra("category")
        val index = categoryList.indexOf(category)
        if (index >= 0) {
            binding.spinnerCategory.setSelection(index)
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val title = binding.etTitle.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }

        if (amountText.isEmpty()) {
            binding.etAmount.error = "Amount is required"
            return
        }

        val amount = amountText.toFloatOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Invalid amount"
            return
        }

        if (date.isEmpty()) {
            binding.etDate.error = "Date is required"
            return
        }

        val expense = Expense(
            id = if (isEditMode) expenseId else 0,
            title = title,
            amount = amount,
            category = category,
            date = date,
            notes = notes.ifEmpty { null }
        )

        val result = if (isEditMode) {
            dbHelper.updateExpense(expense)
        } else {
            dbHelper.insertExpense(expense)
        }

        if (result) {
            val message = if (isEditMode) "Expense updated successfully" else "Expense saved successfully"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            val message = if (isEditMode) "Failed to update expense" else "Failed to save expense"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}