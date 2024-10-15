
package com.openclassrooms.rebonnte.ui


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


/**
 * Composable pour afficher la BottomBar
 */
@Composable
fun BottomBarComposable(
    sActiveScreenP : String,
    onClickMedicinesP  : () -> Unit,
    onClickAislesP : () -> Unit
) {


    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.aisle)) },
            selected = sActiveScreenP==Screen.CTE_AISLE_LIST_SCREEN,
            onClick = {
                onClickAislesP()
            }
        )
        NavigationBarItem(
            //icon = { Icon(Icons.Default.List, contentDescription = null) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text(stringResource(R.string.medicine)) },
            selected = sActiveScreenP==Screen.CTE_MEDICINE_LIST_SCREEN,
            onClick = {
                onClickMedicinesP()
            }
        )
    }

}

// Le thème n'est pas appliqué....
// A l'exécution, si la fenêtre de login est annulée => style pas appliqué non plus
@Preview(showBackground = true)
@Composable
fun BottomBarComposablePreview() {

    RebonnteTheme {
        BottomBarComposable(
            sActiveScreenP = Screen.CTE_MEDICINE_LIST_SCREEN,
            onClickMedicinesP = { },
            onClickAislesP = { },
        )
    }

}