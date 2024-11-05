package com.openclassrooms.rebonnte.ui.medicine.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import com.openclassrooms.rebonnte.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineListViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _uiStateMedicines = MutableStateFlow<MedicineListUIState>(MedicineListUIState.IsLoading)
    val uiStateMedicines: StateFlow<MedicineListUIState> get() = _uiStateMedicines

    // Filtre et tri en cours
    private var _sNameFilter = ""
    private var _enumItemSort = StockRepository.EnumSortedItem.NONE

    init {
        observeFlow()
    }

    /**
     * Observation du flow de medicament du repository
     */
    private fun observeFlow() {

        viewModelScope.launch {

            stockRepository.flowMedicines.collect { resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiStateMedicines.value = MedicineListUIState.LoadingError(resultFlow.errorMessage)

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateMedicines.value = MedicineListUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val listMedicines = resultFlow.value
                        _uiStateMedicines.value = MedicineListUIState.LoadSuccess(listMedicines)

                    }


                }

            }

        }

    }

    /**
     * Chargement de tous les médicaments
     */
    fun loadAllMedicines() {
        viewModelScope.launch {
            stockRepository.loadAllMedicines(_sNameFilter,_enumItemSort)
        }
    }

    /**
     * Filtrer par nom
     */
    fun filterByName(sFilterNameP : String) {
        _sNameFilter = sFilterNameP
        loadAllMedicines()
    }

    /**
     * Annuler les filtres
     */
    fun sortByNone() {
        _enumItemSort = StockRepository.EnumSortedItem.NONE
        loadAllMedicines()
    }

    /**
     * Trier par nom
     */
    fun sortByName() {
        _enumItemSort = StockRepository.EnumSortedItem.NAME
        loadAllMedicines()
    }

    /**
     * Trier par stock
     */
    fun sortByStock() {
        _enumItemSort = StockRepository.EnumSortedItem.STOCK
        loadAllMedicines()
    }

    /**
     * Suppression d'un médicament
     */
    fun deleteMedicineById(sID: String) {
        viewModelScope.launch {
            stockRepository.deleteMedecineById(sID).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Transmission au UIState dédié

                    // Echec du au réseau
                    is ResultCustom.Failure -> {

                        // Récupération du message d'erreur
                        val sError = resultFlow.errorMessage?:""
                        _uiStateMedicines.value = MedicineListUIState.DeleteError(sError)

                    }

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateMedicines.value = MedicineListUIState.IsLoading
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        // Rechargement de la liste de médicaments
                        _uiStateMedicines.value = MedicineListUIState.DeleteSuccess
                        //loadAllMedicines()
                    }

                }

            }
        }
    }

    /**
     * Déconnecte l'utilisateur courant
     */
    fun logout(context : Context) : Task<Void> {
        return userRepository.logout(context)
    }

}

