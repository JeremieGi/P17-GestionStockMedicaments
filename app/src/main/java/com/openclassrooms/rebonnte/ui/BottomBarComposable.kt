
package com.openclassrooms.rebonnte.ui


import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


/**
 * Composable pour afficher la BottomBar
 */
@Composable
fun BottomBarComposable(
    sActiveScreenP : String,
    onClickMedicinesP  : () -> Unit,
    onClickAislesP : () -> Unit,
    onClickLogoutP : (Context) -> Task<Void>,
    onBackClickP : () -> Unit
) {

    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) } // État pour afficher ou non l'AlertDialog

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
        NavigationBarItem(
            //icon = { Icon(Icons.Default.List, contentDescription = null) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text(stringResource(R.string.logout)) },
            selected = false,
            onClick = {
                showDialog = true // Afficher la boîte de dialogue lorsque l'utilisateur clique sur logout
            }
        )
    }

    // Afficher l'AlertDialog si showDialog est vrai
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false // Cacher la boîte de dialogue si l'utilisateur clique en dehors
            },
            title = {
                Text(text = stringResource(R.string.logout))
            },
            text = {
                Text(text = stringResource(R.string.logout_confirmation_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false // Cacher la boîte de dialogue
                        onClickLogoutP(context) // Exécuter l'action de déconnexion
                            .addOnCompleteListener {
                                // méthode qui permet de spécifier une action à exécuter une fois que l'opération signOut() est terminée.

                                Toast
                                    .makeText(context, context.getString(R.string.deconnexion_ok), Toast.LENGTH_SHORT)
                                    .show()

                                onBackClickP()

                            }
                            .addOnFailureListener { exception ->
                                // Erreur lors de la déconnexion

                                val errorMessage = exception.localizedMessage ?: context.getString(R.string.unknown_error)

                                Toast
                                    .makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                    .show()

                            }
                    }
                ) {
                    Text(text = stringResource(R.string.logout))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false // Cacher la boîte de dialogue sans déconnexion
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

}

// Le thème n'est pas appliqué....
// A l'exécution, si la fenêtre de login est annulée => style pas appliqué non plus
@Preview(showBackground = true)
@Composable
fun BottomBarComposablePreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

    RebonnteTheme {
        BottomBarComposable(
            sActiveScreenP = Screen.CTE_MEDICINE_LIST_SCREEN,
            onClickMedicinesP = { },
            onClickAislesP = { },
            onClickLogoutP = mockContext,
            onBackClickP = { }
        )
    }

}