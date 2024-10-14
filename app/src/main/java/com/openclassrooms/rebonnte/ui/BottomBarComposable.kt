
package com.openclassrooms.rebonnte.ui


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


/**
 * Composable pour afficher la BottomBar
 */
@Composable
fun BottomBarComposable(
    sActiveScreenP : String,
    onClickMedicinesP  : () -> Unit,
    onClickAisleP : () -> Unit
) {


    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Aisle") },
            selected = sActiveScreenP==Screen.CTE_AISLE_LIST_SCREEN,
            onClick = {
                onClickAisleP()
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Medicine") },
            selected = sActiveScreenP==Screen.CTE_MEDICINE_LIST_SCREEN,
            onClick = {
                onClickMedicinesP()
            }
        )
    }

}