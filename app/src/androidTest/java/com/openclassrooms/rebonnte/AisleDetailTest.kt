package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AisleDetailTest {


    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val _fakeListAisles = StockFakeAPI.initFakeAisles()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun ailes_click_detail() = runTest {

        UtilTestCommon.openAisleList(composeTestRule)

        val aisles1 = _fakeListAisles[2]

        composeTestRule.onNodeWithText(aisles1.name)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Attend tant que l'évènement n'est pas chargé complétement (sinon problème dans GitHub Action)
        composeTestRule.waitUntil(timeoutMillis = 10000) { // 10000ms => 100s => 1m30
            composeTestRule.onNodeWithText(aisles1.name).isDisplayed()
        }

        // Pour être sûr qu'on est bien sur la fenêtre de détails
        val sLabelName = composeTestRule.activity.getString(R.string.name)
        composeTestRule.onNodeWithText(sLabelName)
            .assertIsDisplayed()

    }

    @Test
    fun ailes_addSuccess() = runTest {

        UtilTestCommon.openAisleList(composeTestRule)

        val sAddButtonName = composeTestRule.activity.getString(R.string.add_an_aisle)
        composeTestRule.onNodeWithContentDescription(sAddButtonName)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Pour être sûr qu'on est bien sur la fenêtre de détails
        val sLabelName = composeTestRule.activity.getString(R.string.name)
        composeTestRule.onNodeWithText(sLabelName)
            .assertIsDisplayed()

        // Saisie d'un nom
        val sNameValue = "NewAisleTest"
        composeTestRule.onNodeWithTag(TestTags.AISLE_DETAIL_TEXT_FIELD_NAME)
            .assertIsDisplayed()
            .performTextInput(sNameValue)

        // Le bouton validate
        val sValidateButtonLabel = composeTestRule.activity.getString(R.string.validate)
        composeTestRule.onNodeWithText(sValidateButtonLabel)
            .assertIsDisplayed()    // doit-être afficher
            .assertIsEnabled()      // activé
            .performClick()         // Simulation du clic

        composeTestRule.awaitIdle()

        // Retour à la liste des allées
        val sTitleAisleList = composeTestRule.activity.getString(R.string.aisles)
        composeTestRule.onNodeWithText(sTitleAisleList).assertIsDisplayed()

        // Vérification de l'ajout de l'allée
        composeTestRule.onNodeWithText(sNameValue).assertIsDisplayed()

    }

    @Test
    fun ailes_addErrorNoName() = runTest {

        UtilTestCommon.openAisleList(composeTestRule)

        val sAddButtonName = composeTestRule.activity.getString(R.string.add_an_aisle)
        composeTestRule.onNodeWithContentDescription(sAddButtonName)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.awaitIdle()

        // Pour être sûr qu'on est bien sur la fenêtre de détails
        val sLabelName = composeTestRule.activity.getString(R.string.name)
        composeTestRule.onNodeWithText(sLabelName)
            .assertIsDisplayed()

        // Saisie d'un nom vide
        composeTestRule.onNodeWithTag(TestTags.AISLE_DETAIL_TEXT_FIELD_NAME)
            .assertIsDisplayed()
            .performTextInput("")

        composeTestRule.awaitIdle()

        // Le bouton validate
        val sValidateButtonLabel = composeTestRule.activity.getString(R.string.validate)
        composeTestRule.onNodeWithText(sValidateButtonLabel)
            .assertIsDisplayed()    // doit-être afficher
            .performClick()         // Simulation du clic => ne fait rien

        composeTestRule.awaitIdle()

        // Affichage de l'erreur
        val sError = composeTestRule.activity.getString(R.string.mandatoryname)
        composeTestRule.onNodeWithText(sError)
            .assertIsDisplayed()


    }
}