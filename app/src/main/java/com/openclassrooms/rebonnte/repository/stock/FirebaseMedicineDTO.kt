package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.Medicine

/**
 * Classe DTO pour stockage des médicaments dans firestore Database
 */
data class FirebaseMedicineDTO (

    // J'ai été obligé de mettre les propriétés en var + @get:PropertyName et  @set:PropertyName

    @get:PropertyName(RUB_ID)
    @set:PropertyName(RUB_ID)
    var id : String = "",

    @get:PropertyName(RUB_NAME)
    @set:PropertyName(RUB_NAME)
    var name: String = "",

    @get:PropertyName(RUB_STOCK)
    @set:PropertyName(RUB_STOCK)
    var stock: Int = 0,

    @get:PropertyName("aisle")
    @set:PropertyName("aisle")
    var aisle : FirebaseAisleDTO = FirebaseAisleDTO(), // Je stocke aussi l'ID de l'allée (plus évolutif en cas de modification de nom d'allée dans les futures évolutions)

    @get:PropertyName("histories")
    @set:PropertyName("histories")
    var histories: MutableList<FirebaseHistoryDTO> = mutableListOf()


) {

    constructor(medicine: Medicine) : this (
        id = medicine.id,
        name = medicine.name,
        stock = medicine.stock,
        aisle = FirebaseAisleDTO(medicine.oAisle),
        histories = medicine.histories.map {
            FirebaseHistoryDTO(it)
        }.toMutableList()
    )

    fun toModel(): Medicine {

        return Medicine(
            id = this.id,
            name = this.name,
            stock = this.stock,
            oAisle = this.aisle.toModel(),
            histories = this.histories.map {
                it.toModel()
            }.toMutableList()
        )
    }

    // Pour garder en phase ces noms de champs avec les @property du DTO + dans tri dans l'API
    companion object {
        const val RUB_ID = "id"
        const val RUB_NAME = "name"
        const val RUB_STOCK = "stock"
    }
}
