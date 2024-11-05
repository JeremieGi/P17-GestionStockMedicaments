package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.model.User

/**
 * Interface utilisée par StockRepository pour la gestion des accès utilisateur
 */
interface UserAPI {

    /**
     * Renvoie Vrai si un utilisateur est loggué, faux sinon
     */
    fun userLogged() : Boolean

    /**
     * Donne l'utilisateur courant
     */
    fun getCurrentUser() : User?

    /**
     * Déconnecte l'utilisateur courant
     */
    fun logout(context : Context) : Task<Void>
}
