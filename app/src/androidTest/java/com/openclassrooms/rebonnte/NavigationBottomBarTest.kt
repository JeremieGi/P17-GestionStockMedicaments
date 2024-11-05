
package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test de navigation via la bottom bar
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationBottomBarTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun navigation_bottom_bar() = runTest {

        composeTestRule.awaitIdle()

        // Détection du menu 'Aisle"
        val sAisleBottomIcon = composeTestRule.activity.getString(R.string.aisle)
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

        // Clique sur le bouton 'Medicine'
        composeTestRule
            .onNodeWithTag(TestTags.BOTTOM_BAR_ICON_MEDICINE).performClick()

        composeTestRule.awaitIdle()

        // La fenêtre des médicaments
        val sTitleUserProfile = composeTestRule.activity.getString(R.string.medicines)
        composeTestRule.onNodeWithText(sTitleUserProfile).assertExists()



    }


}