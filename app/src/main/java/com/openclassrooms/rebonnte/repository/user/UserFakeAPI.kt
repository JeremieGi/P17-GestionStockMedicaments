package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.rebonnte.model.User

/**
 * API utilisée pour renvoyer les données lors des tests instrumentés ou dans les previews Compose
 */
class UserFakeAPI : UserAPI {

    companion object {

        // J'utilise cette procédure pour les previews Compose
        fun initFakeUsers() : List<User> {
            return listOf(
                User(
                    id = "1",
                    sName = "fake user",
                    sEmail = "fakeuser1@fake.fr"
                ),
                User(
                    id = "2",
                    sName = "fake user 2",
                    sEmail = "fakeuser2@fake.fr"
                ),
                User(
                    id = "3",
                    sName = "fake user 3",
                    sEmail = "fakeuser3@fake.fr"
                )
            )
        }

    }

    private val usersFake = initFakeUsers()

    override fun userLogged() : Boolean {
        return true
    }

    override fun getCurrentUser(): User {
        return usersFake[0]
    }

    override fun logout(context: Context): Task<Void> {
        // On ne fait rien dans la Fake API => car ce code n'est pas utile
        return Tasks.forResult(null) // Crée une tâche terminée avec un résultat null
    }

}