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

    private var _uiStateMedicineDetail = MutableStateFlow<MedecineDetailUIState>(MedecineDetailUIState.IsLoading)
    val uiStateMedicineDetail : StateFlow<MedecineDetailUIState> get() = _uiStateMedicineDetail


    fun loadMedicineByID(idMedicineP: String) {

        viewModelScope.launch {

            stockRepository.loadMedicineByID(idMedicineP).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiStateMedicineDetail.value = MedecineDetailUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateMedicineDetail.value = MedecineDetailUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val event = resultFlow.value
                        _uiStateMedicineDetail.value = MedecineDetailUIState.Success(event)

                    }


                }

            }

        }

    }



}