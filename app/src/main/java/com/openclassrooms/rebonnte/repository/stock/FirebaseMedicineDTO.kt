package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine

data class FirebaseMedicineDTO (

    // TODO Denis : J'ai été obligé de mettre les propriétés en var + @get:PropertyName et  @set:PropertyName

    @get:PropertyName(RUB_ID)
    @set:PropertyName(RUB_ID)
    var id : String = "",

    @get:PropertyName(RUB_NAME)
    @set:PropertyName(RUB_NAME)
    var name: String = "",

    @get:PropertyName(RUB_STOCK)
    @set:PropertyName(RUB_STOCK)
    var stock: Int = 0,

    // TODO JG : Faire un Objet AisleDTo ici (parler du Data Connect en soutenance)
    @get:PropertyName("nameaisle")
    @set:PropertyName("nameaisle")
    var sNameAisle: String = "",

    @get:PropertyName("histories")
    @set:PropertyName("histories")
    var histories: MutableList<FirebaseHistoryDTO> = mutableListOf()


) {

    constructor(medicine: Medicine) : this (
        id = medicine.id,
        name = medicine.name,
        stock = medicine.stock,
        sNameAisle = medicine.oAisle.name,
        //histories = emptyList<FirebaseHistoryDTO>().toMutableList()
        histories = medicine.histories.map {
            FirebaseHistoryDTO(it)
        }.toMutableList()
    )

    fun toModel(): Medicine {

        return Medicine(
            id = this.id,
            name = this.name,
            stock = this.stock,
            oAisle = Aisle("",this.sNameAisle),
            //histories = emptyList<History>().toMutableList()
            histories = this.histories.map {
                it.toModel()
            }.toMutableList()
        )
    }

    // Pour ne pas maintenir ces noms de champs avec les @property du DTO + dans tri dans l'API
    companion object {
        const val RUB_ID = "id"
        const val RUB_NAME = "name"
        const val RUB_STOCK = "stock"
    }
}
