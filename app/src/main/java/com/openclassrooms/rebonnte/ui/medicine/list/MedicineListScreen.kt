package com.openclassrooms.rebonnte.ui.medicine.list

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.EmbeddedSearchBar
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.BottomBarComposable
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailActivity
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme



@Composable
fun MedicineListScreen(
    viewModel: MedicineListViewModel = hiltViewModel(),
    onClickBottomAisleP: () -> Unit
) {


    val uiStateMedicines by viewModel.uiStateMedicines.collectAsState()

    LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
        viewModel.loadAllMedicines()
    }

    // Pour déclencher le rafraichissement en cas de modification des donénes dans l'Activity de détails
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == MedicineDetailActivity.RESULT_MEDICINE_UPDATE) {
           viewModel.loadAllMedicines()
        }
    }


    MedicineListStateComposable(
        uiStateMedicinesP = uiStateMedicines,
        sortByNoneP = viewModel::sortByNone,
        sortByNameP = viewModel::sortByName,
        sortByStockP = viewModel::sortByStock,
        filterByNameP = viewModel::filterByName,
        loadAllMedicinesP = viewModel::loadAllMedicines,
        onClickBottomAisleP = onClickBottomAisleP,
        launcher = launcher,
        onItemSwiped = viewModel::deleteMedicineById
    )


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListStateComposable(
    uiStateMedicinesP : MedicineListUIState,
    sortByNoneP : () -> Unit,
    sortByNameP : () -> Unit,
    sortByStockP : () -> Unit,
    filterByNameP : (String) -> Unit,
    loadAllMedicinesP : () -> Unit,
    onClickBottomAisleP: () -> Unit,
    launcher: ActivityResultLauncher<Intent>?,
    onItemSwiped: (id : String) -> Unit,
) {


    Scaffold(
        topBar = {
            var isSearchActive by rememberSaveable { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.medicines)) },
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
                                            sortByNoneP()
                                            expanded = false
                                        },
                                        text = { Text(stringResource(R.string.sort_by_none)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            sortByNameP()
                                            expanded = false
                                        },
                                        text = { Text(stringResource(R.string.sort_by_name)) }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            sortByStockP()
                                            expanded = false
                                        },
                                        text = { Text(stringResource(R.string.sort_by_stock)) }
                                    )
                                }
                            }
                        }

                    }
                )

                EmbeddedSearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        filterByNameP(it)
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


            when (uiStateMedicinesP) {

                // Chargement
                is MedicineListUIState.IsLoading -> {
                    LoadingComposable(modifier = Modifier.padding(innerPadding))
                }

                // Récupération des données avec succès
                is MedicineListUIState.Success -> {

                    MedicineListComposable(
                        modifier = Modifier.padding(innerPadding),
                        listMedicines = uiStateMedicinesP.listMedicines,
                        launcher = launcher,
                        onItemSwiped = onItemSwiped
                    )

                }

                // Erreur lors du chargement de la liste
                is MedicineListUIState.LoadingError -> {

                    val error = uiStateMedicinesP.sError ?: stringResource(
                        R.string.unknown_error
                    )

                    ErrorComposable(
                        modifier= Modifier.padding(innerPadding),
                        sErrorMessage = error,
                        onClickRetryP = loadAllMedicinesP
                    )

                }

                // Erreur lors de la suppression
                is MedicineListUIState.DeleteError -> {

                    val error = uiStateMedicinesP.sError ?: stringResource(
                        R.string.unknown_error
                    )

                    ErrorComposable(
                        modifier= Modifier.padding(innerPadding),
                        sErrorMessage = error,
                        onClickRetryP = null
                    )

                }
            }

        },
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(onClick = {
                startDetailActivity(context, launcher, id=MedicineDetailActivity.PARAM_MEDICINE_ADD) // ID vide = mode ajout
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    )





}

@Composable
fun MedicineListComposable(
    modifier: Modifier,
    listMedicines: List<Medicine>,
    onItemSwiped: (id : String) -> Unit,
    launcher: ActivityResultLauncher<Intent>?) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(listMedicines) { medicine ->
            MedicineItem(
                medicineP = medicine,
                onClick = {
                    startDetailActivity(context, launcher, medicine.id)
                },
                onItemSwiped = onItemSwiped
            )
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MedicineItem(
    medicineP: Medicine,
    onClick: () -> Unit,
    onItemSwiped: (id : String) -> Unit) {

//    val dismissState = rememberDismissState {
//        if (it == DismissValue.DismissedToEnd) {
//            onItemSwiped(item) // Appeler la fonction de suppression après le swipe
//            true
//        } else {
//            false
//        }
//    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicineP.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "${stringResource(R.string.stock)}: ${medicineP.stock}", style = MaterialTheme.typography.bodyMedium)
        }

        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Arrow")
    }

}



// Lance l'activity en utilisant un launcher pour savoir quand l'activity se ferme
private fun startDetailActivity(
    context: Context,
    launcher: ActivityResultLauncher<Intent>?,
    id: String
) {

    // launcher peut-être nullable juste pour les previews Compose
    launcher?.let {
        val intent = Intent(context, MedicineDetailActivity::class.java).apply {
            putExtra(Screen.CTE_PARAM_ID_MEDICINE, id)
        }
        //context.startActivity(intent)
        it.launch(intent)
    }



}




@Preview(
    name ="Medicine list success",
    showBackground = true
)
@Composable
fun MedicineListComposableSuccessPreview() {

    val listFakeMedicines = StockFakeAPI.initFakeMedicines()
    val uiStateSuccess = MedicineListUIState.Success(listFakeMedicines)

    RebonnteTheme {

        MedicineListStateComposable(
            uiStateMedicinesP = uiStateSuccess,
            sortByNoneP = {},
            sortByNameP = {},
            sortByStockP = {},
            filterByNameP = {},
            loadAllMedicinesP = {},
            onClickBottomAisleP = {},
            launcher = null,
            onItemSwiped = {}
        )
    }
}

@Preview("Medicine list loading")
@Composable
fun MedicineListComposableLoadingPreview() {

    RebonnteTheme {
        MedicineListStateComposable(
            uiStateMedicinesP = MedicineListUIState.IsLoading,
            sortByNoneP = {},
            sortByNameP = {},
            sortByStockP = {},
            filterByNameP = {},
            loadAllMedicinesP = {},
            onClickBottomAisleP = {},
            launcher = null,
            onItemSwiped = {}
        )
    }
}


@Preview("Medicine list error")
@Composable
fun MedicineListComposableErrorPreview() {

    RebonnteTheme {
        MedicineListStateComposable(
            uiStateMedicinesP = MedicineListUIState.LoadingError("Erreur de test de la preview"),
            sortByNoneP = {},
            sortByNameP = {},
            sortByStockP = {},
            filterByNameP = {},
            loadAllMedicinesP = {},
            onClickBottomAisleP = {},
            launcher = null,
            onItemSwiped = {}
        )
    }
}


@Preview("Medicine delete  error")
@Composable
fun MedicineListComposableDeleteErrorPreview() {

    RebonnteTheme {
        MedicineListStateComposable(
            uiStateMedicinesP = MedicineListUIState.DeleteError("Erreur de test de la preview"),
            sortByNoneP = {},
            sortByNameP = {},
            sortByStockP = {},
            filterByNameP = {},
            loadAllMedicinesP = {},
            onClickBottomAisleP = {},
            launcher = null,
            onItemSwiped = {}
        )
    }
}