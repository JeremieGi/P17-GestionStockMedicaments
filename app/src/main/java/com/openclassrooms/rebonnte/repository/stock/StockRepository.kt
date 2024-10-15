package com.openclassrooms.rebonnte.repository.stock

import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.model.Aisle
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

    suspend fun loadAllMedicines(sFilterNameP : String, eSortItemP : EnumSortedItem){

        withContext(Dispatchers.IO) {

            // Si pas d'Internet
            if (!injectedContext.isInternetAvailable()) {

                _flowMedicines.emit(
                    ResultCustom.Failure(
                        injectedContext.getInjectedContext().getString(R.string.no_network)
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

    fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>> = flow  {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            _flowMedicines.emit(
                ResultCustom.Failure(
                    injectedContext.getInjectedContext().getString(R.string.no_network)
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


    suspend fun loadAllAisles(){

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            _flowMedicines.emit(
                ResultCustom.Failure(
                    injectedContext.getInjectedContext().getString(R.string.no_network)
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

    // Ajoute un médicament
    fun addMedicine(medicine : Medicine): Flow<ResultCustom<Medicine>> = flow {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            _flowMedicines.emit(
                ResultCustom.Failure(
                    injectedContext.getInjectedContext().getString(R.string.no_network)
                )
            )

        }
        else{
            emit(ResultCustom.Loading)

            // Emettre son propre Flow (avec les éventuelles erreurs ou succès)
            stockApi.addMedicine(medicine).collect { result ->
                emit(result)
            }
        }

    }.flowOn(Dispatchers.IO)  // Exécuter sur un thread d'entrée/sortie (IO)


    fun loadAisleByID(idAisle: String): Flow<ResultCustom<Aisle>> = flow  {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            _flowMedicines.emit(
                ResultCustom.Failure(
                    injectedContext.getInjectedContext().getString(R.string.no_network)
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


    fun updateMedicine(
        updatedMedicine: Medicine,
        author : User
    ) : Flow<ResultCustom<String>> = flow {

        // Si pas d'Internet
        if (!injectedContext.isInternetAvailable()) {

            _flowMedicines.emit(
                ResultCustom.Failure(
                    injectedContext.getInjectedContext().getString(R.string.no_network)
                )
            )

        }
        else{
            emit(ResultCustom.Loading)

            stockApi.updateMedicine(updatedMedicine,author).collect { result ->
                emit(result)
            }
        }

    }


}