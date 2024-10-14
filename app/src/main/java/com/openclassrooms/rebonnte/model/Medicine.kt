package com.openclassrooms.rebonnte.model

data class Medicine(
    val id : String,
    val name: String,
    val stock: Int,
    val oAisle: Aisle,
    val histories: List<History>
)
