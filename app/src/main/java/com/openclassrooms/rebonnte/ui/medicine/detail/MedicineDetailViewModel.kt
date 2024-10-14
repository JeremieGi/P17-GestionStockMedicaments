package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repositoryStock.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor (
    private val stockRepository: StockRepository
): ViewModel() {

    private var _uiStateMedicineDetail = MutableStateFlow<MedicineDetailUIState>(MedicineDetailUIState.IsLoading)
    val uiStateMedicineDetail : StateFlow<MedicineDetailUIState> get() = _uiStateMedicineDetail


    fun loadMedicineByID(idMedicineP: String) {

        viewModelScope.launch {

            stockRepository.loadMedicineByID(idMedicineP).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiStateMedicineDetail.value = MedicineDetailUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateMedicineDetail.value = MedicineDetailUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val medicine = resultFlow.value
                        _uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(medicine)

                    }


                }

            }

        }

    }

    fun incrementStock(){

        val currentState = _uiStateMedicineDetail.value

        // Cette condition devrait toujours être vraie lors de l'appel à cette fonction
        if (currentState is MedicineDetailUIState.LoadSuccess) {

            val nNewStock = currentState.medecineDetail.stock + 1

            val updatedMedicine = currentState.medecineDetail.copy(stock = nNewStock)

            // Met à jour l'état avec le nouvel objet Medicine modifié
            _uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(updatedMedicine)
        }

    }

    fun decrementStock(){

        val currentState = _uiStateMedicineDetail.value

        // Cette condition devrait toujours être vraie lors de l'appel à cette fonction
        if (currentState is MedicineDetailUIState.LoadSuccess) {

            var nNewStock = currentState.medecineDetail.stock - 1
            if (nNewStock < 0) {
                nNewStock = 0
            }

            val updatedMedicine = currentState.medecineDetail.copy(stock = nNewStock)

            // Met à jour l'état avec le nouvel objet Medicine modifié
            _uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(updatedMedicine)
        }

    }

    fun updateStock() {

        val currentState = _uiStateMedicineDetail.value
        if (currentState is MedicineDetailUIState.LoadSuccess) {

            val updatedMedicine = currentState.medecineDetail

            viewModelScope.launch {

                stockRepository.updateMedicine(updatedMedicine).collect { resultFlow ->

                    // En fonction du résultat
                    when (resultFlow) {

                        // Transmission au UIState dédié

                        // Echec du au réseau
                        is ResultCustom.Failure -> {

                            // Récupération du message d'erreur
                            val sError = resultFlow.errorMessage

                            // Affiche la fenêtre d'erreur
                            _uiStateMedicineDetail.value = MedicineDetailUIState.Error(sError)

                        }

                        // En chargement
                        is ResultCustom.Loading -> {
                            // Propagation du chargement
                            _uiStateMedicineDetail.value = MedicineDetailUIState.IsLoading
                        }

                        // Succès
                        is ResultCustom.Success -> {
                            _uiStateMedicineDetail.value = MedicineDetailUIState.UploadSuccess
                        }

                    }

                }

            }


        }


    }


}