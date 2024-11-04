package com.openclassrooms.rebonnte

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

    @Test
    fun medicinesDisplay() = runTest {

        // Affichage des médicaments
        _fakeListMedicines.forEach {
            composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
        }


    }


    /**
     * Fonction qui permet de vérifier le contenu exact d'un LasyColumn
     */
    private fun assertLazyColumn(expectedMedicineListP: List<Medicine>) {

        // Récupérer tous les nœuds avec le testTag
        val nodes = composeTestRule.onAllNodesWithTag(TestTags.MEDICINE_ID_PREFIX).fetchSemanticsNodes()

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
        }

    }


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

        // Re-Clique sur le bouton d'annualtion de tri
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

        // Re-Clique sur le bouton d'annualtion de tri
        val sSortByNone = composeTestRule.activity.getString(R.string.sort_by_none)
        clickOnSortMenu(sSortByNone)

        val expectedMedicineListWithoutSort = _fakeListMedicines
        assertLazyColumn(expectedMedicineListWithoutSort)

    }

    // TODO JG : Test de suppression avec swipe

}