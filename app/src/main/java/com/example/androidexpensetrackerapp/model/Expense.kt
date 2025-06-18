package com.example.androidexpensetrackerapp.model

data class Expense(
    val id: Int,
    val title: String,
    val amount: Float,
    val category: String,
    val date: String,
    val notes: String?
)