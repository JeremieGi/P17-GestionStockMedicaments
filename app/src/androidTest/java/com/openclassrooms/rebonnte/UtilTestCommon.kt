package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import kotlinx.coroutines.test.runTest

object UtilTestCommon {

    /**
     * Ouvre l'écran de liste des allées
     */
    fun openAisleList(composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) = runTest {

        composeTestRule.awaitIdle()

        // Détection du menu 'Aisle"
        val sAisleBottomIcon = composeTestRule.activity.getString(R.string.aisle) // Unresolved reference: activity
        composeTestRule.onNodeWithText(sAisleBottomIcon)
            .assertIsDisplayed()

        // Clique sur le bouton de la bottom bar
        composeTestRule.onNodeWithTag(TestTags.BOTTOM_BAR_ICON_AISLE)
            .performClick()

        // Attente des redessins
        composeTestRule.awaitIdle()

        // Affiche la liste des allées
        val sTitleAisleList = composeTestRule.activity.getString(R.string.aisles)
        composeTestRule.onNodeWithText(sTitleAisleList).assertIsDisplayed()

    }


}