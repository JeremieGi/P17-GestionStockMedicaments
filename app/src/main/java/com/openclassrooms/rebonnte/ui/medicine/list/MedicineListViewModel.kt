package com.openclassrooms.rebonnte.ui.medicine.list

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
class MedicineListViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private var _uiStateMedicines = MutableStateFlow<MedicineListUIState>(MedicineListUIState.IsLoading)
    val uiStateMedicines: StateFlow<MedicineListUIState> get() = _uiStateMedicines

    private var _sNameFilter = ""
    private var _enumItemSort = StockRepository.EnumSortedItem.NONE

    init {
        observeFlow()
    }

    private fun observeFlow() {

        viewModelScope.launch {

            stockRepository.flowMedicines.collect { resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiStateMedicines.value = MedicineListUIState.Error(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateMedicines.value = MedicineListUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val listMedicines = resultFlow.value
                        _uiStateMedicines.value = MedicineListUIState.Success(listMedicines)

                    }


                }

            }

        }

    }

    fun loadAllMedicines() {
        viewModelScope.launch {
            stockRepository.loadAllMedicines(_sNameFilter,_enumItemSort)
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

