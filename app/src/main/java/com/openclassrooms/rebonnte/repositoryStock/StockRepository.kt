package com.openclassrooms.rebonnte.repositoryStock

import com.openclassrooms.p15_eventorias.repository.InjectedContext
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val eventApi: StockAPI,
    private val injectedContext: InjectedContext // Contexte connu par injection de dépendance (Permet de vérifier l'accès à Internet et aussi d'accéder aux ressources chaines)
){

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


    suspend fun loadAllMedecines(){

        withContext(Dispatchers.IO) {
            eventApi.loadAllMedecines().collect { result ->
                _flowMedecines.emit(result)
            }
        }

    }

}