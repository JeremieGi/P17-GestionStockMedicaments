package com.openclassrooms.rebonnte.ui.medecine.list

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.openclassrooms.rebonnte.EmbeddedSearchBar
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.ui.BottomBarComposable
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailActivity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListScreen(
    viewModel: MedicineListViewModel = hiltViewModel(),
    onClickAddP: () -> Unit,
    onClickBottomAisleP: () -> Unit
) {


    Scaffold(
        topBar = {
            var isSearchActive by rememberSaveable { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                TopAppBar(
                    title = { Text(text = "Medicines") },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = null)
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    offset = DpOffset(x = 0.dp, y = 0.dp)
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            viewModel.sortByNone()
                                            expanded = false
                                        },
                                        text = { Text("Sort by None") }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            viewModel.sortByName()
                                            expanded = false
                                        },
                                        text = { Text("Sort by Name") }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            viewModel.sortByStock()
                                            expanded = false
                                        },
                                        text = { Text("Sort by Stock") }
                                    )
                                }
                            }
                        }

                    }
                )

                EmbeddedSearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        viewModel.filterByName(it)
                        searchQuery = it
                    },
                    isSearchActive = isSearchActive,
                    onActiveChanged = { isSearchActive = it }
                )

            }

        },
        bottomBar = {
            BottomBarComposable(
                sActiveScreenP = Screen.CTE_MEDICINE_LIST_SCREEN,
                onClickMedicinesP = { /*L'icone sera grisée*/ },
                onClickAislesP = onClickBottomAisleP
            )
        },
        content = { innerPadding ->


            val uiStateMedicines by viewModel.uiStateMedicines.collectAsState()

            LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
                viewModel.loadAllMedicines()
            }

            MedicineListStateComposable(
                modifier = Modifier.padding(innerPadding),
                uiStateMedicinesP = uiStateMedicines
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
fun MedicineListStateComposable(
    modifier: Modifier = Modifier,
    uiStateMedicinesP : MedecineListUIState
) {

    when (uiStateMedicinesP) {

        // Chargement
        is MedecineListUIState.IsLoading -> {
            LoadingComposable(modifier = modifier)
        }

        // Récupération des données avec succès
        is MedecineListUIState.Success -> {

            MedecineListComposable(
                modifier = modifier,
                listMedicines = uiStateMedicinesP.listMedecines
            )

        }

        // Exception
        is MedecineListUIState.Error -> {

            val error = uiStateMedicinesP.sError ?: stringResource(
                R.string.unknown_error
            )

            ErrorComposable(
                modifier= modifier,
                sErrorMessage = error,
                onClickRetryP = {
                    /* TODO JG : hisser la méthode load */
                }
            )


        }
    }


}

@Composable
fun MedecineListComposable(
    modifier: Modifier,
    listMedicines: List<Medicine>) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(listMedicines) { medicine ->
            MedicineItem(medicine = medicine, onClick = {
                startDetailActivity(context, medicine.id)
            })
        }
    }

}

@Composable
fun MedicineItem(medicine: Medicine, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicine.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Stock: ${medicine.stock}", style = MaterialTheme.typography.bodyMedium)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Arrow")
    }
}

private fun startDetailActivity(context: Context, id: String) {
    val intent = Intent(context, MedicineDetailActivity::class.java).apply {
        putExtra(Screen.CTE_PARAM_ID_MEDECINE, id)
    }
    context.startActivity(intent)
}



