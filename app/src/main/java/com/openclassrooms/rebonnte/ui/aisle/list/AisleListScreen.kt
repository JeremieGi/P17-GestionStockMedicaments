package com.openclassrooms.rebonnte.ui.aisle.list

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.ui.BottomBarComposable
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleDetailActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleListScreen(
    viewModel: AisleListViewModel = hiltViewModel(),
    onClickAddP: () -> Unit,
    onClickMedicineP : () -> Unit,
) {

    Scaffold(
        topBar = {
            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                TopAppBar(
                    title = { Text(text = "Aisle")  },
                )
            }
        },
        bottomBar = {
            BottomBarComposable(
                sActiveScreenP = Screen.CTE_AISLE_LIST_SCREEN,
                onClickMedicinesP = { onClickMedicineP },
                onClickAisleP = { /*TODO JG*/ })
        },
        content = { innerPadding ->

            val uiStateList by viewModel.uiStateListAile.collectAsState()

            // TODO Denis : Mieux vaut appler le viewModel avant le composable ou dedans ?
            LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
                viewModel.loadAllAisle()
            }

            AisleListStateComposable(
                modifier = Modifier.padding(innerPadding),
                uiStateListP = uiStateList,
                loadAllAilesP = viewModel::loadAllAisle
            )



        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onClickAddP()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    )


}

@Composable
fun AisleListStateComposable(
    modifier: Modifier = Modifier,
    uiStateListP: AisleListUIState,
    loadAllAilesP : () -> Unit
) {

    when (uiStateListP) {

        // Chargement
        is AisleListUIState.IsLoading -> {
            LoadingComposable(modifier= modifier)
        }

        // Récupération des données avec succès
        is AisleListUIState.Success -> {

            AisleListComposable(
                modifier = modifier,
                listAisles = uiStateListP.listAisles
            )

        }

        // Exception
        is AisleListUIState.Error -> {

            val error = uiStateListP.sError ?: stringResource(
                R.string.unknown_error
            )

            ErrorComposable(
                modifier= modifier,
                sErrorMessage = error,
                onClickRetryP = {
                    loadAllAilesP()
                }
            )


        }
    }



}

@Composable
fun AisleListComposable(
    modifier: Modifier,
    listAisles: List<Aisle>
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(listAisles) { aisle ->
            AisleItem(aisle = aisle, onClick = {
                startDetailActivity(context, aisle.name)
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
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Arrow")
    }
}



private fun startDetailActivity(context: Context, name: String) {
    val intent = Intent(context, AisleDetailActivity::class.java).apply {
        putExtra(Screen.CTE_PARAM_ID_AISLE, name)
    }
    context.startActivity(intent)
}