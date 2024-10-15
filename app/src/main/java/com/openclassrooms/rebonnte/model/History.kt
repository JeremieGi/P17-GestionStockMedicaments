package com.openclassrooms.rebonnte.model

import java.util.Date

data class History(
    val author : User,
    val date: Date = Date(),
    val details: String
)
