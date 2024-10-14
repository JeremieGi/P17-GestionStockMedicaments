package com.openclassrooms.rebonnte.ui.medicine.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint


// TODO Denis : Je laisse les activitys ou je fais une appli mono-activity ?

@AndroidEntryPoint
class MedicineDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(Screen.CTE_PARAM_ID_MEDECINE) ?: "Unknown"

        setContent {
            RebonnteTheme {
                MedicineDetailScreen(idMedicineP = id)
            }
        }
    }

}
