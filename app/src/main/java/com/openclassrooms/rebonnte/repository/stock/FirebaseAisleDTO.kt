package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine


data class FirebaseAisleDTO (

    @PropertyName(RUB_AISLE_ID)
    val id : String,

    @PropertyName("name")
    val name : String

){

    constructor(aisle: Aisle) : this (
        id = aisle.id,
        name = aisle.name
    )

    fun toModel() : Aisle {
        return Aisle(
            id = this.id,
            name = this.name
        )
    }

    // Pour ne pas maintenir ces noms de champs avec les @property du DTO
    companion object {
        const val RUB_AISLE_ID = "id"
    }
}



