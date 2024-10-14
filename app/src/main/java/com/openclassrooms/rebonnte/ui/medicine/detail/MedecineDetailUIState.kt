package com.openclassrooms.rebonnte.ui.medicine.detail

import com.openclassrooms.rebonnte.model.Medicine


sealed class MedecineDetailUIState {

    data object IsLoading : MedecineDetailUIState()

    data class Success(
        val medecineDetail : Medicine
    ) : MedecineDetailUIState()

    data class Error(val sError: String?) : MedecineDetailUIState()

}