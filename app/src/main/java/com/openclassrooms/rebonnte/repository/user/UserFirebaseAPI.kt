package com.openclassrooms.rebonnte.repository.user

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.rebonnte.model.User

/**
 * API utilisée en prod pour gérer l'accès aux utilisateurs
 */
class UserFirebaseAPI : UserAPI {

    // Utilisation du contexte utilisateur de Firebase
    private fun getCurrentFirebaseUser() : FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun userLogged(): Boolean {
        return getCurrentFirebaseUser() != null
    }

    override fun getCurrentUser(): User? {
        val firebaseUser =  getCurrentFirebaseUser()
        if (firebaseUser!=null){
            return User(
                id = firebaseUser.uid,
                sName = firebaseUser.displayName?:"",
                sEmail = firebaseUser.email?:""
            )
        }
        else{
            return null
        }
    }

    override fun logout(context: Context): Task<Void> {
        return AuthUI.getInstance().signOut(context)
    }


}