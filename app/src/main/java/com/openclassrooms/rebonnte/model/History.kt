package com.openclassrooms.rebonnte.model

import java.util.Date

data class History(
    val userId: String,
    val date: Date,
    val details: String
)
