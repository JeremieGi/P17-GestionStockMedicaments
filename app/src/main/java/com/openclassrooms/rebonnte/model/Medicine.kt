package com.openclassrooms.rebonnte.model

data class Medicine(
    var name: String,
    var stock: Int,
    var nameAisle: String, // TODo JG : remplacé par var oAisle : Aisle plus évolutif
    var histories: List<History>
)
