package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.InjectedContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserAPI,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
) {


    fun userLogged() : Boolean {
        return userApi.userLogged()
    }

    fun getCurrentUser() : User? {
        return userApi.getCurrentUser()
    }

    /**
     * Log out current user
     */
    fun logout(context : Context) : Task<Void> {
        return userApi.logout(context)
    }
}