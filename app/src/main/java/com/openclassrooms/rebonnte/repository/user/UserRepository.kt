package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.repository.ResultCustom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserAPI,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
) {


    fun loadCurrentUser() : Flow<ResultCustom<User>> {

        if (!injectedContext.isInternetAvailable()) {
            return flow {
                emit(
                    ResultCustom.Failure(
                        injectedContext.getInjectedContext().getString(R.string.no_network)
                    )
                )
            }
        } else {
            return userApi.loadCurrentUser()
        }

    }

    fun insertCurrentUser() {
        userApi.insertCurrentUser()
    }

    fun userLogged() : Boolean {
        return userApi.userLogged()
    }

    fun getCurrentUser() : User {
        return userApi.getCurrentUser()
    }

    /**
     * Log out current user
     */
    fun signOut(context : Context) : Task<Void> {
        return userApi.signOut(context)
    }
}