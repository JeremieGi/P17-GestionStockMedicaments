package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.stock.FirebaseMedicineDTO.Companion.RUB_ID
import java.util.Date


data class FirebaseHistoryDTO (

    // TODO Denis : J'ai été obligé de mettre les propriétés en var + @get:PropertyName et  @set:PropertyName

    @get:PropertyName("emailauthor")
    val sEmailAuthor : String = "",

    @get:PropertyName("date")
    val lDate: Long = 0L,

    @get:PropertyName("details")
    val details: String = ""

){

    constructor(history: History) : this (
        sEmailAuthor = history.author.sEmail,
        lDate = history.date.time,
        details = history.details
    )

    fun toModel(): History {

        return History(
            author = User(
                id ="",
                sName = "",
                sEmail = this.sEmailAuthor)
            ,
            date =  Date(this.lDate),
            details = this.details
        )

    }

}
