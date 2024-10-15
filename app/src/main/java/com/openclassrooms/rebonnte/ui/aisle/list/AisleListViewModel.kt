package com.openclassrooms.rebonnte.ui.aisle.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AisleListViewModel  @Inject constructor(
    private val stockRepository: StockRepository
): ViewModel() {

    private var _uiStateListAile = MutableStateFlow<AisleListUIState>(AisleListUIState.IsLoading)
    val uiStateListAile: StateFlow<AisleListUIState> get() = _uiStateListAile

    init {
        observeFlow()
    }

    private fun observeFlow() {

        viewModelScope.launch {

            stockRepository.flowAisles.collect { resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiStateListAile.value = AisleListUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateListAile.value = AisleListUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val listAisles = resultFlow.value
                        _uiStateListAile.value = AisleListUIState.Success(listAisles)

                    }


                }

            }

        }
    }

    fun loadAllAisle() {
        viewModelScope.launch {
            stockRepository.loadAllAisles()
        }
    }

}

