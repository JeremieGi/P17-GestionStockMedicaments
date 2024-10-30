package com.openclassrooms.rebonnte

import android.content.Context
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockAPI
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import com.openclassrooms.rebonnte.repository.user.UserFakeAPI
import org.junit.Before
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StockRepositoryTest {

    // TODO Denis Question : Classe de test très longue : je peux la découper en plusieurs

    private lateinit var cutStockRepository : StockRepository // Class Under Test

    // Paramètres du repository
    private lateinit var mockAPI: StockAPI
    private lateinit var mockInjectedContext : InjectedContext

    // Contexte du test (mocké)
    private lateinit var mockContext: Context

    // Mock de l'objet medicine
    private lateinit var mockMedicine: Medicine

    /**
     * Création des mocks
     */
    @Before
    fun createRepositoryWithMockedParamaters() {
        mockAPI = mockk()
        mockInjectedContext = mockk()
        mockContext = mockk()
        mockMedicine = mockk()
        cutStockRepository = StockRepository(mockAPI,mockInjectedContext)
    }

    /**
     * Chargement de tous les allées : Succès
     */
    @Test
    fun loadAllAisles_success()  = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Liste des allées
        val mockListAisles  = StockFakeAPI.initFakeAisles()
        coEvery {
            mockAPI.loadAllAisles()
        } returns flowOf(ResultCustom.Success(mockListAisles))

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Aisle>>>()
        val jobCollector = launch {
            cutStockRepository.flowAisles.collect { result ->
                resultList.add(result)
            }
        }

        // Test réel de la fonction
        val jobCut = launch {
            cutStockRepository.loadAllAisles()
        }

        // Attend que toutes les couroutines en attente s'executent
        jobCut.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockAPI.loadAllAisles()
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(1, resultList.size)

        val expectedResult = ResultCustom.Success(mockListAisles)
        assertEquals(expectedResult,resultList[0])

        // Cancel the collection job
        jobCollector.cancel()
        jobCut.cancel()
    }

    /**
     * Chargement de tous les allées : Echec pas de connexion
     */
    @Test
    fun loadAllAisles_NoInternetConnexion()  = runTest {

        // definition des mocks

        // Pas de connexion Internet
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns false

        // Configurer le comportement de injectedContext pour getContext()
        coEvery {
            mockInjectedContext.getContext()
        } returns mockContext

        // Configurer le comportement de injectedContext pour getString()
        coEvery {
            mockContext.getString(R.string.no_network)
        } returns "No Network Connection"

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Aisle>>>()
        val jobCollector = launch {
            cutStockRepository.flowAisles.collect { result ->
                resultList.add(result)
            }
        }

        // Test réel de la fonction
        val jobCut = launch {
            cutStockRepository.loadAllAisles()
        }

        // Attend que toutes les couroutines en attente s'executent
        jobCut.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockInjectedContext.getContext()
            mockContext.getString(any())
        }

        // Une valeur reçue en erreur
        assertEquals(1, resultList.size)
        assert(resultList[0] is ResultCustom.Failure)

        // Cancel the collection job
        jobCollector.cancel()
        jobCut.cancel()
    }


    /**
     * Chargement de tous les médicaments : Succès
     */
    @Test
    fun loadAllMedicines_success()  = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Liste des médicaments
        val mockListMedicines  = StockFakeAPI.initFakeMedicines()
        coEvery {
            mockAPI.loadAllMedicines(any(), any())
        } returns flowOf(ResultCustom.Success(mockListMedicines))

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Medicine>>>()
        val jobCollector = launch {
            cutStockRepository.flowMedicines.collect { result ->
                resultList.add(result)
            }
        }

        // Test réel de la fonction
        val jobCut = launch {
            cutStockRepository.loadAllMedicines("",StockRepository.EnumSortedItem.NONE)
        }

        // Attend que toutes les couroutines en attente s'executent
        jobCut.join()
        //jobCollector.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockAPI.loadAllMedicines("",StockRepository.EnumSortedItem.NONE)
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(1, resultList.size)

        val expectedResult = ResultCustom.Success(mockListMedicines)
        assertEquals(expectedResult,resultList[0])

        // Cancel the collection job
        jobCollector.cancel()
        jobCut.cancel()
    }

    /**
     * Chargement de tous les médicaments : Echec pas de connexion
     */
    @Test
    fun loadAllMedicines_NoInternetConnexion()  = runTest {

        // definition des mocks

        // Pas de connexion Internet
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns false

        // Configurer le comportement de injectedContext pour getContext()
        coEvery {
            mockInjectedContext.getContext()
        } returns mockContext

        // Configurer le comportement de injectedContext pour getString()
        coEvery {
            mockContext.getString(R.string.no_network)
        } returns "No Network Connection"

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<List<Medicine>>>()
        val jobCollector = launch {
            cutStockRepository.flowMedicines.collect { result ->
                resultList.add(result)
            }
        }

        // Test réel de la fonction
        val jobCut = launch {
            cutStockRepository.loadAllMedicines("",StockRepository.EnumSortedItem.NONE)
        }

        // Attend que toutes les couroutines en attente s'executent
        jobCut.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockInjectedContext.getContext()
            mockContext.getString(any())
        }

        // Une valeur reçue en erreur
        assertEquals(1, resultList.size)
        assert(resultList[0] is ResultCustom.Failure)

        // Cancel the collection job
        jobCollector.cancel()
        jobCut.cancel()
    }

    /**
     * Ajout d'une allée avec succès
     */
    @Test
    fun addAisle_success() = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getContext()
        } returns mockContext


        val successValTest = "Test success"
        coEvery {
            mockAPI.addAisle(any())
        } returns flowOf(ResultCustom.Success(successValTest))

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<String>>()
        val mockAisle = Aisle("idTest","nameTest")
        val jobCut = launch {
            cutStockRepository.addAisle(mockAisle).collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
        jobCut.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockAPI.addAisle(any())
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(2, resultList.size)

        assertEquals(ResultCustom.Loading,resultList[0])

        val expectedResult = ResultCustom.Success(successValTest)
        assertEquals(expectedResult,resultList[1])

        // Cancel the collection job
        jobCut.cancel()

    }

    /**
     * Ajout d'une allée  sans connexion Internet
     */
    @Test
    fun addAisle_NoInternetConnexion() = runTest {

        // definition des mocks

        // Connexion Internet => erreur
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns false

        // Configurer le comportement de context pour getString()
        coEvery {
            mockContext.getString(any())
        } returns ""

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getContext()
        } returns mockContext

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<String>>()
        val mockAisle = Aisle("idTest","nameTest")
        val jobCut = launch {
            cutStockRepository.addAisle(mockAisle).collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
        jobCut.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockContext.getString(any())
            mockInjectedContext.getContext()
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(1, resultList.size)

        assert(resultList[0] is ResultCustom.Failure)

        // Cancel the collection job
        jobCut.cancel()

    }


    /**
     * Ajout d'un médicament avec succès
     */
    @Test
    fun addMedicine_success() = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getContext()
        } returns mockContext

        // Configurer le comportement de injectedContext pour getString()
        coEvery {
            mockContext.getString(R.string.creation)
        } returns "Creation"

        // Mock partiel de Medicine -  mockAPI.addMedicine renverra la valeur reçue en paramètre
        val medicineTest = Medicine(
            id = "idTest",
            name = "nameTest",
            stock = 5,
            oAisle = Aisle("idAisle","nameAisle"),
            histories = emptyList<History>().toMutableList()
        )
        val spyMedicine = spyk(medicineTest, recordPrivateCalls = true) // Enregistre les appels pour vérification
        coEvery {
            mockAPI.addMedicine(spyMedicine)
        } returns flowOf(ResultCustom.Success(spyMedicine))

        // Test de la fonction addMedicine
        val authorParam = User(id = "idTest", sName = "UserTest", sEmail = "")
        val resultList = cutStockRepository.addMedicine(spyMedicine, authorParam).toList()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockContext.getString(any())
            mockAPI.addMedicine(any())
            spyMedicine.addHistory(any())
        }

        // On attend les valeurs de mockAPI.loadAllEvents
        assertEquals(2, resultList.size)

        assertEquals(ResultCustom.Loading,resultList[0])

        val resultSuccess = resultList[1]
        if (resultSuccess is ResultCustom.Success){
            val resultMedicineWithHistory = resultSuccess.value
            assertEquals("Check history creation : ",1,resultMedicineWithHistory.histories.size)
        }
        else{
            assert(false) { "expected type ResultCustom.Success" }
        }

    }

    /**
     * Ajout d'un medicament sans connexion Internet
     */
    @Test
    fun addMedicine_NoInternetConnexion() = runTest {

        // definition des mocks

        // Connexion Internet => erreur
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns false

        // Configurer le comportement de context pour getString()
        coEvery {
            mockContext.getString(any())
        } returns ""

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getContext()
        } returns mockContext

        // Créer le collecteur du flow du repository
        val resultList = mutableListOf<ResultCustom<Medicine>>()

        val listMedicines  = StockFakeAPI.initFakeMedicines()
        val medicineParam = listMedicines[0]

        val mockListUsers  = UserFakeAPI.initFakeUsers()
        val mockUser = mockListUsers[0]

        val jobCut = launch {
            cutStockRepository.addMedicine(medicineParam,mockUser).collect { result ->
                resultList.add(result)
            }
        }

        // Attend que toutes les couroutines en attente s'executent
        jobCut.join()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockContext.getString(any())
            mockInjectedContext.getContext()
        }

        // On attend les valeurs
        assertEquals("One value expected in the result flow",1, resultList.size)

        assert(resultList[0] is ResultCustom.Failure)

        // Cancel the collection job
        jobCut.cancel()

    }

    /**
    * Mise à jour d'un médicament avec succès
    */
    @Test
    fun updateMedicine_success() = runTest {

        // definition des mocks

        // Connexion Internet OK
        coEvery {
            mockInjectedContext.isInternetAvailable()
        } returns true

        // Il faut aussi mocker l'appel à la fonction sDiff de la classse Medicine (pour que le test ne soit pas rouge en cas de regression sur cette fonction)
        coEvery {
            mockMedicine.sDiff(any(),any())
        } returns "mocked diff"

        // Configurer le comportement de context
        coEvery {
            mockInjectedContext.getContext()
        } returns mockContext

        // Mock partiel de Medicine -  mockAPI.addMedicine renverra la valeur reçue en paramètre
        val authorParam = User(id = "idTest", sName = "UserTest", sEmail = "")
        val medicineTest = Medicine(
            id = "idTest",
            name = "nameTest",
            stock = 5,
            oAisle = Aisle("idAisle","nameAisle"),
            histories = listOf(
                History(author = authorParam, details="first historic line")
            ).toMutableList()
        )
        val spyMedicine = spyk(medicineTest, recordPrivateCalls = true) // Enregistre les appels pour vérification
        coEvery {
            mockAPI.updateMedicine(spyMedicine)
        } returns flowOf(ResultCustom.Success(spyMedicine))

        // Test de la fonction updateMedicine

        val resultList = cutStockRepository.updateMedicine(
            oldMedicine = mockMedicine,
            updatedMedicine = spyMedicine,
            author = authorParam
        ).toList()

        // coVerify : s'assure que les fonctions des mocks ont été appelées
        coVerify {
            mockInjectedContext.isInternetAvailable()
            mockAPI.updateMedicine(any())
            spyMedicine.addHistory(any())
        }

        // On attend les valeurs
        assertEquals(2, resultList.size)

        assertEquals(ResultCustom.Loading,resultList[0])

        val resultSuccess = resultList[1]
        if (resultSuccess is ResultCustom.Success){
            val resultMedicineWithHistory = resultSuccess.value
            assertEquals("Check history - one line more : ",2,resultMedicineWithHistory.histories.size)
        }
        else{
            assert(false) { "expected type ResultCustom.Success" }
        }

    }



    // Je ne teste pas toutes les méthodes car certaines ne font que passe-plat comme deleteMedecineById par exemple

}