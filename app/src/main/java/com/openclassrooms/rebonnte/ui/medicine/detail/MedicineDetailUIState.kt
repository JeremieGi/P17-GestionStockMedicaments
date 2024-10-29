package com.openclassrooms.rebonnte.ui.medicine.detail

import com.openclassrooms.rebonnte.model.Medicine

/**
 * UI State principal
 */
data class MedicineDetailUIState (
    // Valeur en mémoire du médicament affiché
    val currentStateMedicine : CurrentMedicineUIState = CurrentMedicineUIState.IsLoading,
    // Erreurs de formulaire
    val formError: FormErrorAddMedicine? = null
)


sealed class CurrentMedicineUIState {

    data object IsLoading : CurrentMedicineUIState()

    data class LoadSuccess(
        val medicineValue : Medicine
    ) : CurrentMedicineUIState()

    data class LoadError(val sError: String) : CurrentMedicineUIState()
    data class ValidateError(val sError: String) : CurrentMedicineUIState()

    data object ValidateSuccess : CurrentMedicineUIState() // upload or insert
}

/**
 * Différents types d'erreur sur le formulaire d'ajout
 */
sealed class FormErrorAddMedicine {

    data object NameError : FormErrorAddMedicine()

    data object AisleErrorEmpty : FormErrorAddMedicine()
    data object AisleErrorNoExist : FormErrorAddMedicine()


    data object StockError : FormErrorAddMedicine()

}