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

    private lateinit var _oldMedicine : Medicine

    // Liste chargée une fois (pour autocompletion lors de la saisie des allées)
    private var _listExistingAisles : List<Aisle> = emptyList()


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

                        _oldMedicine = medicine // Conservation de la valeur initiale pour vérifier les modifications


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

                // Current user
                val currentUser = userRepository.getCurrentUser()

                // Utilisateur non identifié
                if (currentUser==null){

                    // Ce cas ne devrait jamais se produire
                    _uiStateMedicineDetail.update{ currentStateParam ->
                        currentStateParam.copy(
                            // TODO Denis : Question : Au lieu d'injecter le context dans le ViewModel je crée un état supplémentaire CurrentMedicineUIState.ValidateErrorUserunknown
                            currentStateMedicine = CurrentMedicineUIState.ValidateErrorUserUnlogged,
                            formError = null,
                        )
                    }

                }
                else{

                    // On fait l'update
                    val updatedMedicine = currentState.currentStateMedicine.medicineValue

                    viewModelScope.launch {

                        val flowResult : Flow<ResultCustom<Medicine>>
                        if (_isAddMode){
                            flowResult = stockRepository.addMedicine(
                                medicine = updatedMedicine,
                                author = currentUser
                            )
                        }
                        else{
                            flowResult = stockRepository.updateMedicine(
                                oldMedicine = _oldMedicine,
                                updatedMedicine = updatedMedicine,
                                author = currentUser
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
                                            currentStateMedicine = CurrentMedicineUIState.ValidateErrorRepository(sError),
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

        _uiStateMedicineDetail.update{ currentState ->
            currentState.copy(
                currentStateMedicine = CurrentMedicineUIState.LoadSuccess(newMedicine),
                formError = null,
            )
        }

        // Lance le chargement de toutes les allées pour aide à la saisie
        // (uniquement en mode Add pour ne pas faire des appels réseaux inutiles)
        observeFlowAllAisles()
        loadAllAisle()


    }


    private fun observeFlowAllAisles() {

        viewModelScope.launch {

            stockRepository.flowAisles.collect { resultFlow ->

                // TODO Denis : Revue : Chargement de la liste des allées lors de l'ajout d'une nouvelle allée

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure -> {
                        // Propagation du message d'erreur

                    }

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement

                    }

                    // Succès
                    is ResultCustom.Success -> {
                        _listExistingAisles = resultFlow.value
                    }

                }

            }

        }
    }

    private fun loadAllAisle() {
        viewModelScope.launch {
            stockRepository.loadAllAisles()
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

            val currentAisle = aisleFindByName(sInputAisleP)
            val updatedAisle : Aisle
            // Le nom saisi n'est pas un nom d'allée connu
            if ( currentAisle == null ) {
                updatedAisle =  currentState.currentStateMedicine.medicineValue.oAisle.copy(
                    id = "",
                    name = sInputAisleP
                )
            }
            else{
                updatedAisle =  currentState.currentStateMedicine.medicineValue.oAisle.copy(
                    id = currentAisle.id,
                    name = currentAisle.name
                )
            }

            val updatedMedicine =  currentState.currentStateMedicine.medicineValue.copy(oAisle = updatedAisle)


            _uiStateMedicineDetail.update{ currentStateP ->
                currentStateP.copy(
                    currentStateMedicine = CurrentMedicineUIState.LoadSuccess(updatedMedicine),
                    formError = null,
                )
            }

            checkFormError()

        }

    }

    private fun aisleFindByName(sAisleInputNameP : String) : Aisle? {

        return _listExistingAisles.find { it.name == sAisleInputNameP }

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

    // T012 - Ajout de stock - Contrôle de saisie
    // Renvoie les erreurs de formulaire (champs obligatoires)
    private fun getFormError (): FormErrorAddMedicine? {

        val currentState = _uiStateMedicineDetail.value
        if (currentState.currentStateMedicine is CurrentMedicineUIState.LoadSuccess) {

            if (currentState.currentStateMedicine.medicineValue.name.isEmpty()){
                return FormErrorAddMedicine.NameError
            }

            if (currentState.currentStateMedicine.medicineValue.oAisle.name.isEmpty()){
                return FormErrorAddMedicine.AisleErrorEmpty
            }
            else{

                // TODO Denis JG prio 3 => AutocompleteTextView non dispo en compose (Voir meilleure solution)

                // en mode ajout uniquement
                if (_isAddMode){
                    // Vérifier l'existence de l'allée
                    if ( aisleFindByName(currentState.currentStateMedicine.medicineValue.oAisle.name) == null ) {
                        return FormErrorAddMedicine.AisleErrorNoExist
                    }
                }


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