package com.openclassrooms.rebonnte.ui.aisle.list

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openclassrooms.rebonnte.EmbeddedSearchBar
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.currentRoute
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.ui.BottomBarComposable
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleDetailActivity
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleItem
import com.openclassrooms.rebonnte.ui.medecine.list.MedicineListStateComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleScreen(
    viewModel: AisleListViewModel = hiltViewModel(),
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
                onClickMedicinesP = { /*TODO JG*/ },
                onClickAisleP = { /*TODO JG*/ })
        },
        content = { innerPadding ->

            val uiStateList by viewModel.uiStateListAile.collectAsState()

            AisleListStateComposable(
                modifier = Modifier.padding(innerPadding),
                uiStateListP = uiStateList,
                loadAllAilesP = viewModel::loadAllAisle,
                onAilesClickP = onEventClickP,
                onClickAddP = onClickAddP,
                onClickBottomMedicineP = onClickProfileP
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
    loadAllAilesP: Any,
    onClickAddP: Any,
    onClickBottomMedicineP: Any) {

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
                    loadAllEventsP(searchText.text, bSortAsc)
                }
            )


        }
    }



}

@Composable
fun AisleListComposable(
    modifier: Modifier,
    listAisles: List<Aisle>) {

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
        putExtra("nameAisle", name)
    }
    context.startActivity(intent)
}