package com.openclassrooms.rebonnte.ui.medicine.detail

import com.openclassrooms.rebonnte.model.Medicine


sealed class MedicineDetailUIState {

    data object IsLoading : MedicineDetailUIState()

    data class LoadSuccess(
        val medicineDetail : Medicine
    ) : MedicineDetailUIState()

    data class Error(val sError: String?) : MedicineDetailUIState()

    data object UploadSuccess : MedicineDetailUIState()
}