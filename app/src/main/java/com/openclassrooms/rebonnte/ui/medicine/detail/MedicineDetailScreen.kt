package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

// Cet écran fait l'affichage de détails d'un médicaement et aussi son ajout
fun bDetailMode(idMedicineP: String): Boolean {
    return idMedicineP.isNotEmpty()
}

@Composable
fun MedicineDetailScreen(
    idMedicineP : String,
    viewModel: MedicineDetailViewModel = hiltViewModel(),
    onMedicineUpdated : () -> Unit,
) {

    // Une actionBar avec le nom de l'appli ici car l'activity à un thème avec ActionBar

    val uiStateMedicineDetail by viewModel.uiStateMedicineDetail.collectAsState()

    if (bDetailMode(idMedicineP)){
        LaunchedEffect(idMedicineP) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
            viewModel.loadMedicineByID(idMedicineP)
        }
    }
    else{
        // TODO : Mode ajout à implémenter
    }



    MedicineDetailStateComposable(
        uiStateMedicineDetailP = uiStateMedicineDetail,
        loadMedicineByIDP = { viewModel.loadMedicineByID(idMedicineP) },
        decrementStockP = viewModel::decrementStock,
        incrementStockP = viewModel::incrementStock,
        updateStockP = viewModel::updateStock,
        onMedicineUpdated = onMedicineUpdated
    )


}



@Composable
fun MedicineDetailStateComposable(
    uiStateMedicineDetailP: MedicineDetailUIState,
    loadMedicineByIDP : () -> Unit,
    decrementStockP : () -> Unit,
    incrementStockP : () -> Unit,
    updateStockP : () -> Unit,
    onMedicineUpdated: () -> Unit,
) {



    Scaffold { contentPadding ->

        when (uiStateMedicineDetailP) {

            // Chargement
            is MedicineDetailUIState.IsLoading -> {
                LoadingComposable(Modifier.padding(contentPadding))
            }

            // Récupération des données avec succès
            is MedicineDetailUIState.LoadSuccess -> {

                MedicineDetailSuccessComposable(
                    modifier=Modifier.padding(contentPadding),
                    medicineP = uiStateMedicineDetailP.medicineDetail,
                    decrementStockP = decrementStockP,
                    incrementStockP = incrementStockP,
                    updateStockP = updateStockP
                )

            }

            // Exception
            is MedicineDetailUIState.Error -> {

                val error = uiStateMedicineDetailP.sError ?: stringResource(
                    R.string.unknown_error
                )


                ErrorComposable(
                    modifier=Modifier
                        .padding(contentPadding)
                    ,
                    sErrorMessage = error,
                    onClickRetryP = loadMedicineByIDP
                )


            }

            is MedicineDetailUIState.UploadSuccess -> {
                onMedicineUpdated() // Retour à la liste avec notification de la modification
            }
        }



    }

}

@Composable
fun MedicineDetailSuccessComposable(
    modifier: Modifier,
    medicineP: Medicine,
    decrementStockP : () -> Unit,
    incrementStockP : () -> Unit,
    updateStockP : () -> Unit
) {

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        TextField(
            value = medicineP.name,
            onValueChange = {},
            label = { Text(stringResource(R.string.name)) },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = medicineP.oAisle.name,
            onValueChange = {},
            label = { Text(stringResource(R.string.aisle)) },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Désincrémenter le stock
            IconButton(onClick = {
                // Correction T010
                // Avant ici : java.lang.IndexOutOfBoundsException: Index 1 out of bounds for length 1
                // Suite à un appel directement aux donénes en mémorie sans pattern MVVM
                decrementStockP()
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.minus_one)
                )
            }
            TextField(
                value = medicineP.stock.toString(),
                onValueChange = {},
                label = { Text(stringResource(R.string.stock)) },
                enabled = false,
                modifier = Modifier.weight(1f)
            )
            // Incrémenter le stock
            IconButton(onClick = {
                incrementStockP()
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.plus_one)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // T003a - Mise à jour de stock : Ne doit pas bloquer le thread principal
        Button(
            modifier = Modifier.fillMaxWidth(),
            content = {
                Text(text = stringResource(id = R.string.validate))
            },
            onClick = {
                updateStockP()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.history), style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(medicineP.histories) { history ->
                HistoryItem(history = history)
            }
        }


    }

}

// T011a - Affichage de l’historique - Améliorer l’UI
// TODO Denis : "L’affichage de l’historique des changements est peu esthétique et se trouve en
//bas de la liste. Intégrer l’historique dans le contenu scrollable de la fiche détail
//d’un magasin serait appréciable."
// => C'est déjà fait même si esthétiquement pas top => que faut-il faire exactement ?s
@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // TODO JG : Ne pas oublier d'utiliser les ressources chaines ici
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "User: ${history.author.sEmail}")
            Text(text = "Date: ${history.date}")
            Text(text = "Details: ${history.details}")
        }
    }
}



@Preview("Medicine Detail Loading")
@Composable
fun MedicineDetailStateComposableLoadingPreview() {

    val uiStateLoading = MedicineDetailUIState.IsLoading

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateLoading,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateStockP = {},
            onMedicineUpdated = {}
        )

    }
}


@Preview("Medicine Detail Success")
@Composable
fun MedicineDetailStateComposableSuccessPreview() {


    val listFakeMedicines = StockFakeAPI.initFakeMedicines()
    val uiStateSuccess = MedicineDetailUIState.LoadSuccess(listFakeMedicines[0])

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateSuccess,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateStockP = {},
            onMedicineUpdated = {}
        )

    }


}


@Preview("Medicine Detail Error")
@Composable
fun MedicineDetailStateComposableErrorPreview() {

    val uiStateError = MedicineDetailUIState.Error("Message de test pour la preview")

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateError,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateStockP = {},
            onMedicineUpdated = {}
        )
    }
}
