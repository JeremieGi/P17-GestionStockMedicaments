package com.openclassrooms.rebonnte.ui.aisle.detail

import com.openclassrooms.rebonnte.model.Aisle


/**
 * UI State principal
 */
data class AisleDetailUIState (
    // Valeur en mémoire affiché
    val currentStateAisle : CurrentAisleUIState = CurrentAisleUIState.IsLoading,
    // Erreurs de formulaire
    val formError: FormErrorAddAisle? = null
)

sealed class CurrentAisleUIState {

    data object IsLoading : CurrentAisleUIState()

    data class LoadSuccess(
        val aisle : Aisle
    ) : CurrentAisleUIState()

    data class LoadError(val sError: String?) : CurrentAisleUIState()
    data class ValidateError(val sError: String) : CurrentAisleUIState()

    data object ValidateSuccess : CurrentAisleUIState() // upload or insert
}

/**
 * Différents types d'erreur sur le formulaire d'ajout
 */
sealed class FormErrorAddAisle {

    data class NameError(val error: String?) : FormErrorAddAisle()

}