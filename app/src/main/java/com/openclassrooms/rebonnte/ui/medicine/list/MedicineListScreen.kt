package com.openclassrooms.rebonnte.ui.medicine.list

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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
    onClickBottomAisleP: () -> Unit,
    onBackClickP: () -> Unit
) {

    val uiStateMedicines by viewModel.uiStateMedicines.collectAsState()

    LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
        viewModel.loadAllMedicines()
    }

    // Pour déclencher le rafraichissement en cas de modification des données dans l'Activity de détails
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
        onItemSwiped = viewModel::deleteMedicineById,
        onClickLogoutOnBottomBarP = viewModel::logout,
        onBackClickP = onBackClickP,
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
    onClickLogoutOnBottomBarP : (Context) -> Task<Void>,
    onBackClickP: () -> Unit
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
                onClickAislesP = onClickBottomAisleP,
                onClickLogoutP = onClickLogoutOnBottomBarP,
                onBackClickP = onBackClickP
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

@OptIn(ExperimentalFoundationApi::class)
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
        items(
            items =listMedicines,
            key = { it.id }
        ) { medicine ->

            // Composant qui permet de swiper dans la lazyColumn
            SwipeBox(
                medicineP = medicine,
                onDelete = {
                    onItemSwiped(medicine.id)
                },
                modifier = Modifier.animateItemPlacement()
            ) {


                MedicineItem(
                    medicineP = medicine,
                    onClick = {
                        startDetailActivity(context, launcher, medicine.id)
                    }
                )

            }

        }
    }

}

@Composable
fun MedicineItem(
    medicineP: Medicine,
    onClick: () -> Unit
) {

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

// Doc : https://medium.com/@shivathapaa/apply-swipetodismissbox-in-android-jetpack-compose-4b9cec46355e
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBox(
    modifier: Modifier = Modifier,
    medicineP: Medicine,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    // Permet de connaître l'état du swipe en cours
    val swipeState = rememberSwipeToDismissBoxState()


    // Composant de Compose
    SwipeToDismissBox(
        modifier = modifier.animateContentSize(), // Animation lors du changement de taille du composant
        state = swipeState,
        backgroundContent = {

            // Affichage de la partie qui va apparaître lors du swipe

            // Afficher l'icône et le fond uniquement si l'élément est en cours de swipe EndToStart
            if (swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }

        }
    ) {
        content()
    }

    // État pour gérer l'affichage de la boîte de dialogue de confirmation
    var showDialog by remember { mutableStateOf(false) }


    // Gestion du swipe de suppression
    if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart) {

        // Affichage de la boite de dialogue de confirmation
        showDialog = true

        // Je remets la ligne en non-swipée car sinon ce bloc de code s'exécute à chaque redessin et l'AlertDialog réapparait
        LaunchedEffect(swipeState) {
            // Retour immédiat à la position initiale
            swipeState.snapTo(SwipeToDismissBoxValue.Settled)
        }


    }

    // Si l'utilisateur a confirmé la suppression
    if (showDialog) {


        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text(stringResource(R.string.confirm_deletion)) },
            text = { Text(stringResource(R.string.delete_confirm_question,medicineP.name)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete() // Appeler la fonction de suppression
                    showDialog = false // Fermer la boîte de dialogue
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false // Fermer la boîte de dialogue
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )

    }



    // Désactivation du swipe StartToEnd
    // Si l'utilisateur swipe vers la droite, le retour à la position initiale se fait instantanément
    if (swipeState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
        LaunchedEffect(swipeState) {
            // Retour immédiat à la position initiale
            swipeState.snapTo(SwipeToDismissBoxValue.Settled)
        }
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

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

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
            onItemSwiped = {},
            onClickLogoutOnBottomBarP = mockContext,
            onBackClickP = {},
        )
    }
}

@Preview("Medicine list loading")
@Composable
fun MedicineListComposableLoadingPreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

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
            onItemSwiped = {},
            onClickLogoutOnBottomBarP = mockContext,
            onBackClickP = {},
        )
    }
}


@Preview("Medicine list error")
@Composable
fun MedicineListComposableErrorPreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }


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
            onItemSwiped = {},
            onClickLogoutOnBottomBarP = mockContext,
            onBackClickP = {},
        )
    }
}


@Preview("Medicine delete  error")
@Composable
fun MedicineListComposableDeleteErrorPreview() {

    val mockContext : (Context) -> Task<Void> = { _ ->
        // Simulate a successful sign-out task
        Tasks.forResult(null)
    }

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
            onItemSwiped = {},
            onClickLogoutOnBottomBarP = mockContext,
            onBackClickP = {},
        )
    }
}