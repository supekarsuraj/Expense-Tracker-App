package com.example.androidexpensetrackerapp.uiDesign

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidexpensetrackerapp.adaptar.ExpenseAdapter
import com.example.androidexpensetrackerapp.databinding.ActivityExpenseListBinding
import com.example.androidexpensetrackerapp.db.DBHelper
import com.example.androidexpensetrackerapp.model.Expense
import java.text.DateFormatSymbols

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseListBinding
    private lateinit var expenseAdapter: ExpenseAdapter
    private val allExpenses = mutableListOf<Expense>()
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        setupRecyclerView()
        setupFilters()
        loadExpensesFromDatabase()
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            showEditDeleteDialog(expense)
        }
        binding.recyclerViewExpenses.apply {
            layoutManager = LinearLayoutManager(this@ExpenseListActivity)
            adapter = expenseAdapter
        }
    }

    private fun setupFilters() {
        val categories = listOf("All", "Food", "Travel", "Shopping", "Other")
        val months = listOf(
            "All", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterCategory.adapter = categoryAdapter

        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterMonth.adapter = monthAdapter

        binding.spinnerFilterCategory.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                filterExpenses()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        binding.spinnerFilterMonth.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                filterExpenses()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterExpenses()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

//    private fun filterExpenses() {
//        val searchQuery = binding.etSearch.text.toString().lowercase()
//        val selectedCategory = binding.spinnerFilterCategory.selectedItem.toString()
//        val selectedMonth = binding.spinnerFilterMonth.selectedItem.toString()
//
//        val filtered = allExpenses.filter { expense ->
//            val matchesCategory = selectedCategory == "All" || expense.category == selectedCategory
//            val matchesSearch = expense.title.lowercase().contains(searchQuery) ||
//                    (expense.notes?.lowercase()?.contains(searchQuery) ?: false)
//
//            val matchesMonth = if (selectedMonth == "All") true else {
//                try {
//                    val monthIndex = expense.date.split("-")[1].toInt()
//                    val monthName = DateFormatSymbols().months[monthIndex - 1]
//                    monthName.equals(selectedMonth, ignoreCase = true)
//                } catch (e: Exception) {
//                    false
//                }
//            }
//
//            matchesCategory && matchesSearch && matchesMonth
//        }
//
//        expenseAdapter.submitList(filtered)
//    }

    private fun filterExpenses() {
        val searchQuery = binding.etSearch.text.toString().lowercase()
        val selectedCategory = binding.spinnerFilterCategory.selectedItem.toString()
        val selectedMonth = binding.spinnerFilterMonth.selectedItem.toString()

        val filtered = allExpenses.filter { expense ->
            val matchesCategory = selectedCategory == "All" || expense.category == selectedCategory
            val matchesSearch = expense.title.lowercase().contains(searchQuery) ||
                    (expense.notes?.lowercase()?.contains(searchQuery) ?: false)

            val matchesMonth = if (selectedMonth == "All") true else {
                try {
                    val monthIndex = expense.date.split("-")[1].toInt()
                    val monthName = java.text.DateFormatSymbols().months[monthIndex - 1]
                    monthName.equals(selectedMonth, ignoreCase = true)
                } catch (e: Exception) {
                    false
                }
            }

            matchesCategory && matchesSearch && matchesMonth
        }
        val totalAmount = filtered.sumOf { it.amount.toDouble() }
        binding.tvTotalAmount.text = "Total: ₹ %.2f".format(totalAmount)


        expenseAdapter.submitList(filtered)
    }

    private fun loadExpensesFromDatabase() {
        allExpenses.clear()
        allExpenses.addAll(dbHelper.getAllExpenses())
        expenseAdapter.submitList(allExpenses.toList()) // Immutable copy
    }

    private fun showEditDeleteDialog(expense: Expense) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(this)
            .setTitle("Select Option")
            .setItems(options) { _, which ->
                if (which == 0) {
                    val intent = Intent(this, AddExpenseActivity::class.java).apply {
                        putExtra("expense_id", expense.id)
                        putExtra("title", expense.title)
                        putExtra("amount", expense.amount)
                        putExtra("category", expense.category)
                        putExtra("date", expense.date)
                        putExtra("notes", expense.notes)
                    }
                    startActivity(intent)
                } else {
                    dbHelper.deleteExpenseById(expense.id)
                    loadExpensesFromDatabase()
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }

    override fun onResume() {
        super.onResume()
        loadExpensesFromDatabase()
    }
}
