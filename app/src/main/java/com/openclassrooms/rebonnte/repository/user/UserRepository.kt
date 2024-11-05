package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository permettant la connexion des utilisateurs
 */
@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserAPI
) {

    /**
     * Renvoie Vrai si un utilisateur est loggué, faux sinon
     */
    fun userLogged() : Boolean {
        return userApi.userLogged()
    }

    /**
     * Donne l'utilisateur courant
     */
    fun getCurrentUser() : User? {
        return userApi.getCurrentUser()
    }

    /**
     * Déconnecte l'utilisateur courant
     */
    fun logout(context : Context) : Task<Void> {
        return userApi.logout(context)
    }
}