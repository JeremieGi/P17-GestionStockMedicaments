package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import com.openclassrooms.rebonnte.repository.user.UserFakeAPI
import com.openclassrooms.rebonnte.repository.user.UserRepository
import com.openclassrooms.rebonnte.ui.medicine.detail.CurrentMedicineUIState
import com.openclassrooms.rebonnte.ui.medicine.detail.FormErrorAddMedicine
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailUIState
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailViewModel
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MedicineDetailViewModelTest {

    // Utilisation de MockK pour le mock du repository
    @MockK
    lateinit var mockStockRepository: StockRepository
    @MockK
    lateinit var mockUserRepository: UserRepository

    // ViewModel que nous allons tester
    private lateinit var cutViewModel: MedicineDetailViewModel

    // Préparer des données fictives
    private val _listAisles = StockFakeAPI.initFakeAisles()
    private val _listMedicines = StockFakeAPI.initFakeMedicines()
    private val _listUsers = UserFakeAPI.initFakeUsers()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined) // Utile pour définir un dispatcher en mode test
        MockKAnnotations.init(this)
        cutViewModel = MedicineDetailViewModel(mockStockRepository,mockUserRepository)
    }

    private fun initAddTestMode(){



        // Créer un MutableSharedFlow pour simuler le flow dans le test
        val aislesFlow = MutableSharedFlow<ResultCustom<List<Aisle>>>()

        // Configurer le mock pour renvoyer le MutableSharedFlow
        every { mockStockRepository.flowAisles } returns aislesFlow.asSharedFlow()

        // Mock de la méthode loadAllAisles
        coEvery { mockStockRepository.loadAllAisles() } coAnswers {
            // Simuler le comportement de la méthode
            aislesFlow.emit(ResultCustom.Success(_listAisles))
        }

        cutViewModel.initNewMedicine()

        // Vérifie que l'état est LoadSuccess et que l'ojbet en cours de création est bien initialisé
        val currentState = cutViewModel.uiStateMedicineDetail.value.currentStateMedicine
        if (currentState is CurrentMedicineUIState.LoadSuccess) {
            val currentMedicine = currentState.medicineValue
            Assert.assertNotEquals("Init ID : ",currentMedicine.id, "")
            assertEquals("Init name : ",currentMedicine.name, "")
            assertEquals("Mode Add : ", true, cutViewModel.bAddMode())
        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }

        // Vérifie que la liste d'allée est chargée
        val currentAislesLoaded = cutViewModel.uiStateMedicineDetail.value.listAisles
        assertEquals("Check the loading of aisle",_listAisles.size,currentAislesLoaded?.size)


    }

    // ---------- Mode d'ajout d'une allée ----------

    @Test
    fun addMode_AddSuccess() = runTest {

        initAddTestMode()

        // Simulation de saisie du nom
        val sMedicineName = "MedicineNameTest"
        cutViewModel.onInputNameChanged(sMedicineName)

        val currentStateAfterName = cutViewModel.uiStateMedicineDetail.value.currentStateMedicine
        if (currentStateAfterName is CurrentMedicineUIState.LoadSuccess) {
            val currentName = currentStateAfterName.medicineValue.name
            assertEquals("Init name : ",currentName, sMedicineName)

        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }


        // Simulation de saisie de l'allée (existante)
        val aisleNameInput = _listAisles[0]
        cutViewModel.onInputAisleChanged(aisleNameInput.name)

        val currentStateAfterAisle = cutViewModel.uiStateMedicineDetail.value.currentStateMedicine
        if (currentStateAfterAisle is CurrentMedicineUIState.LoadSuccess) {
            val currentAisle = currentStateAfterAisle.medicineValue.oAisle
            assertEquals("Select aisle name : ",aisleNameInput.name,currentAisle.name)
            assertEquals("Select existing aisle ID : ",aisleNameInput.id,currentAisle.id)

        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }

        // Stock de 1
        cutViewModel.incrementStock()
        val currentStateIncrementStock = cutViewModel.uiStateMedicineDetail.value.currentStateMedicine
        if (currentStateIncrementStock is CurrentMedicineUIState.LoadSuccess) {
            val currentStock = currentStateIncrementStock.medicineValue.stock
            assertEquals("Select stock : ",1,currentStock)

        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }


        // Appeler la méthode add - Ici il y a un appel au repository

        // Simuler un succès lors de l'ajout

        coEvery { mockStockRepository.addMedicine(any(),any()) } returns flowOf(ResultCustom.Success(_listMedicines[0]))
        coEvery { mockUserRepository.getCurrentUser() } returns _listUsers[0]


        // Appeler la méthode à tester
        cutViewModel.updateOrInsertMedicine()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockStockRepository.addMedicine(any(),any())
            mockUserRepository.getCurrentUser()
        }

        // Vérifier que l'UIState a bien été mis à jour avec le succès
        val expectedState = CurrentMedicineUIState.ValidateSuccess
        assertEquals(expectedState, cutViewModel.uiStateMedicineDetail.value.currentStateMedicine)

    }

    @Test
    fun addMode_error_MedicineNameEmpty() = runTest {

        initAddTestMode()

        // Simulation de saisie du nom vide
        val sMedicineName = ""
        cutViewModel.onInputNameChanged(sMedicineName)


        // Erreur de formulaire
        val currentFormErrorAfterName = cutViewModel.uiStateMedicineDetail.value.formError
        assertEquals("Error no name : ",currentFormErrorAfterName, FormErrorAddMedicine.NameError)

    }

    @Test
    fun addMode_error_AisleDoesntExist() = runTest {

        initAddTestMode()

        // Simulation de saisie du nom vide
        val sMedicineName = "MedicineNameTest"
        cutViewModel.onInputNameChanged(sMedicineName)

        // Simulation de saisie d'une allée inexistante
        val sAisleName = "unknownAisle"
        cutViewModel.onInputAisleChanged(sAisleName)


        // Erreur de formulaire
        val currentFormErrorAisleDoesntExist = cutViewModel.uiStateMedicineDetail.value.formError
        assertEquals("Unknown aisle detection : ",FormErrorAddMedicine.AisleErrorNoExist,currentFormErrorAisleDoesntExist)

    }

    @Test
    fun addMode_error_AisleEmpty() = runTest {

        initAddTestMode()

        // Simulation de saisie du nom vide
        val sMedicineName = "MedicineNameTest"
        cutViewModel.onInputNameChanged(sMedicineName)

        // Pas de saisie d'une allée
        val sAisleName = ""
        cutViewModel.onInputAisleChanged(sAisleName)


        // Erreur de formulaire
        val currentFormError = cutViewModel.uiStateMedicineDetail.value.formError
        assertEquals("Unknown aisle detection : ",FormErrorAddMedicine.AisleErrorEmpty,currentFormError)

    }


    @Test
    fun addMode_error_Stock0() = runTest {

        initAddTestMode()

        // Simulation de saisie du nom vide
        val sMedicineName = "MedicineNameTest"
        cutViewModel.onInputNameChanged(sMedicineName)

        // Simulation de saisie de l'allée (existante)
        val aisleNameInput = _listAisles[0]
        cutViewModel.onInputAisleChanged(aisleNameInput.name)

        // Pas de saisie de stock

        // Erreur de formulaire
        val currentFormError = cutViewModel.uiStateMedicineDetail.value.formError
        assertEquals("Unknown aisle detection : ",FormErrorAddMedicine.StockError,currentFormError)

    }



    // ---------- Mode d'affichage simple ----------

    @Test
    fun detailMode_loadSuccess() = runTest {

        // Préparer des données fictives
        val medicine1 = _listMedicines[0]

        // Simuler le succès dans le repository
        coEvery { mockStockRepository.loadMedicineByID(any()) } returns flowOf(ResultCustom.Success(medicine1))

        // Créer le collecteur du flow du repository
        val emittedStates = mutableListOf<MedicineDetailUIState>() // Liste pour capturer les résultats émis
        val jobCollector = launch {
            cutViewModel.uiStateMedicineDetail.collect { result ->
                emittedStates.add(result)
            }
        }

        // Appel de la fonction pour charger le médicament par ID
        val jobCut = launch {
            cutViewModel.loadMedicineByID("idMedicine")
        }

        // Attente de la fin de la collecte
        jobCut.join()

        // Assertions pour vérifier que l'état de l'UI est mis à jour pour le succès
        assertEquals(2, emittedStates.size)

        // IsLoading est l'état initial du Ui State
        val expectedLoadResult = MedicineDetailUIState(
            currentStateMedicine = CurrentMedicineUIState.IsLoading,
            formError = null,
            listAisles = null
        )
        assertEquals(expectedLoadResult,emittedStates[0])

        // Ensuite le succès Mocké
        val expectedSuccessResult = MedicineDetailUIState(
            currentStateMedicine = CurrentMedicineUIState.LoadSuccess(medicine1),
            formError = null,
            listAisles = null
        )
        assertEquals(expectedSuccessResult,emittedStates[1])


        // Annuler l'observation
        jobCollector.cancel()

    }

    @Test
    fun detailMode_loadError() = runTest {

        // Préparer des données fictives
        val sError = "TestError"

        // Simuler le succès dans le repository
        coEvery { mockStockRepository.loadMedicineByID(any()) } returns flowOf(ResultCustom.Failure(sError))

        // Créer le collecteur du flow du repository
        val emittedStates = mutableListOf<MedicineDetailUIState>() // Liste pour capturer les résultats émis
        val jobCollector = launch {
            cutViewModel.uiStateMedicineDetail.collect { result ->
                emittedStates.add(result)
            }
        }

        // Appel de la fonction pour charger le médicament par ID
        val jobCut = launch {
            cutViewModel.loadMedicineByID("idMedicine")
        }

        // Attente de la fin de la collecte
        jobCut.join()

        // Assertions pour vérifier que l'état de l'UI est mis à jour pour le succès
        assertEquals(2, emittedStates.size)

        // IsLoading est l'état initial du Ui State
        val expectedLoadResult = MedicineDetailUIState(
            currentStateMedicine = CurrentMedicineUIState.IsLoading,
            formError = null,
            listAisles = null
        )
        assertEquals(expectedLoadResult,emittedStates[0])

        // Ensuite le succès Mocké
        val expectedSuccessResult = MedicineDetailUIState(
            currentStateMedicine = CurrentMedicineUIState.LoadError(sError),
            formError = null,
            listAisles = null
        )
        assertEquals(expectedSuccessResult,emittedStates[1])


        // Annuler l'observation
        jobCollector.cancel()

    }


    @Test
    fun detailMode_blockingNegativeStock() = runTest {

        // Préparer des données fictives
        val medicine1 = _listMedicines[0]

        // Simuler le succès dans le repository
        coEvery { mockStockRepository.loadMedicineByID(any()) } returns flowOf(ResultCustom.Success(medicine1))

        // Créer le collecteur du flow du repository
        val emittedStates = mutableListOf<MedicineDetailUIState>() // Liste pour capturer les résultats émis
        val jobCollector = launch {
            cutViewModel.uiStateMedicineDetail.collect { result ->
                emittedStates.add(result)
            }
        }

        // Appel de la fonction pour charger le médicament par ID
        val jobCut = launch {
            cutViewModel.loadMedicineByID("idMedicine")
        }

        // Attente de la fin de la collecte
        jobCut.join()

        // Assertions pour vérifier que l'état de l'UI est mis à jour pour le succès
        assertEquals(2, emittedStates.size)

        // IsLoading est l'état initial du Ui State
        val expectedLoadResult = MedicineDetailUIState(
            currentStateMedicine = CurrentMedicineUIState.IsLoading,
            formError = null,
            listAisles = null
        )
        assertEquals(expectedLoadResult,emittedStates[0])

        // Ensuite le succès Mocké
        val expectedSuccessResult = MedicineDetailUIState(
            currentStateMedicine = CurrentMedicineUIState.LoadSuccess(medicine1),
            formError = null,
            listAisles = null
        )
        assertEquals(expectedSuccessResult,emittedStates[1])


        // Annuler l'observation
        jobCollector.cancel()

        // On décrementé le stock pour arriver à un stock en téhorie négatif mais bloqué à 0
        for (i in 0..medicine1.stock+10) {
            cutViewModel.decrementStock()
        }

        // Le stock reste à 0
        val currentStateIncrementStock = cutViewModel.uiStateMedicineDetail.value.currentStateMedicine
        if (currentStateIncrementStock is CurrentMedicineUIState.LoadSuccess) {
            val currentStock = currentStateIncrementStock.medicineValue.stock
            assertEquals("Stock stay 0 : ",0,currentStock)

        }
        else{
            assert(false) { "The current state would be LoadSuccess" }
        }
    }


}