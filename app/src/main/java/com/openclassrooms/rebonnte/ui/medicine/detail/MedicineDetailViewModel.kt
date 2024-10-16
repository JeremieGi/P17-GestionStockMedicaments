package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import com.openclassrooms.rebonnte.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MedicineDetailViewModel @Inject constructor (
    private val stockRepository: StockRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private var _uiStateMedicineDetail = MutableStateFlow( MedicineDetailUIState() )
    val uiStateMedicineDetail : StateFlow<MedicineDetailUIState> get() = _uiStateMedicineDetail

    private var _isAddMode = false


    fun loadMedicineByID(idMedicineP: String) {

        viewModelScope.launch {

            stockRepository.loadMedicineByID(idMedicineP).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        //_uiStateMedicineDetail.value = MedicineDetailUIState.Error(resultFlow.errorMessage)

                        _uiStateMedicineDetail.update{ currentState ->
                            currentState.copy(
                                currentStateMedicine = CurrentMedicineUIState.LoadError(resultFlow.errorMessage?:""),
                                formError = null
                            )
                        }

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        //_uiStateMedicineDetail.value = MedicineDetailUIState.IsLoading

                        _uiStateMedicineDetail.update{ currentState ->
                            currentState.copy(
                                currentStateMedicine = CurrentMedicineUIState.IsLoading,
                                formError = null
                            )
                        }
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        val medicine = resultFlow.value
//                        _uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(medicine)

                        _uiStateMedicineDetail.update{ currentState ->
                            currentState.copy(
                                currentStateMedicine = CurrentMedicineUIState.LoadSuccess(medicine)
                            )
                        }


                    }


                }

            }

        }

    }

    fun incrementStock(){

        val currentState = _uiStateMedicineDetail.value

        // Cette condition devrait toujours être vraie lors de l'appel à cette fonction
        if (currentState.currentStateMedicine is CurrentMedicineUIState.LoadSuccess) {

            val nNewStock = currentState.currentStateMedicine.medicineValue.stock + 1

            val updatedMedicine = currentState.currentStateMedicine.medicineValue.copy(stock = nNewStock)

            // Met à jour l'état avec le nouvel objet Medicine modifié
            //_uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(updatedMedicine)

            _uiStateMedicineDetail.update{ currentStateP ->
                currentStateP.copy(
                    currentStateMedicine = CurrentMedicineUIState.LoadSuccess(updatedMedicine)
                )
            }

            checkFormError()

        }

    }

    fun decrementStock(){

        val currentState = _uiStateMedicineDetail.value

        // Cette condition devrait toujours être vraie lors de l'appel à cette fonction
        if (currentState.currentStateMedicine is CurrentMedicineUIState.LoadSuccess) {

            var nNewStock = currentState.currentStateMedicine.medicineValue.stock - 1
            if (nNewStock < 0) {
                nNewStock = 0
            }

            val updatedMedicine = currentState.currentStateMedicine.medicineValue.copy(stock = nNewStock)

            // Met à jour l'état avec le nouvel objet Medicine modifié
            //_uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(updatedMedicine)

            _uiStateMedicineDetail.update{ currentStateP ->
                currentStateP.copy(
                    currentStateMedicine = CurrentMedicineUIState.LoadSuccess(updatedMedicine)
                )
            }

            checkFormError()
        }

    }

    fun updateOrInsertMedicine() {

        val currentState = _uiStateMedicineDetail.value
        if (currentState.currentStateMedicine is CurrentMedicineUIState.LoadSuccess) {

            // Pas d'erreur de saisie
            val formError = getFormError()
            if (formError==null){

                // On fait l'update
                val updatedMedicine = currentState.currentStateMedicine.medicineValue

                viewModelScope.launch {

                    val flowResult : Flow<ResultCustom<String>>
                    if (_isAddMode){
                        flowResult = stockRepository.addMedicine(
                            medicine = updatedMedicine,
                            author = userRepository.getCurrentUser()
                        )
                    }
                    else{
                        flowResult = stockRepository.updateMedicine(
                            updatedMedicine = updatedMedicine,
                            author = userRepository.getCurrentUser()
                        )
                    }

                    flowResult.collect { resultFlow ->

                        // En fonction du résultat
                        when (resultFlow) {

                            // Transmission au UIState dédié

                            // Echec du au réseau
                            is ResultCustom.Failure -> {

                                // Récupération du message d'erreur
                                val sError = resultFlow.errorMessage?:""

                                // Affiche la fenêtre d'erreur
                                //_uiStateMedicineDetail.value = MedicineDetailUIState.Error(sError)

                                _uiStateMedicineDetail.update{ currentState ->
                                    currentState.copy(
                                        currentStateMedicine = CurrentMedicineUIState.ValidateError(sError),
                                        formError = null,
                                    )
                                }

                            }

                            // En chargement
                            is ResultCustom.Loading -> {
                                // Propagation du chargement
                                //_uiStateMedicineDetail.value = MedicineDetailUIState.IsLoading

                                _uiStateMedicineDetail.update{ currentState ->
                                    currentState.copy(
                                        currentStateMedicine = CurrentMedicineUIState.IsLoading,
                                        formError = null,
                                    )
                                }
                            }

                            // Succès
                            is ResultCustom.Success -> {
                                //_uiStateMedicineDetail.value = MedicineDetailUIState.ValidateSuccess

                                _uiStateMedicineDetail.update{ currentState ->
                                    currentState.copy(
                                        currentStateMedicine = CurrentMedicineUIState.ValidateSuccess,
                                        formError = null,
                                    )
                                }
                            }

                        }

                    }

                }

            }
            else{
                // Affichage des erreurs
                _uiStateMedicineDetail.update{ currentStateP ->
                    currentStateP.copy(
                        formError = null
                    )
                }
            }




        }


    }

    // Initialise un nouveau médicament
    fun initNewMedicine() {

        _isAddMode = true

        val newMedicine = Medicine(
            id = UUID.randomUUID().toString(),
            name = "",
            stock = 0,
            oAisle = Aisle(id="", name = ""),
            histories = mutableListOf()
        )

        //_uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(newMedicine,null)

        _uiStateMedicineDetail.update{ currentState ->
            currentState.copy(
                currentStateMedicine = CurrentMedicineUIState.LoadSuccess(newMedicine),
                formError = null,
            )
        }


    }

    fun bAddMode(): Boolean {
        return _isAddMode
    }

    fun onInputNameChanged(sInputNameP: String) {

        val currentState = _uiStateMedicineDetail.value
        if (currentState.currentStateMedicine is CurrentMedicineUIState.LoadSuccess) {

            val updatedMedicine = currentState.currentStateMedicine.medicineValue.copy(name = sInputNameP)
            //_uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(updatedMedicine)

            _uiStateMedicineDetail.update{ currentStateP ->
                currentStateP.copy(
                    currentStateMedicine = CurrentMedicineUIState.LoadSuccess(updatedMedicine),
                    formError = null,
                )
            }

            checkFormError()

        }

    }


    fun onInputAisleChanged(sInputAisleP: String) {

        val currentState = _uiStateMedicineDetail.value
        if (currentState.currentStateMedicine is CurrentMedicineUIState.LoadSuccess) {

            val updatedAisle =  currentState.currentStateMedicine.medicineValue.oAisle.copy(
                name = sInputAisleP
            )
            val updatedMedicine =  currentState.currentStateMedicine.medicineValue.copy(oAisle = updatedAisle)

            //_uiStateMedicineDetail.value = MedicineDetailUIState.LoadSuccess(updatedMedicine)

            _uiStateMedicineDetail.update{ currentStateP ->
                currentStateP.copy(
                    currentStateMedicine = CurrentMedicineUIState.LoadSuccess(updatedMedicine),
                    formError = null,
                )
            }

            checkFormError()

        }

    }


    // Vérifie les erreurs du formulaire en cours de saisie
    private fun checkFormError() {

        // Mise à jour des erreurs
        val formError = getFormError()

        _uiStateMedicineDetail.update{ currentState ->
            currentState.copy(
                formError = formError,
            )
        }


    }

    private fun getFormError (): FormErrorAddMedicine? {

        // TODO Denis : Je recopie souvent ces 2 lignes
        val currentState = _uiStateMedicineDetail.value
        if (currentState.currentStateMedicine is CurrentMedicineUIState.LoadSuccess) {

            if (currentState.currentStateMedicine.medicineValue.name.isEmpty()){
                return FormErrorAddMedicine.NameError
            }

            if (currentState.currentStateMedicine.medicineValue.oAisle.name.isEmpty()){
                return FormErrorAddMedicine.AisleError("Please select an aisle")
            }
            else{
               // TODO JG : Ajouter l'existance de l'allée
            }

            // En création
            if (_isAddMode){
                // On ne peut pas mettre un stock à zéro
                if (currentState.currentStateMedicine.medicineValue.stock == 0){
                    return FormErrorAddMedicine.StockError
                }
            }



            return null

        }



        return null

    }


}