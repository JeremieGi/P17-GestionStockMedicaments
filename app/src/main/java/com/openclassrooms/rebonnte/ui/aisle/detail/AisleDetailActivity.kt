package com.openclassrooms.rebonnte.ui.aisle.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AisleDetailActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(Screen.CTE_PARAM_ID_AISLE) ?: "Unknown"

        setContent {
            RebonnteTheme {
                AisleDetailScreen(
                    idAisleP = id,
                    onBackClick = {} // TODO JG : A modifier quand j'aurai décider comment implémenter la navigation
                )
            }
        }
    }

}

