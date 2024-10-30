package com.openclassrooms.rebonnte

import android.content.Context
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.repository.user.UserFakeAPI
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Seule classe Model testée car les autres n'ont pas de méthodes

class MedicineTest {

    // Context mocké => Utilisation des ressources de chaine
    private lateinit var context: Context

    // objet de test
    private val _originalMedicine = Medicine(
        id = "123",
        name = "Paracetamol",
        stock = 50,
        oAisle = Aisle("A", "Aisle A"),
        histories = mutableListOf()
    )

    @Before
    fun setUp() {
        context = mockk()
    }

    @Test
    fun addHistoryTest() {

        // Initialiser un médicament avec un historique existant
        val listMedicines  = StockFakeAPI.initFakeMedicines()
        val cutMedicine = listMedicines[0]
        val nHistorySize = cutMedicine.histories.size

        val listUsers  = UserFakeAPI.initFakeUsers()
        val userTest = listUsers[0]

        // Ajouter un nouvel historique
        val newHistory = History(author = userTest, details = "new history")
        cutMedicine.addHistory(newHistory)

        // Vérifier que le nouvel historique est bien ajouté au début de la liste
        assertEquals("new history created",nHistorySize+1, cutMedicine.histories.size)
        assertEquals("check value of new history",newHistory, cutMedicine.histories[0])
    }

    @Test
    fun `sDiff with different name`() {

        // Configurer les messages de chaîne pour les tests
        // answers permet de définir une fonction qui sera appelée lorsque la méthode est invoquée. les arguments passés à la méthode dans it.invocation.args.
        every { context.getString(R.string.new_name, any()) } answers {

            val arrayParam2 = it.invocation.args[1] as Array<*> // Le 2ème paramètre de context.getString est un tableau d'argument
            val medicineName = arrayParam2[0] as String

            "New name: $medicineName" // Valeur qui sera renvoyée par context.getString(R.string.new_name, any()) lors du test
        }

//        every { context.getString(R.string., any()) } answers { "New aisle: ${it.invocation.args[1]}" }

        // Changement du nom
        val updatedMedicine = _originalMedicine.copy(name = "Ibuprofen")

        // Appel à la fonction testée
        val result = _originalMedicine.sDiff(updatedMedicine, context)

        // Vérification d ela différence
        assertEquals("New name: Ibuprofen", result)
    }


    @Test
    fun `sDiff with different stock`() {

        // Configurer les messages de chaîne pour les tests
        // answers permet de définir une fonction qui sera appelée lorsque la méthode est invoquée. les arguments passés à la méthode dans it.invocation.args.
        every { context.getString(R.string.new_stock, any()) } answers {

            val arrayParam2 = it.invocation.args[1] as Array<*> // Le 2ème paramètre de context.getString est un tableau d'argument
            val medicineStock = arrayParam2[0] as String

            "New stock: $medicineStock" // Valeur qui sera renvoyée par context.getString(R.string.new_stock, any()) lors du test
        }

        val updatedMedicine = _originalMedicine.copy(stock = 100)

        val result = _originalMedicine.sDiff(updatedMedicine, context)

        assertEquals("New stock: 100", result)
    }

    @Test
    fun `sDiff with different aisle`() {

        every { context.getString(R.string.new_aisle, any()) } answers {

            val arrayParam2 = it.invocation.args[1] as Array<*> // Le 2ème paramètre de context.getString est un tableau d'argument
            val medicineAisleName = arrayParam2[0] as String

            "New aisle: $medicineAisleName" // Valeur qui sera renvoyée par context.getString(R.string.new_aisle, any()) lors du test
        }
        val updatedMedicine = _originalMedicine.copy(oAisle = Aisle("B", "Aisle B"))

        val result = _originalMedicine.sDiff(updatedMedicine, context)

        assertEquals("New aisle: Aisle B", result)
    }


    @Test
    fun `sDiff with no difference`() {

        val updatedMedicine = _originalMedicine.copy()

        val result = _originalMedicine.sDiff(updatedMedicine, context)

        assertEquals("", result)

    }

}