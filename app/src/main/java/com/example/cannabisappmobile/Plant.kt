package com.example.cannabisappmobile

data class Plant(
    val id: String,
    var healthStatus: String,
    var date: String,
    var origin: String,
    var description: String,
    var stage: String,
    var storage: String,
    var active: Int,
    var withdrawalDate: String?,
    var removalReason: String,
    var decontaminationResponsible: String,
    var note: String
)
