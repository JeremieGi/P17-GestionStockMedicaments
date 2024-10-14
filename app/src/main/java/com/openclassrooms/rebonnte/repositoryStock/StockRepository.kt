package com.openclassrooms.rebonnte.repositoryStock

import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.ResultCustom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val stockApi: StockAPI,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
){

    // TODO Denis : Je gère les pertes de connexion à Internet ?

    /**
     * Liste des médicaments
     */
    private var _flowMedecines = MutableSharedFlow<ResultCustom<List<Medicine>>>()
    val flowMedecines : SharedFlow<ResultCustom<List<Medicine>>>
        get() = _flowMedecines

    /**
     * Liste des allées
     */
    private var _flowAisles = MutableSharedFlow<ResultCustom<List<Aisle>>>()
    val flowAisles : SharedFlow<ResultCustom<List<Aisle>>>
        get() = _flowAisles


    enum class EnumSortedItem {
        NONE, NAME, STOCK
    }

    suspend fun loadAllMedecines(sFilterNameP : String, eSortItemP : EnumSortedItem){

        withContext(Dispatchers.IO) {
            stockApi.loadAllMedecines(sFilterNameP, eSortItemP).collect { result ->
                _flowMedecines.emit(result)
            }
        }

    }

    fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>> = flow  {

        emit(ResultCustom.Loading)

        // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
        stockApi.loadMedicineByID(idMedicine).collect { result ->
            emit(result)
        }


    }.flowOn(Dispatchers.IO)

    suspend fun loadAllAisles(){

        withContext(Dispatchers.IO) {
            stockApi.loadAllAisles().collect { result ->
                _flowAisles.emit(result)
            }
        }

    }

    // Ajoute un médicament
    fun addMedicine(medicine : Medicine): Flow<ResultCustom<Medicine>> = flow {

        emit(ResultCustom.Loading)

        // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
        stockApi.addMedicine(medicine).collect { result ->
            emit(result)
        }

    }.flowOn(Dispatchers.IO)  // Exécuter sur un thread d'entrée/sortie (IO)


    fun loadAisleByID(idAisle: String): Flow<ResultCustom<Aisle>> = flow  {

        emit(ResultCustom.Loading)

        // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
        stockApi.loadAisleByID(idAisle).collect { result ->
            emit(result)
        }


    }.flowOn(Dispatchers.IO)


    fun updateMedicine(updatedMedicine: Medicine) : Flow<ResultCustom<String>> = flow {

        emit(ResultCustom.Loading)

        stockApi.updateMedicine(updatedMedicine).collect { result ->
            emit(result)
        }

    }


}