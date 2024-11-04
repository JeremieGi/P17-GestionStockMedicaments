package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
}