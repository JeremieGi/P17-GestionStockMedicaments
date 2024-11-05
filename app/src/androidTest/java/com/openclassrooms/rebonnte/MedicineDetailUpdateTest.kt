package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds

/**
 * Test de l'affichage et modification d'un médicament
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MedicineDetailUpdateTest {

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
     * Affichage de la fenêtre de détail d'un médicament
     */
    @Test
    fun medicines_click_detail() = runTest {

        val medicine = _fakeListMedicines[1]

        composeTestRule.onNodeWithText(medicine.name)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Attend tant que l'évènement n'est pas chargé complétement (sinon problème dans GitHub Action)
        composeTestRule.waitUntil(timeoutMillis = 10000) { // 10000ms => 100s => 1m30
            composeTestRule.onNodeWithText(medicine.name).isDisplayed()
        }

        // Pour être sûr qu'on est bien sur la fenêtre de détails
        val sLabelName = composeTestRule.activity.getString(R.string.name)
        composeTestRule.onNodeWithText(sLabelName)
            .assertIsDisplayed()

        // Présence des données
        composeTestRule.onNodeWithText(medicine.oAisle.name)
        composeTestRule.onNodeWithText(medicine.stock.toString())

        // Présence de l'historique
        medicine.histories.forEach {
            composeTestRule.onNodeWithText(it.details)
            composeTestRule.onNodeWithText(it.author.sEmail)
        }


    }

    /**
     * Mise à jour du stock
     */
    @Test
    fun updateStock() = runTest(timeout = 60.seconds) {// Augmentation du time-out par défaut qui est 10s

        val medicine = _fakeListMedicines[1]

        composeTestRule.onNodeWithText(medicine.name)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Attend tant que le medicament n'est pas chargé complétement (sinon problème dans GitHub Action)
        composeTestRule.waitUntil(timeoutMillis = 10000) { // 10000ms => 100s => 1m30
            composeTestRule.onNodeWithText(medicine.name).isDisplayed()
        }

        // Pour être sûr qu'on est bien sur la fenêtre de détails
        val sLabelName = composeTestRule.activity.getString(R.string.name)
        composeTestRule.onNodeWithText(sLabelName)
            .assertIsDisplayed()

        // Remet le stock à 0
        val sButtonDecrementStock = composeTestRule.activity.getString(R.string.minus_one)
        // Clique le nombre fois necessaire pour remettre le stock à 0
        for (i in 0 .. medicine.stock) {
            composeTestRule.onNodeWithContentDescription(sButtonDecrementStock)
                .performClick()
        }

        composeTestRule.onNodeWithTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_STOCK)
            .assertTextContains("0") // au lieu de assertTextEquals => Cette méthode ne vérifie que la présence de 0 dans le TextField et ne nécessite pas une correspondance exacte entre tous les attributs de texte de l’élément.

        // 3 click mais le stock reste à 0 (pas de stock négatif)
        for (i in 0 .. 3) {
            composeTestRule.onNodeWithContentDescription(sButtonDecrementStock)
                .performClick()
        }
        composeTestRule.onNodeWithTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_STOCK)
            .assertTextContains("0") // au lieu de assertTextEquals => Cette méthode ne vérifie que la présence de 0 dans le TextField et ne nécessite pas une correspondance exacte entre tous les attributs de texte de l’élément.


        // Clique 100 fois sur l'incrémentation de stock
        val sButtonIncrementStock = composeTestRule.activity.getString(R.string.plus_one)
        val nStockTest = 11
        for (i in 1 .. nStockTest) {
            composeTestRule.onNodeWithContentDescription(sButtonIncrementStock)
                .performClick()
        }

        composeTestRule.onNodeWithTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_STOCK)
            .assertTextContains(nStockTest.toString())


        // Le bouton validate
        val sValidateButtonLabel = composeTestRule.activity.getString(R.string.validate)
        composeTestRule.onNodeWithText(sValidateButtonLabel)
            .assertIsDisplayed()    // doit-être afficher
            .assertIsEnabled()      // activé
            .performClick()         // Simulation du clic

        composeTestRule.awaitIdle()

        // Retour à la liste des médicaments
        val sTitleMedicineList = composeTestRule.activity.getString(R.string.medicines)
        composeTestRule.onNodeWithText(sTitleMedicineList).assertIsDisplayed()

        // Vérification de la mise à jour du stock
        val sStock = "${composeTestRule.activity.getString(R.string.stock)}: $nStockTest"
        composeTestRule.onNodeWithText(sStock)
            .assertIsDisplayed()

    }
}