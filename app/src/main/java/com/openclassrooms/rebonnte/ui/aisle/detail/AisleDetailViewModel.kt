package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repositoryStock.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AisleDetailViewModel @Inject constructor (
    private val stockRepository: StockRepository
): ViewModel() {

    // UI state - Chargement par défaut
    private val _uiState = MutableStateFlow<AisleDetailUIState>(AisleDetailUIState.IsLoading)
    val uiState: StateFlow<AisleDetailUIState> = _uiState.asStateFlow() // Accès en lecture seule de l'extérieur

    fun loadAisleByID (idAisle : String) {

        viewModelScope.launch {

            stockRepository.loadAisleByID(idAisle).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiState.value = AisleDetailUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiState.value = AisleDetailUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val event = resultFlow.value
                        _uiState.value = AisleDetailUIState.Success(event)

                    }


                }

            }

        }


    }

}
