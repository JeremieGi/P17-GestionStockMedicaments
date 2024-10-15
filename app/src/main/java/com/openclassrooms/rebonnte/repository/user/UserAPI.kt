package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.ResultCustom
import kotlinx.coroutines.flow.Flow


interface UserAPI {

    // Return true if the user is logged
    fun userLogged() : Boolean

    // Donne l'utilisateur courant
    fun getCurrentUser() : User

    /**
     * Chargement asynchrone d'un utilisateur
     */
    fun loadCurrentUser(): Flow<ResultCustom<User>>

    /**
     * Enregistre les données de l'utilisateur courant (après sa première identification)
     */
    fun insertCurrentUser()

    /**
     * Déconnecte un utilisateur
     */
    fun signOut(context : Context) : Task<Void>
}
