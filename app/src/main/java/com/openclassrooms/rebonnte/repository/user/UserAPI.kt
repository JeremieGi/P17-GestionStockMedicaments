package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.model.User


interface UserAPI {

    // Return true if the user is logged
    fun userLogged() : Boolean

    // Donne l'utilisateur courant
    fun getCurrentUser() : User?

    /**
     * Déconnecte un utilisateur
     */
    fun logout(context : Context) : Task<Void>
}
