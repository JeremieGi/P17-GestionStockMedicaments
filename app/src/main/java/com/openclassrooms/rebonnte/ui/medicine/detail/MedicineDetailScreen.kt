package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repositoryStock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


@Composable
fun MedicineDetailScreen(
    idMedicineP : String,
    viewModel: MedicineDetailViewModel = hiltViewModel(),
    onBackClick : () -> Unit,
) {

    val uiStateMedicineDetail by viewModel.uiStateMedicineDetail.collectAsState()

    LaunchedEffect(idMedicineP) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
        viewModel.loadMedicineByID(idMedicineP)
    }

    MedicineDetailStateComposable(
        uiStateMedicineDetailP = uiStateMedicineDetail,
        loadMedicineByIDP = { viewModel.loadMedicineByID(idMedicineP) },
        decrementStockP = viewModel::decrementStock,
        incrementStockP = viewModel::incrementStock,
        updateStockP = viewModel::updateStock,
        onBackClick = onBackClick
    )


}

@Composable
fun MedicineDetailStateComposable(
    uiStateMedicineDetailP: MedicineDetailUIState,
    loadMedicineByIDP : () -> Unit,
    decrementStockP : () -> Unit,
    incrementStockP : () -> Unit,
    updateStockP : () -> Unit,
    onBackClick: () -> Unit,
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
                    medicineP = uiStateMedicineDetailP.medecineDetail,
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
                onBackClick() // Retour à la liste
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
            label = { Text("Name") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = medicineP.oAisle.name,
            onValueChange = {},
            label = { Text("Aisle") },
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
                // Avant ici : java.lang.IndexOutOfBoundsException: Index 1 out of bounds for length 1
                // Suite à un appel directement aux donénes en mémorie sans pattern MVVM
                decrementStockP()
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Minus One"
                )
            }
            TextField(
                value = medicineP.stock.toString(),
                onValueChange = {},
                label = { Text("Stock") },
                enabled = false,
                modifier = Modifier.weight(1f)
            )
            // Incrémenter le stock
            IconButton(onClick = {
                incrementStockP()
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Plus One"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

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

        Text(text = "History", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(medicineP.histories) { history ->
                HistoryItem(history = history)
            }
        }


    }

}

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "User: ${history.userId}")
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
            onBackClick = {}
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
            onBackClick = {}
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
            onBackClick = {}
        )
    }
}