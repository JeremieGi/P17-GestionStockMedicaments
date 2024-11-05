package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.User
import java.util.Date

/**
 * Classe DTO pour stockage de l'historique dans firestore Database
 */
data class FirebaseHistoryDTO (

    @get:PropertyName("emailauthor")
    @set:PropertyName("emailauthor")
    var sEmailAuthor : String = "",

    @get:PropertyName("date")
    @set:PropertyName("date")
    var lDate: Long = 0L,

    @get:PropertyName("details")
    @set:PropertyName("details")
    var details: String = ""

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
