package com.openclassrooms.rebonnte.ui.medicine.list

import com.openclassrooms.rebonnte.model.Medicine

sealed class MedicineListUIState {

    data object IsLoading : MedicineListUIState()

    data class LoadSuccess(
        val listMedicines : List<Medicine>
    ) : MedicineListUIState()

    data class LoadingError(val sError: String?) : MedicineListUIState()

    data class DeleteError(val sError: String?) : MedicineListUIState()

    data object DeleteSuccess : MedicineListUIState()

}
