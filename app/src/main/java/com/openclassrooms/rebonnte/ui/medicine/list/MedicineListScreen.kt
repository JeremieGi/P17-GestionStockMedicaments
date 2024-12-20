package com.openclassrooms.rebonnte.ui.medicine.list

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.TestTags
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
    navigateLaunchScreenP : () -> Unit
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
        navigateLaunchScreenP = navigateLaunchScreenP,
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
    navigateLaunchScreenP: () -> Unit
) {

    val context = LocalContext.current

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
                                IconButton(
                                    onClick = { expanded = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = stringResource(R.string.sort_medicines)
                                    )
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
                navigateLaunchScreenP = navigateLaunchScreenP
            )
        },
        content = { innerPadding ->


            when (uiStateMedicinesP) {

                // Chargement
                is MedicineListUIState.IsLoading -> {
                    LoadingComposable(modifier = Modifier.padding(innerPadding))
                }

                // Récupération des données avec succès
                is MedicineListUIState.LoadSuccess -> {

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

                    Toast.makeText(context,
                        stringResource(R.string.delete_error, error), Toast.LENGTH_SHORT).show()

                }

                is MedicineListUIState.DeleteSuccess -> {
                    Toast.makeText(context,
                        stringResource(R.string.medicine_successfull_deleted), Toast.LENGTH_SHORT).show()
                    loadAllMedicinesP()
                }
            }

        },
        floatingActionButton = {

            FloatingActionButton(onClick = {
                startDetailActivity(context, launcher, id=MedicineDetailActivity.PARAM_MEDICINE_ADD) // ID vide = mode ajout
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_medicine)
                )
            }
        }
    )





}


@Composable
fun EmbeddedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf(query) }
    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }

    val shape: Shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchActive) {
            IconButton(onClick = { activeChanged(false) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        BasicTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                onQueryChange(query)
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                innerTextField()
            },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface
            ),
        )

        if (isSearchActive && searchQuery.isNotEmpty()) {
            IconButton(onClick = {
                searchQuery = ""
                onQueryChange("")
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
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
            items = listMedicines,
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
                    },
                    onItemSwiped = onItemSwiped
                )

            }

        }
    }

}

@Composable
fun MedicineItem(
    medicineP: Medicine,
    onClick: () -> Unit,
    onItemSwiped: (id : String) -> Unit,
) {

    val context = LocalContext.current

    // Utilisation de la Card dédié aux tests instrumentés
    Card(
        modifier = Modifier
            .testTag("${TestTags.MEDICINE_ID_PREFIX}${medicineP.id}") // Dans le test instru, je n'arrive pas à lister les noeuds dont le tags commence par TestTags.MEDICINE_ID_PREFIX (mais çà me permet de sélectionner un élement par son ID)
            .semantics {
                // Ajout d'une action personnalisée pour la suppression (car il est impossible de swiper avec Talkback activé)
                // Pour y accéder :
                //  - il faut se positionner sur la carte
                //  - clic rapide avec 3 doigts : affiche le menu talkBack -> choisir Actions
                //  - on voit apparaître l'action personnalisée
                //  - le clic sur l'action déclenche le code ci-dessous
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = context.getString(R.string.deleteParam, medicineP.name)
                    ) {
                        // Appel de la fonction pour supprimer l'élément
                        onItemSwiped(medicineP.id)
                        true
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Définit la couleur de fond comme transparente
        )
    ){

        Row(
            modifier = Modifier
                .testTag(TestTags.MEDICINE_ITEM) // Permet de lister les items dans le test instrumenté
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = medicineP.name,
                    style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${stringResource(R.string.stock)}: ${medicineP.stock}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Arrow")
        }

    }



}

// Doc : https://medium.com/@shivathapaa/apply-swipetodismissbox-in-android-jetpack-compose-4b9cec46355e
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBox(
    modifier: Modifier = Modifier,
    medicineP: Medicine,
    onDelete: () -> Unit,
    medicineItemComposableP : @Composable () -> Unit
) {
    // Permet de connaître l'état du swipe en cours
    val swipeState = rememberSwipeToDismissBoxState()


    // Composant de Compose
    SwipeToDismissBox(
        modifier = modifier.animateContentSize(), // Animation lors du changement de taille du composant
        state = swipeState,
        enableDismissFromStartToEnd = false, // Désactivation du swipe startToEnd
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
        medicineItemComposableP()
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
    val uiStateSuccess = MedicineListUIState.LoadSuccess(listFakeMedicines)

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
            navigateLaunchScreenP = {},
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
            navigateLaunchScreenP = {},
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
            navigateLaunchScreenP = {},
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
            navigateLaunchScreenP = {},
        )
    }
}