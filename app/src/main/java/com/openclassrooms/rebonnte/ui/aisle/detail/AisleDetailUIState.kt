package com.openclassrooms.rebonnte.ui.aisle.detail

import com.openclassrooms.rebonnte.model.Aisle


sealed class AisleDetailUIState {

    data object IsLoading : AisleDetailUIState()

    data class Success(
        val aisle : Aisle
    ) : AisleDetailUIState()

    data class Error(val sError: String?) : AisleDetailUIState()

}