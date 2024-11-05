package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.Aisle

/**
 * Classe DTO pour stockage des allées dans firestore Database
 */
data class FirebaseAisleDTO (

    @get:PropertyName(RUB_AISLE_ID)
    @set:PropertyName(RUB_AISLE_ID)
    var id : String = "",

    @get:PropertyName(RUB_AISLE_NAME)
    @set:PropertyName(RUB_AISLE_NAME)
    var name : String = ""

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
        const val RUB_AISLE_NAME = "name"
    }
}



