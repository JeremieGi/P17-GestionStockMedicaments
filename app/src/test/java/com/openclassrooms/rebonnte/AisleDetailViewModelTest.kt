package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleDetailViewModel
import com.openclassrooms.rebonnte.ui.aisle.detail.CurrentAisleUIState
import com.openclassrooms.rebonnte.ui.aisle.detail.FormErrorAddAisle
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AisleDetailViewModelTest {

    // Utilisation de MockK pour le mock du repository
    @MockK
    lateinit var mockStockRepository: StockRepository

    // ViewModel que nous allons tester
    private lateinit var cutViewModel: AisleDetailViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined) // Utile pour définir un dispatcher en mode test
        MockKAnnotations.init(this)
        cutViewModel = AisleDetailViewModel(mockStockRepository)
    }

    @Test
    fun add_success() = runTest {

        initAddTestMode()

        // Simulation de saisie
        val sAisleName = "AisleNameTest"
        cutViewModel.onInputNameChanged(sAisleName)

        val currentStateAfterName = cutViewModel.uiStateAisleDetail.value.currentStateAisle
        if (currentStateAfterName is CurrentAisleUIState.LoadSuccess) {
            val currentAisle = currentStateAfterName.aisle
            assertEquals("Init name : ",currentAisle.name, sAisleName)

        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }

        // Pas d'erreur de formulaire
        val currentFormErrorAfterName = cutViewModel.uiStateAisleDetail.value.formError
        assertEquals("No form error : ",currentFormErrorAfterName, null)


        // Appeler la méthode add - Ici il y a un appel au repository

        // Simuler un succès lors de l'ajout
        coEvery { mockStockRepository.addAisle(any()) } returns flowOf(ResultCustom.Success(""))

        // Appeler la méthode add
        cutViewModel.addAisle()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockStockRepository.addAisle(any())
        }

        // Vérifier que l'UIState a bien été mis à jour avec le succès
        val expectedState = CurrentAisleUIState.ValidateSuccess
        assertEquals(expectedState, cutViewModel.uiStateAisleDetail.value.currentStateAisle)

    }

    @Test
    fun add_error_AisleAlreadyExist() = runTest {

        initAddTestMode()

        // Simulation de saisie d'une allée déjà existante
        val sAisleName = StockFakeAPI.CTE_AISLE_NAME
        cutViewModel.onInputNameChanged(sAisleName)

        val currentStateAfterName = cutViewModel.uiStateAisleDetail.value.currentStateAisle
        if (currentStateAfterName is CurrentAisleUIState.LoadSuccess) {
            val currentAisle = currentStateAfterName.aisle
            assertEquals("Init name : ",currentAisle.name, sAisleName)
        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }

        // Erreur de formulaire
        val currentFormErrorAfterName = cutViewModel.uiStateAisleDetail.value.formError
        assertEquals("Error name already exist : ",currentFormErrorAfterName, FormErrorAddAisle.NameErrorAlreadyExist)

    }

    @Test
    fun add_error_AisleNameEmpty() = runTest {

        initAddTestMode()

        // Allée vide
        val sAisleName = ""
        cutViewModel.onInputNameChanged(sAisleName)

        val currentStateAfterName = cutViewModel.uiStateAisleDetail.value.currentStateAisle
        if (currentStateAfterName is CurrentAisleUIState.LoadSuccess) {
            val currentAisle = currentStateAfterName.aisle
            assertEquals("Init name : ",currentAisle.name, sAisleName)
        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }

        // Erreur de formulaire
        val currentFormErrorAfterName = cutViewModel.uiStateAisleDetail.value.formError
        assertEquals("Error no name : ",currentFormErrorAfterName, FormErrorAddAisle.NameErrorEmpty)

    }


    private fun initAddTestMode(){

        // Préparer des données fictives
        val listAisles = StockFakeAPI.initFakeAisles()

        // Créer un MutableSharedFlow pour simuler le flow dans le test
        val aislesFlow = MutableSharedFlow<ResultCustom<List<Aisle>>>()

        // Configurer le mock pour renvoyer le MutableSharedFlow
        every { mockStockRepository.flowAisles } returns aislesFlow.asSharedFlow()

        // Mock de la méthode loadAllAisles
        coEvery { mockStockRepository.loadAllAisles() } coAnswers {
            // Simuler le comportement de la méthode
            aislesFlow.emit(ResultCustom.Success(listAisles))
        }

        cutViewModel.initNewAisle()

        // Vérifie que l'état est LoadSuccess et que l'ojbet en cours de création est bien initialisé
        val currentState = cutViewModel.uiStateAisleDetail.value.currentStateAisle
        if (currentState is CurrentAisleUIState.LoadSuccess) {
            val currentAisle = currentState.aisle
            Assert.assertNotEquals("Init ID : ",currentAisle.id, "")
            assertEquals("Init name : ",currentAisle.name, "")
            assertEquals("Mode Add : ", true, cutViewModel.bAddMode())
        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }

    }

}

