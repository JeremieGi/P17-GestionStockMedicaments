package com.openclassrooms.rebonnte.repositoryStock

import com.openclassrooms.rebonnte.model.Medicine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StockFakeAPI : StockAPI {



    override fun loadAllMedecines(): Flow<ResultCustom<List<Medicine>>> {
        TODO("Not yet implemented")
    }


}