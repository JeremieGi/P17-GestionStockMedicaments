package com.openclassrooms.rebonnte.ui.medecine.list

import com.openclassrooms.rebonnte.model.Medicine

sealed class MedecineListUIState {

    data object IsLoading : MedecineListUIState()

    data class Success(
        val listMedecines : List<Medicine>
    ) : MedecineListUIState()

    data class Error(val sError: String?) : MedecineListUIState()

}
