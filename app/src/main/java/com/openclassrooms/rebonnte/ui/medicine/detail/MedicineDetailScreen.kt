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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repositoryStock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleDetailStateComposable
import com.openclassrooms.rebonnte.ui.aisle.detail.AisleDetailUIState
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


@Composable
fun MedicineDetailScreen(
    idMedicineP : String,
    viewModel: MedicineDetailViewModel = hiltViewModel(),
) {

    val uiStateMedicineDetail by viewModel.uiStateMedicineDetail.collectAsState()

    LaunchedEffect(Unit) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable
        viewModel.loadMedicineByID(idMedicineP)
    }

    MedicineDetailStateComposable(
        uiStateMedicineDetailP = uiStateMedicineDetail,
        loadMedicineByIDP = { viewModel.loadMedicineByID(idMedicineP) }
    )


}

@Composable
fun MedicineDetailStateComposable(
    uiStateMedicineDetailP: MedicineDetailUIState,
    loadMedicineByIDP : () -> Unit,
) {



    Scaffold { contentPadding ->

        when (uiStateMedicineDetailP) {

            // Chargement
            is MedicineDetailUIState.IsLoading -> {
                LoadingComposable(Modifier.padding(contentPadding))
            }

            // Récupération des données avec succès
            is MedicineDetailUIState.Success -> {

                MedicineDetailSuccessComposable(
                    modifier=Modifier.padding(contentPadding),
                    medicineP = uiStateMedicineDetailP.medecineDetail
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
        }



    }

}

@Composable
fun MedicineDetailSuccessComposable(
    modifier: Modifier,
    medicineP: Medicine
) {

    // TODO JG : A mettre dans le viewModel
    val stock by remember { mutableIntStateOf(medicineP.stock) }

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
//                if (stock > 0) {
                    // TODO JG : Désincrémenter le stock  => Enregistrer l'historique
//                    medicines[medicines.size].histories.toMutableList().add(
//                        History(
//                            medicine.name,
//                            "efeza56f1e65f",
//                            Date().toString(),
//                            "Updated medicine details"
//                        )
//                    )
//                    stock--
//                }
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Minus One"
                )
            }
            TextField(
                value = stock.toString(),
                onValueChange = {},
                label = { Text("Stock") },
                enabled = false,
                modifier = Modifier.weight(1f)
            )
            // Incrémenter le stock
            IconButton(onClick = {
                // TODO JG : Incrémenter le stock  -> Enregistrer l'historique
//                medicines[medicines.size].histories.toMutableList().add(
//                    History(
//                        medicine.name,
//                        "efeza56f1e65f",
//                        Date().toString(),
//                        "Updated medicine details"
//                    )
//                )
//                stock++
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Plus One"
                )
            }
        }
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
            loadMedicineByIDP = {}
        )

    }
}


@Preview("Medicine Detail Success")
@Composable
fun MedicineDetailStateComposableSuccessPreview() {


    val listFakeMedicines = StockFakeAPI.initFakeMedicines()
    val uiStateSuccess = MedicineDetailUIState.Success(listFakeMedicines[0])

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateSuccess,
            loadMedicineByIDP = {}
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
            loadMedicineByIDP = {}
        )
    }
}
