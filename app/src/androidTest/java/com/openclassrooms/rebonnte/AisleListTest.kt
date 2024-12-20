package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
 * Test de la fenêtre listant les allées
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AisleListTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val _fakeListAisles = StockFakeAPI.initFakeAisles()

    @Before
    fun init() {
        hiltRule.inject()
    }


    /**
     * Affichage basiques des allées
     */
    @Test
    fun ailesDisplay() = runTest {

        UtilTestCommon.openAisleList(composeTestRule)

        // Affichage des 3 allées
        _fakeListAisles.forEach {
            composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
        }


    }


}