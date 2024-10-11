package com.openclassrooms.rebonnte.ui.aisle.list

import com.openclassrooms.rebonnte.model.Aisle


sealed class AisleListUIState {

    data object IsLoading : AisleListUIState()

    data class Success(
        val listAisles : List<Aisle>
    ) : AisleListUIState()

    data class Error(val sError: String?) : AisleListUIState()

}
