package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.ResultCustom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserFakeAPI : UserAPI {

    companion object {

        // J'utilise cette procédure pour les previews Compose
        fun initFakeCurrentUser() : List<User> {
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

    private val usersFake = initFakeCurrentUser()

    override fun userLogged() : Boolean {
        return true
    }

    override fun getCurrentUser(): User {
        return usersFake[0]
    }


    override fun loadCurrentUser(): Flow<ResultCustom<User>> {

        return callbackFlow {

            trySend(ResultCustom.Loading)
            //delay(1*1000)

            trySend(ResultCustom.Success(getCurrentUser()))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }


    override fun insertCurrentUser() {
        // On ne fait rien dans la Fake API => car ce code n'est pas utile
    }

    override fun signOut(context: Context): Task<Void> {
        // On ne fait rien dans la Fake API => car ce code n'est pas utile
        return Tasks.forResult(null) // Crée une tâche terminée avec un résultat null
    }

}