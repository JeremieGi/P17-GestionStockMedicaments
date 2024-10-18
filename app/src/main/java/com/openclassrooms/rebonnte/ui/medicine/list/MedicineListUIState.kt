package com.openclassrooms.rebonnte.ui.medicine.list

import com.openclassrooms.rebonnte.model.Medicine

sealed class MedicineListUIState {

    data object IsLoading : MedicineListUIState()

    data class Success(
        val listMedicines : List<Medicine>
    ) : MedicineListUIState()

    data class LoadingError(val sError: String?) : MedicineListUIState()

    // TODO JG : Mettre le success et l'erreur dans cet objet (pour mettre une snack bar) avec un bouton Retry éventuellement
    data class DeleteError(val sError: String?) : MedicineListUIState()

}
