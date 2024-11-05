package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.repository.stock.FirebaseAisleDTO
import com.openclassrooms.rebonnte.repository.stock.FirebaseHistoryDTO
import com.openclassrooms.rebonnte.repository.stock.FirebaseMedicineDTO
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests des classes DTO
 */
class FirebaseDTOTest {

    // Ce test assure 100% de couvertures des 3 classes de DTO

    /**
     * Transformation DTO vers Model
     */
    @Test
    fun toModel(){

        val emailAuthor = "emailauthor@free.fr"

        val cutDTO = FirebaseMedicineDTO(
            id = "1",
            name = "testMedicine",
            stock = 10,
            aisle = FirebaseAisleDTO("idTestAisle","nameAisle1"),
            histories = mutableListOf( FirebaseHistoryDTO(emailAuthor, details = "details") )
        )

        val medicineModel = cutDTO.toModel()


        assertEquals(cutDTO.id,medicineModel.id)
        assertEquals(cutDTO.name,medicineModel.name,)
        assertEquals(cutDTO.stock,medicineModel.stock)
        assertEquals(cutDTO.aisle.id,medicineModel.oAisle.id)
        assertEquals(cutDTO.aisle.name,medicineModel.oAisle.name)
        assertEquals(cutDTO.histories.size,medicineModel.histories.size)
        assertEquals(cutDTO.histories[0].sEmailAuthor,medicineModel.histories[0].author.sEmail)
        assertEquals(cutDTO.histories[0].details,medicineModel.histories[0].details)


    }

    /**
     * Transformation Model vers DTO
     */
    @Test
    fun testConstructor(){

        val medicines = StockFakeAPI.initFakeMedicines()
        val medicineModel = medicines[0]

        val cutDTO = FirebaseMedicineDTO(medicineModel)

        assertEquals(medicineModel.id,cutDTO.id)
        assertEquals(medicineModel.name,cutDTO.name)
        assertEquals(medicineModel.stock,cutDTO.stock)
        assertEquals(medicineModel.oAisle.id,cutDTO.aisle.id)
        assertEquals(medicineModel.oAisle.name,cutDTO.aisle.name)
        assertEquals(medicineModel.histories.size,cutDTO.histories.size)
        assertEquals(medicineModel.histories[0].author.sEmail,cutDTO.histories[0].sEmailAuthor)
        assertEquals(medicineModel.histories[0].details,cutDTO.histories[0].details)

    }


}