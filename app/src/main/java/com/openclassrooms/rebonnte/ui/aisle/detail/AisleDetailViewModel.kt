package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AisleDetailViewModel @Inject constructor (
    private val stockRepository: StockRepository
): ViewModel() {

    private val _uiStateAisleDetail = MutableStateFlow(AisleDetailUIState())
    val uiStateAisleDetail : StateFlow<AisleDetailUIState> = _uiStateAisleDetail.asStateFlow() // Accès en lecture seule de l'extérieur

    private var _isAddMode = false

    // Liste chargée une fois
    private var _listExistingAisles : List<Aisle> = emptyList()

    /**
     * Permet de récupérer la liste des allées (pour véfication de la non-existence dans le formulaire)
     */
    private fun observeFlowAllAisles() {

        viewModelScope.launch {

            stockRepository.flowAisles.collect { resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure -> {
                        // Propagation du message d'erreur
                        _uiStateAisleDetail.update{ currentState ->
                            currentState.copy(
                                currentStateAisle = CurrentAisleUIState.LoadError(resultFlow.errorMessage),
                                formError = null,
                            )
                        }
                    }

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Pas de propagation du chargement
                    }

                    // Succès
                    is ResultCustom.Success -> {
                        _listExistingAisles = resultFlow.value
                    }

                }

            }

        }
    }

    /**
     * Lance le chargement de toutes les allées
     */
    private fun loadAllAisle() {
        viewModelScope.launch {
            stockRepository.loadAllAisles()
        }
    }

    /**
     * Chargement d'une allée par son ID
     */
    fun loadAisleByID (idAisle : String) {

        viewModelScope.launch {

            stockRepository.loadAisleByID(idAisle).collect{ resultFlow ->

                // En fonction du résultat
                when (resultFlow) {

                    // Echec
                    is ResultCustom.Failure ->
                        // Propagation du message d'erreur
                        _uiStateAisleDetail.update{ currentState ->
                            currentState.copy(
                                currentStateAisle = CurrentAisleUIState.LoadError(resultFlow.errorMessage),
                                formError = null,
                            )
                        }

                    // En chargement
                    is ResultCustom.Loading -> {
                        // Propagation du chargement
                        _uiStateAisleDetail.update{ currentState ->
                            currentState.copy(
                                currentStateAisle = CurrentAisleUIState.IsLoading,
                                formError = null,
                            )
                        }
                    }

                    // Succès
                    is ResultCustom.Success -> {

                        val aisle = resultFlow.value

                        _uiStateAisleDetail.update{ currentState ->
                            currentState.copy(
                                currentStateAisle = CurrentAisleUIState.LoadSuccess(aisle),
                                formError = null,
                            )
                        }

                    }


                }

            }

        }


    }

    /**
     * Initialisation du viewModel en mode ajout
     */
    fun initNewAisle() {

        _isAddMode = true   // Mode Ajout activé

        val newAisle = Aisle(
            id = UUID.randomUUID().toString(), // Génération d'un ID
            name = ""
        )

        _uiStateAisleDetail.update{ currentState ->
            currentState.copy(
                currentStateAisle = CurrentAisleUIState.LoadSuccess(newAisle),
                formError = null,
            )
        }

        // Chargement des allées au chargement du viewModel (pour contrôle de l'existence)
        observeFlowAllAisles()
        loadAllAisle()
    }

    /**
     * Retourne le mode Ajout ou Détail
     */
    fun bAddMode(): Boolean {
        return _isAddMode
    }

    /**
     * Méthode appelée à chaque modification du nom dans le formulaire de saisie
     */
    fun onInputNameChanged(sInputNameP: String) {

        val currentState = _uiStateAisleDetail.value
        if (currentState.currentStateAisle is CurrentAisleUIState.LoadSuccess) {

            val updatedMedicine = currentState.currentStateAisle.aisle.copy(name = sInputNameP)

            _uiStateAisleDetail.update{ currentStateP ->
                currentStateP.copy(
                    currentStateAisle = CurrentAisleUIState.LoadSuccess(updatedMedicine),
                    formError = null,
                )
            }

            checkFormError()

        }

    }

    /**
     * Vérifie les erreurs du formulaire en cours de saisie
     */
    private fun checkFormError() {

        // Mise à jour des erreurs
        val formError = getFormError()

        _uiStateAisleDetail.update{ currentState ->
            currentState.copy(
                formError = formError,
            )
        }


    }

    /**
     * Renvoie Vrai si l'allée saisie existe déjà
     */
    private fun aisleNameExist(sAisleInputNameP : String) : Boolean {

        // any, qui retourne true si un élément correspondant au critère existe, et false sinon.
        return _listExistingAisles.any { it.name == sAisleInputNameP }

    }

    /**
     * Renvoie les erreurs de formulaire (champs obligatoires)
     */
    private fun getFormError (): FormErrorAddAisle? {

        val currentState = _uiStateAisleDetail.value
        if (currentState.currentStateAisle is CurrentAisleUIState.LoadSuccess) {

            if (currentState.currentStateAisle.aisle.name.isEmpty()){
                return FormErrorAddAisle.NameErrorEmpty
            }
            else{
                // Vérifier que l'allée n'existe pas déjà
                if ( aisleNameExist(currentState.currentStateAisle.aisle.name) ) {
                    return FormErrorAddAisle.NameErrorAlreadyExist
                }
            }

            return null

        }

        return null

    }

    /**
     * Ajoute une allée si les donénes saisies sont correctes
     */
    fun addAisle() {

        val currentState = _uiStateAisleDetail.value
        if (currentState.currentStateAisle is CurrentAisleUIState.LoadSuccess) {

            // Pas d'erreur de saisie
            val formError = getFormError()
            if (formError==null){

                // On fait l'insert
                val aisleToInsert = currentState.currentStateAisle.aisle

                viewModelScope.launch {

                    val flowResult = stockRepository.addAisle(aisleToInsert)

                    flowResult.collect { resultFlow ->

                        // En fonction du résultat
                        when (resultFlow) {

                            // Transmission au UIState dédié

                            // Echec du au réseau
                            is ResultCustom.Failure -> {

                                // Récupération du message d'erreur
                                val sError = resultFlow.errorMessage?:""

                                // Affiche la fenêtre d'erreur

                                _uiStateAisleDetail.update{ currentState ->
                                    currentState.copy(
                                        currentStateAisle = CurrentAisleUIState.ValidateError(sError),
                                        formError = null,
                                    )
                                }

                            }

                            // En chargement
                            is ResultCustom.Loading -> {
                                // Propagation du chargement
                                //_uiStateMedicineDetail.value = MedicineDetailUIState.IsLoading

                                _uiStateAisleDetail.update{ currentState ->
                                    currentState.copy(
                                        currentStateAisle = CurrentAisleUIState.IsLoading,
                                        formError = null,
                                    )
                                }
                            }

                            // Succès
                            is ResultCustom.Success -> {
                                //_uiStateMedicineDetail.value = MedicineDetailUIState.ValidateSuccess

                                _uiStateAisleDetail.update{ currentState ->
                                    currentState.copy(
                                        currentStateAisle = CurrentAisleUIState.ValidateSuccess,
                                        formError = null,
                                    )
                                }
                            }

                        }

                    }

                }

            }
            else{
                _uiStateAisleDetail.update{ currentState ->
                    currentState.copy(
                        formError = formError,
                    )
                }
            }


        }

    }

}
