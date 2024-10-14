package com.openclassrooms.rebonnte.ui.medicine.detail

import com.openclassrooms.rebonnte.model.Medicine


sealed class MedicineDetailUIState {

    data object IsLoading : MedicineDetailUIState()

    data class Success(
        val medecineDetail : Medicine
    ) : MedicineDetailUIState()

    data class Error(val sError: String?) : MedicineDetailUIState()

}