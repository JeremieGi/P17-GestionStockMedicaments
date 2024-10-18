package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine

data class FirebaseMedicineDTO (

    @PropertyName(RUB_ID)
    val id : String = "",

    @PropertyName(RUB_NAME)
    val name: String = "",

    @PropertyName(RUB_STOCK)
    val stock: Int = 0,

    // TODO JG : Faire un Objet AisleDTo ici (parler du Data Connect en soutenance)
    @PropertyName("nameaisle")
    val sNameAisle: String = "",

    @PropertyName("histories")
    val histories: MutableList<FirebaseHistoryDTO> = mutableListOf()


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
