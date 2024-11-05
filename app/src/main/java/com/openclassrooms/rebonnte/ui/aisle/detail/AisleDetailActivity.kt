package com.openclassrooms.rebonnte.ui.aisle.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AisleDetailActivity : ComponentActivity() {

    companion object {
        const val RESULT_AISLE_ADD = 1              // Indique à l'appelant (fenêtre de liste), que l'allée a été ajoutée. Celà déclenchera le rafraichissement

        const val PARAM_AISLE_ADD = "AddMode"       // Paramètre en mode ajout
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(Screen.CTE_PARAM_ID_AISLE) ?: "Unknown"

        // Déclarez le callback pour le bouton de retour
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish() // Ferme l'activité
            }
        })

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

    // Pas besoin ca marche déjà lors du clic sur le bouton Back système
//    override fun onBackPressed() {
//        setResult(RESULT_BACK, intent)  // Indique que l'utilisateur a cliqué sur Back
//        super.onBackPressed()           // Appelle la méthode parente pour terminer l'activité
//    }

//    // Lors du clic sur le bouton back de l'action bar
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        return when (item.itemId) {
//            android.R.id.home -> { // Id de la flèche de retour
//                onBackPressed() // Gérer le clic sur la flèche de retour
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

}

