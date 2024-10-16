package com.openclassrooms.rebonnte.ui.medicine.list

import com.openclassrooms.rebonnte.model.Medicine

sealed class MedicineListUIState {

    data object IsLoading : MedicineListUIState()

    data class Success(
        val listMedicines : List<Medicine>
    ) : MedicineListUIState()

    data class LoadingError(val sError: String?) : MedicineListUIState()

    // TODO Denis : Correct de gérer le retour d'erreur de la suppression comme çà ?
    data class DeleteError(val sError: String?) : MedicineListUIState()

}
