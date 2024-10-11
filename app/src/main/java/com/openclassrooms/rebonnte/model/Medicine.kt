package com.openclassrooms.rebonnte.model

data class Medicine(
    var name: String,
    var stock: Int,
    var nameAisle: String,
    var histories: List<History>
)
