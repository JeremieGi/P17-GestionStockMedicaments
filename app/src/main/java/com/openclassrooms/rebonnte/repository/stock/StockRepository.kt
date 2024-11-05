package com.openclassrooms.rebonnte.repository.stock

import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.model.User
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

/**
 * Repository permettant la gestion des allées et des médicaments
 */
@Singleton
class StockRepository @Inject constructor(
    private val stockApi: StockAPI,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
){

    /**
     * Liste des médicaments
     */
    private var _flowMedicines = MutableSharedFlow<ResultCustom<List<Medicine>>>()
    val flowMedicines : SharedFlow<ResultCustom<List<Medicine>>>
        get() = _flowMedicines

    /**
     * Liste des allées
     */
    private var _flowAisles = MutableSharedFlow<ResultCustom<List<Aisle>>>()
    val flowAisles : SharedFlow<ResultCustom<List<Aisle>>>
        get() = _flowAisles


    enum class EnumSortedItem {
        NONE, NAME, STOCK
    }

    /**
     * Chargement de tous les médicaments
     */
    suspend fun loadAllMedicines(sFilterNameP : String, eSortItemP : EnumSortedItem){

        withContext(Dispatchers.IO) {

            // Si pas d'Internet
            if (!injectedContext.isInternetAvailable()) {

                _flowMedicines.emit(
                    ResultCustom.Failure(
                        injectedContext.getContext().getString(R.string.no_network)
                    )
                )

            }
            else{

                stockApi.loadAllMedicines(sFilterNameP, eSortItemP).collect { result ->
                    _flowMedicines.emit(result)
                }
            }


        }

    }

    /**
     * Chargement d'un médicament
     */
    fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>> = flow  {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            emit(
                ResultCustom.Failure(
                    injectedContext.getContext().getString(R.string.no_network)
                )
            )

        }
        else{

            emit(ResultCustom.Loading)

            // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
            stockApi.loadMedicineByID(idMedicine).collect { result ->
                emit(result)
            }

        }

    }.flowOn(Dispatchers.IO)

    /**
     * Chargement de toutes les allées
     */
    suspend fun loadAllAisles(){

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            _flowAisles.emit(
                ResultCustom.Failure(
                    injectedContext.getContext().getString(R.string.no_network)
                )
            )

        }
        else{
            withContext(Dispatchers.IO) {
                stockApi.loadAllAisles().collect { result ->
                    _flowAisles.emit(result)
                }
            }
        }

    }

    /**
     * Ajoute un médicament (et un historique avec le créateur)
     */
    fun addMedicine(
        medicine : Medicine,
        author : User
    ): Flow<ResultCustom<Medicine>> = flow {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            emit(
                ResultCustom.Failure(
                    injectedContext.getContext().getString(R.string.no_network)
                )
            )

        }
        else{
            emit(ResultCustom.Loading)

            // Ajout de l'historique dans le repository
            val newHistory = History(
                author = author,
                details =  injectedContext.getContext().getString(R.string.creation)
            )
            medicine.addHistory(newHistory)

            // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
            stockApi.addMedicine(medicine).collect { result ->
                emit(result)
            }
        }

    }.flowOn(Dispatchers.IO)  // Exécuter sur un thread d'entrée/sortie (IO)

    /**
     * Mise à jour d'un médicament avec ajout de la modification dans l'historique
     */
    fun updateMedicine(
        oldMedicine : Medicine,
        updatedMedicine: Medicine,
        author : User
    ) : Flow<ResultCustom<Medicine>> = flow {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            emit(
                ResultCustom.Failure(
                    injectedContext.getContext().getString(R.string.no_network)
                )
            )

        }
        else{
            emit(ResultCustom.Loading)

            val sDetail = oldMedicine.sDiff(updatedMedicine,injectedContext.getContext())

            val newHistory = History(
                author = author,
                details = sDetail
            )
            updatedMedicine.addHistory(newHistory)

            stockApi.updateMedicine(updatedMedicine).collect { result ->
                emit(result)
            }
        }

    }

    /**
     * Chargement d'une allée
     */
    fun loadAisleByID(idAisle: String): Flow<ResultCustom<Aisle>> = flow  {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            emit(
                ResultCustom.Failure(
                    injectedContext.getContext().getString(R.string.no_network)
                )
            )

        }
        else{
            emit(ResultCustom.Loading)

            // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
            stockApi.loadAisleByID(idAisle).collect { result ->
                emit(result)
            }
        }

    }.flowOn(Dispatchers.IO)

    /**
     * Suppression d'un médicament
     */
    fun deleteMedecineById(sIDMedicineP : String) : Flow<ResultCustom<String>> = flow {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            emit(
                ResultCustom.Failure(
                    injectedContext.getContext().getString(R.string.no_network)
                )
            )

        }
        else{
            emit(ResultCustom.Loading)

            // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
            stockApi.deleteMedicineByID(sIDMedicineP).collect { result ->
                emit(result)
            }
        }

    }

    /**
     * Ajout d'une allée
     */
    fun addAisle(aisle: Aisle): Flow<ResultCustom<String>> = flow {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            emit(
                ResultCustom.Failure(
                    injectedContext.getContext().getString(R.string.no_network)
                )
            )

        }
        else{
            emit(ResultCustom.Loading)

            // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
            stockApi.addAisle(aisle).collect { result ->
                emit(result)
            }
        }

    }.flowOn(Dispatchers.IO)  // Exécuter sur un thread d'entrée/sortie (IO)


}