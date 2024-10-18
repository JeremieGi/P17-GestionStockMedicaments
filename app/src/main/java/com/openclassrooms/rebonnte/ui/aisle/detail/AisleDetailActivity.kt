package com.openclassrooms.rebonnte.ui.aisle.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AisleDetailActivity : ComponentActivity() {

    companion object {

        const val RESULT_AISLE_ADD= 1        // Indique à l'appelant (fenêtre de liste), que l'allée a été ajoutée. Celà déclenchera le rafraichissement

        const val PARAM_AISLE_ADD = "AddMode"    // Paramètre en mode ajout

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(Screen.CTE_PARAM_ID_AISLE) ?: "Unknown"

        setContent {
            RebonnteTheme {
                // Une actionBar avec le nom de l'appli ici car l'activity à un thème avec ActionBar
                // Cette action bar permettra le retour en arrière
                AisleDetailScreen(
                    idAisleP = id,
                    onAisleInsertedP = {
                        // Indique à l'appelant (fenêtre de liste), que l'allée a été ajoutée
                        setResult(RESULT_AISLE_ADD)
                        finish()
                    }
                )
            }
        }
    }

}

