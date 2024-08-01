package com.example.cannabisappmobile

data class History(
    val action: String,
    val plantId: String,
    val modifiedField: String,
    val oldValue: String,
    val newValue: String,
    val timestamp: String
)
