package com.openclassrooms.rebonnte.ui.medicine.list

import com.openclassrooms.rebonnte.model.Medicine

sealed class MedicineListUIState {

    data object IsLoading : MedicineListUIState()

    data class Success(
        val listMedicines : List<Medicine>
    ) : MedicineListUIState()

    data class Error(val sError: String?) : MedicineListUIState()

}
