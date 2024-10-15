package com.openclassrooms.rebonnte.ui.medicine.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint


// T094 - Je laisse les activity

@AndroidEntryPoint
class MedicineDetailActivity : ComponentActivity() {

    companion object {

        const val RESULT_MEDICINE_UPDATE = 1        // Indique à l'appelant (fenêtre de liste), que le medicatment a été mis à jour. Celà déclenchera le rafraichissement

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



}
