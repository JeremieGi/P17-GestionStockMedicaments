package com.openclassrooms.rebonnte.repository.stock

import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.model.User
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockRepository.EnumSortedItem
import kotlinx.coroutines.flow.Flow

interface StockAPI {


    fun loadAllMedicines(sFilterNameP : String, eSortItemP : EnumSortedItem) : Flow<ResultCustom<List<Medicine>>>

    // Ajout d'un médicament
    fun addMedicine(
        medicine: Medicine,
        author : User
    ): Flow<ResultCustom<String>>

    fun updateMedicine(
        updatedMedicine: Medicine,
        author : User
    ) : Flow<ResultCustom<String>>

    // Chargement d'un médicament
    fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>>

    // Supprime un médicament
    fun deleteMedicineByID(idMedicine: String): Flow<ResultCustom<String>>

    fun loadAllAisles() : Flow<ResultCustom<List<Aisle>>>

    // Chargement d'une allée
    fun loadAisleByID(idAisle: String): Flow<ResultCustom<Aisle>>



}