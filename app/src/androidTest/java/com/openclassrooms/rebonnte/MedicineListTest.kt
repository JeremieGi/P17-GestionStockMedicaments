package com.openclassrooms.rebonnte


import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test de l'affichage de la liste de médicaments
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MedicineListTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val _fakeListMedicines = StockFakeAPI.initFakeMedicines()

    @Before
    fun init() {
        hiltRule.inject()
    }

    /**
     * Affichage basique
     */
    @Test
    fun medicinesDisplay() = runTest {

        // Affichage des médicaments
        _fakeListMedicines.forEach {
            composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
        }


    }


    /**
     * Fonction qui permet de vérifier le contenu exact d'un LazyColumn
     */
    private fun assertLazyColumn(expectedMedicineListP: List<Medicine>) {

        // Récupérer tous les nœuds avec le testTag
        val nodes = composeTestRule.onAllNodesWithTag(TestTags.MEDICINE_ITEM).fetchSemanticsNodes()

        var nSize = 0

        // Parcours de chaque noeud
        nodes.forEachIndexed { index, node ->

            // Récupération du texte semantique
            val annotatedString = node.config.getOrNull(SemanticsProperties.Text)
            val sSemanticText = annotatedString?.toString() ?: ""

            val expectedName = expectedMedicineListP[index].name

            // Vérifie si le texte sémantique contient le titre attendu
            assert(sSemanticText.contains(expectedName)) {
                "This medicine would be $expectedName and it's $sSemanticText"
            }

            nSize++

        }


        assert(nSize==expectedMedicineListP.size) {
            "Expected size = ${expectedMedicineListP.size} and real size = $nSize"
        }

    }

    /**
     * Permet de cliquer sur l'icone du menu puis sur l'item (DropdownMenuItem) souhaité
     */
    private fun clickOnSortMenu(sDropdownMenuItem : String) = runTest {

        val sDropdownMenuIcon = composeTestRule.activity.getString(R.string.sort_medicines)
        composeTestRule.onNodeWithContentDescription(sDropdownMenuIcon)
            .performClick()

        composeTestRule.awaitIdle()

        // Clique sur le DropdownMenuItem
        composeTestRule.onNodeWithText(sDropdownMenuItem)
            .performClick()

        composeTestRule.awaitIdle()


    }


    /**
     * Test du tri par nom
     */
    @Test
    fun sortByName() = runTest {

        composeTestRule.awaitIdle()

        val sSortByName = composeTestRule.activity.getString(R.string.sort_by_name)
        clickOnSortMenu(sSortByName)

        val expectedMedicineList = _fakeListMedicines.sortedBy { it.name }
        assertLazyColumn(expectedMedicineList)

        // Re-Clique sur le bouton d'annulation de tri
        val sSortByNone = composeTestRule.activity.getString(R.string.sort_by_none)
        clickOnSortMenu(sSortByNone)

        val expectedMedicineListWithoutSort = _fakeListMedicines
        assertLazyColumn(expectedMedicineListWithoutSort)

    }

    /**
     * Test du tri par stock
     */
    @Test
    fun sortByStock() = runTest {

        composeTestRule.awaitIdle()

        val sSortByStock = composeTestRule.activity.getString(R.string.sort_by_stock)
        clickOnSortMenu(sSortByStock)

        val expectedMedicineList = _fakeListMedicines.sortedBy { it.stock }
        assertLazyColumn(expectedMedicineList)

        // Re-Clique sur le bouton d'annulation de tri
        val sSortByNone = composeTestRule.activity.getString(R.string.sort_by_none)
        clickOnSortMenu(sSortByNone)

        val expectedMedicineListWithoutSort = _fakeListMedicines
        assertLazyColumn(expectedMedicineListWithoutSort)

    }

    /**
     * Suppression d'un médicament par swipe (de droite à gauche)
     */
    @Test
    fun deleteMedicineBySwipe() = runTest {

        composeTestRule.awaitIdle()

        val medicineDeleted = _fakeListMedicines[0]

        val tagItem = "${TestTags.MEDICINE_ID_PREFIX}${medicineDeleted.id}"

        // Récupérez le nœud avec le tag
        val node = composeTestRule.onNodeWithTag(tagItem).fetchSemanticsNode()

        // Récupérez la taille de l'élément
        val width = node.size.width.toFloat()

        // Swipe sur quasiment la totalité de la largeur
        composeTestRule.onNodeWithTag(tagItem)
            .performTouchInput {

                swipeLeft(
                    startX = width * 0.9f, // Presque à droite
                    endX = width * 0.1f,    // Presque à gauche
                    durationMillis = 300 // Durée du swipe, ajustable
                )
            }

        composeTestRule.awaitIdle()

        // La fenêtre de confirmation s'ouvre

        // Confirmation de la suppression
        val sDeleteButton = composeTestRule.activity.getString(R.string.delete)
        composeTestRule.onNodeWithText(sDeleteButton)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Vérification de la suppression
        val expectedMedicineList = _fakeListMedicines.filter{ it.id != medicineDeleted.id}
        assertLazyColumn(expectedMedicineList)


    }

}