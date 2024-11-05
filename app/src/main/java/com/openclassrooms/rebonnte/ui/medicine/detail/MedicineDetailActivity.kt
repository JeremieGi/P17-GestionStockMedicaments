package com.openclassrooms.rebonnte.ui.medicine.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint


// T094 - Je laisse les activity

@AndroidEntryPoint
class MedicineDetailActivity : ComponentActivity() {

    companion object {
        const val RESULT_MEDICINE_UPDATE = 1        // Indique à l'appelant (fenêtre de liste), que le medicament a été mis à jour. Celà déclenchera le rafraichissement
        const val PARAM_MEDICINE_ADD = "AddMode"    // Paramètre en mode ajout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(Screen.CTE_PARAM_ID_MEDICINE) ?: ""

        setContent {
            RebonnteTheme {
                MedicineDetailScreen(
                    idMedicineP = id,
                    onMedicineUpdated = {
                        // Indique à l'appelant (fenêtre de liste), que le medicatment a été mis à jour
                        setResult(RESULT_MEDICINE_UPDATE)
                        finish()
                    }
                )
            }
        }
    }

    // Lors du clic sur le bouton back de l'action bar (permet un retour géré correctement voir AisleDetailActivity pour plus d'explication)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> { // Id de la flèche de retour
                //onBackPressed() // Gérer le clic sur la flèche de retour
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
