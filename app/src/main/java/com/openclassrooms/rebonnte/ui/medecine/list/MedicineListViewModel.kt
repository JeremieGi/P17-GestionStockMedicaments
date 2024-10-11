package com.openclassrooms.rebonnte.ui.medecine.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repositoryStock.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MedicineListViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private var _uiStateMedicines = MutableStateFlow<MedecineListUIState>(MedecineListUIState.IsLoading)
    val uiStateMedicines: StateFlow<MedecineListUIState> get() = _uiStateMedicines

    private var _sNameFilter = ""
    private var _enumItemSort = StockRepository.EnumSortedItem.NONE

    init {
        observeFlow()
    }

    private fun observeFlow() {

        viewModelScope.launch {

            stockRepository.flowMedecines.collect { resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiStateMedicines.value = MedecineListUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateMedicines.value = MedecineListUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val listEvents = resultFlow.value
                        _uiStateMedicines.value = MedecineListUIState.Success(listEvents)

                    }


                }

            }

        }

    }

    // TODO JG : A basculer dans une fenêtre d'ajout
    fun addRandomMedicine(aisles: List<Aisle>) {

        val randomMed =  Medicine(
            "Medicine",
            Random().nextInt(100),
            aisles[Random().nextInt(aisles.size)].name,
            emptyList()
        )

        viewModelScope.launch {

            stockRepository.addMedicine(randomMed).collect { resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Transmission au UIState dédié

                    // Echec du au réseau
                    is ResultCustom.Failure -> {

                        // Récupération du message d'erreur
                        val sErrorNetwork = resultFlow.errorMessage


                    }

                    // En chargement
                    is ResultCustom.Loading -> {

                    }

                    // Succès
                    is ResultCustom.Success -> {

                    }

                }
            }

        }


    }

    fun loadAllMedicines() {
        viewModelScope.launch {
            stockRepository.loadAllMedecines(_sNameFilter,_enumItemSort)
        }
    }

    fun filterByName(sFilterNameP : String) {
        _sNameFilter = sFilterNameP
        loadAllMedicines()
    }

    fun sortByNone() {
        _enumItemSort = StockRepository.EnumSortedItem.NONE
        loadAllMedicines()
    }

    fun sortByName() {
        _enumItemSort = StockRepository.EnumSortedItem.NAME
        loadAllMedicines()
    }

    fun sortByStock() {
        _enumItemSort = StockRepository.EnumSortedItem.STOCK
        loadAllMedicines()
    }

}

