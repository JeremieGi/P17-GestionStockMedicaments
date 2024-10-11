package com.openclassrooms.rebonnte.repositoryStock

import com.openclassrooms.rebonnte.model.Medicine
import kotlinx.coroutines.flow.Flow

interface StockAPI {


    fun loadAllMedecines() : Flow<ResultCustom<List<Medicine>>>


}