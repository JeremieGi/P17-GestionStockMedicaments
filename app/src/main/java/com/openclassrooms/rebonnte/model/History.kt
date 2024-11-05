package com.openclassrooms.rebonnte.model

import java.util.Date

/**
 * Classe métier pour stocker l'historique de modification d'un médicament
 */
data class History(
    val author : User,
    val date: Date = Date(),
    val details: String
)
