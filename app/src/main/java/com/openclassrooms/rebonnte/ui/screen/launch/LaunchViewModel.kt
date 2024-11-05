package com.openclassrooms.rebonnte.ui.screen.launch

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor (
    private val userRepository: UserRepository
): ViewModel() {

    /**
     * Renvoie Vrai si un utilisateur est loggué, faux sinon
     */
    fun userLogged() : Boolean {
        return userRepository.userLogged()
    }

    /**
     * Donne l'utilisateur courant
     */
    fun getCurrentUser() : User? {
        return userRepository.getCurrentUser()
    }

}