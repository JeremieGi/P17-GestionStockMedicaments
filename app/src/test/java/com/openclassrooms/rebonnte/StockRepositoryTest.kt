package com.openclassrooms.rebonnte

import android.content.Context
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockAPI
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.repository.stock.StockRepository
import org.junit.Before
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StockRepositoryTest {

    private lateinit var cutStockRepository : StockRepository // Class Under Test

    // Paramètres du repository
    private lateinit var mockAPI: StockAPI
    private lateinit var mockInjectedContext : InjectedContext

    // Contexte du test (mocké)
    private lateinit var mockContext: Context

    /**
     * Création des mocks
     */
    @Before
    fun createRepositoryWithMockedParamaters() {
        mockAPI = mockk()
        mockInjectedContext = mockk()
        mockContext = mockk()
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
        jobCollector.join()

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

        // Configurer le comportement de injectedContext pour getInjectedContext()
        coEvery {
            mockInjectedContext.getInjectedContext()
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
            mockInjectedContext.getInjectedContext()
            mockContext.getString(any())
        }

        // Une valeur reçue en erreur
        assertEquals(1, resultList.size)
        assert(resultList[0] is ResultCustom.Failure)

        // Cancel the collection job
        jobCollector.cancel()
        jobCut.cancel()
    }

}