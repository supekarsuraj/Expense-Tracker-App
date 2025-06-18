package com.example.androidexpensetrackerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidexpensetrackerapp.databinding.ActivityMainBinding
import com.example.androidexpensetrackerapp.uiDesign.AddExpenseActivity
import com.example.androidexpensetrackerapp.uiDesign.ExpenseListActivity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateThemeIcon(isDarkMode)
        binding.themeToggleBtn.setOnClickListener {
            val newMode = !sharedPrefs.getBoolean("dark_mode", false)
            sharedPrefs.edit().putBoolean("dark_mode", newMode).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }
        binding.btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.btnViewExpenses.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
    }

    private fun updateThemeIcon(isDarkMode: Boolean) {
        val icon = if (isDarkMode) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        binding.themeToggleBtn.setImageResource(icon)
    }
}
