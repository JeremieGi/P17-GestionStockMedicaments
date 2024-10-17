package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.stock.FirebaseMedicineDTO.Companion.RUB_ID
import java.util.Date


data class FirebaseHistoryDTO (

    @PropertyName("emailauthor")
    val sEmailAuthor : String = "",

    @PropertyName("date")
    val lDate: Long = 0L,

    @PropertyName("details")
    val details: String = ""

){

    constructor(history: History) : this (
        sEmailAuthor = history.author.sEmail,
        lDate = history.date.time,
        details = history.details
    )

    fun toModel(): History {

        return History(
            author = User("","",this.sEmailAuthor),
            date =  Date(this.lDate),
            details = this.details
        )

    }

}
