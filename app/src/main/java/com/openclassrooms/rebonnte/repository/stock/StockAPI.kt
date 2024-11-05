package com.openclassrooms.rebonnte.repository.stock

import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repository.stock.StockRepository.EnumSortedItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface utilisée par StockRepository pour l'accès aux données
 */
interface StockAPI {

    /**
     * Charge tous les médicaments
     */
    fun loadAllMedicines(sFilterNameP : String, eSortItemP : EnumSortedItem) : Flow<ResultCustom<List<Medicine>>>

    /**
     * Ajout d'un médicament
     */
    fun addMedicine(
        medicine: Medicine
    ): Flow<ResultCustom<Medicine>>

    /**
     * Mise à jour d'un médicament
     */
    fun updateMedicine(
        updatedMedicine: Medicine
    ) : Flow<ResultCustom<Medicine>>

    /**
     * Chargement d'un médicament par son ID
     */
    fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>>

    /**
     * Supprime un médicament par son ID
     */
    fun deleteMedicineByID(idMedicine: String): Flow<ResultCustom<String>>

    /**
     * Chargement de toutes les allées
     */
    fun loadAllAisles() : Flow<ResultCustom<List<Aisle>>>

    /**
     * Chargement d'une allée par son ID
     */
    fun loadAisleByID(idAisle: String): Flow<ResultCustom<Aisle>>

    /**
     * Ajout d'une allée
     */
    fun addAisle(aisle: Aisle): Flow<ResultCustom<String>>


}