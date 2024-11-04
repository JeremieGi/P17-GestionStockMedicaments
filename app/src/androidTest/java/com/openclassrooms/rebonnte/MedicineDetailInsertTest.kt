package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
 * Test de l'ajout d'un médicament
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MedicineDetailInsertTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val _fakeListAisle = StockFakeAPI.initFakeAisles()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun medicines_addSuccess() = runTest {

        val sAddButtonName = composeTestRule.activity.getString(R.string.add_medicine)
        composeTestRule.onNodeWithContentDescription(sAddButtonName)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Pour être sûr qu'on est bien sur la fenêtre de détails
        val sLabelName = composeTestRule.activity.getString(R.string.name)
        composeTestRule.onNodeWithText(sLabelName)
            .assertIsDisplayed()

        // Saisie d'un nom
        val sNameValue = "NewMedicineTest"
        composeTestRule.onNodeWithTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_NAME)
            .assertIsDisplayed()
            .performTextInput(sNameValue)

        // Saisie d'une allée existante
        val sAisleValue = _fakeListAisle[0].name
        composeTestRule.onNodeWithTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_AISLE)
            .assertIsDisplayed()
            .performTextInput(sAisleValue)

        // Stock de 1
        val sButtonIncrementStock = composeTestRule.activity.getString(R.string.plus_one)
        composeTestRule.onNodeWithContentDescription(sButtonIncrementStock)
            .performClick()

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

        // Vérification de l'ajout
        composeTestRule.onNodeWithText(sNameValue).assertIsDisplayed()

    }



}