package com.openclassrooms.rebonnte.ui.aisle.list

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.BottomBarComposable
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleDetailActivity
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun AisleListScreen(
    viewModel: AisleListViewModel = hiltViewModel(),
    onClickMedicineOnBottomBarP : () -> Unit,
    onBackClickP: () -> Unit
) {

    val uiStateList by viewModel.uiStateListAile.collectAsState()

    LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
        viewModel.loadAllAisle()
    }


    // Pour déclencher le rafraichissement en cas de modification des données dans l'Activity de détails
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AisleDetailActivity.RESULT_AISLE_ADD) {
            viewModel.loadAllAisle()
        }
    }

    AisleListStateComposable(
        uiStateListP = uiStateList,
        loadAllAilesP = viewModel::loadAllAisle,
        onClickMedicineOnBottomBarP = onClickMedicineOnBottomBarP,
        onClickLogoutOnBottomBarP = viewModel::logout,
        onBackClickP = onBackClickP,
        launcherP = launcher
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleListStateComposable(
    modifier: Modifier = Modifier,
    uiStateListP: AisleListUIState,
    loadAllAilesP : () -> Unit,
    onClickMedicineOnBottomBarP : () -> Unit,
    onClickLogoutOnBottomBarP : (Context) -> Task<Void>,
    onBackClickP: () -> Unit,
    launcherP : ActivityResultLauncher<Intent>?,
) {

    Scaffold(
        topBar = {
            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.aisles))  },
                )
            }
        },
        bottomBar = {
            BottomBarComposable(
                sActiveScreenP = Screen.CTE_AISLE_LIST_SCREEN,
                onClickMedicinesP = onClickMedicineOnBottomBarP,
                onClickAislesP = { /* Bouton non clickable */ },
                onClickLogoutP = onClickLogoutOnBottomBarP,
                onBackClickP = onBackClickP
            )
        },
        content = { innerPadding ->

            when (uiStateListP) {

                // Chargement
                is AisleListUIState.IsLoading -> {
                    LoadingComposable(modifier= modifier.padding(innerPadding))
                }

                // Récupération des données avec succès
                is AisleListUIState.Success -> {

                    AisleListComposable(
                        modifier = modifier.padding(innerPadding),
                        listAisles = uiStateListP.listAisles,
                        launcherP = launcherP
                    )

                }

                // Exception
                is AisleListUIState.Error -> {

                    val error = uiStateListP.sError ?: stringResource(
                        R.string.unknown_error
                    )

                    ErrorComposable(
                        modifier= modifier.padding(innerPadding),
                        sErrorMessage = error,
                        onClickRetryP = {
                            loadAllAilesP()
                        }
                    )


                }
            }



        },
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(
                onClick = {
                    startAisleDetailActivity(context, launcherP , AisleDetailActivity.PARAM_AISLE_ADD)
                },
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_an_aisle)
                )
            }
        }
    )


}

@Composable
fun AisleListComposable(
    modifier: Modifier,
    listAisles: List<Aisle>,
    launcherP : ActivityResultLauncher<Intent>?,
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(listAisles) { aisle ->
            AisleItem(aisle = aisle, onClick = {
                startAisleDetailActivity(context, launcherP, aisle.id)
            })
        }
    }

}

@Composable
fun AisleItem(
    aisle: Aisle,
    onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = aisle.name, style = MaterialTheme.typography.bodyMedium)
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Arrow")
    }
}



// Lance l'activity en utilisant un launcher pour savoir quand l'activity se ferme
private fun startAisleDetailActivity(
    context: Context,
    launcher: ActivityResultLauncher<Intent>?,
    id: String
) {

    // launcher peut-être nullable juste pour les previews Compose
    launcher?.let {
        val intent = Intent(context, AisleDetailActivity::class.java).apply {
            putExtra(Screen.CTE_PARAM_ID_AISLE, id)
        }
        //context.startActivity(intent)
        it.launch(intent)
    }



}


@Preview(
    name ="Aisle list success",
    showBackground = true
)
@Composable
fun AisleListComposableSuccessPreview() {

    val listFakeAisles = StockFakeAPI.initFakeAisles()
    val uiStateSuccess = AisleListUIState.Success(listFakeAisles)

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }


    RebonnteTheme {

        AisleListStateComposable(
            uiStateListP = uiStateSuccess,
            loadAllAilesP = {},
            onClickMedicineOnBottomBarP = {},
            onClickLogoutOnBottomBarP = mockContext,
            onBackClickP = {},
            launcherP = null

        )
    }
}

@Preview("Aisle list loading")
@Composable
fun AisleListComposableLoadingPreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

    RebonnteTheme {
        AisleListStateComposable(
            uiStateListP = AisleListUIState.IsLoading,
            loadAllAilesP = {},
            onClickMedicineOnBottomBarP = {},
            onClickLogoutOnBottomBarP = mockContext,
            onBackClickP = {},
            launcherP = null
        )
    }
}


@Preview("Aisle list error")
@Composable
fun AisleListComposableErrorPreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

    RebonnteTheme {
        AisleListStateComposable(
            uiStateListP = AisleListUIState.Error("Erreur de test de la preview"),
            loadAllAilesP = {},
            onClickMedicineOnBottomBarP = {},
            onClickLogoutOnBottomBarP = mockContext,
            onBackClickP = {},
            launcherP = null
        )
    }
}