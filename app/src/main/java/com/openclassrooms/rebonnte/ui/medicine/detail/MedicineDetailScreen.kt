package com.openclassrooms.rebonnte.ui.medicine.detail


import android.content.res.Configuration
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.TestTags
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Cet écran fait l'affichage de détails d'un médicament, la mise à jour du stock et l'ajout
 */
@Composable
fun MedicineDetailScreen(
    idMedicineP : String,
    viewModel: MedicineDetailViewModel = hiltViewModel(),
    onMedicineUpdated : () -> Unit,
) {

    // Une actionBar avec le nom de l'appli ici car l'activity à un thème avec ActionBar

    val uiStateMedicineDetail by viewModel.uiStateMedicineDetail.collectAsState()


    LaunchedEffect(idMedicineP) { // Pour déclencher l'effet secondaire une seule fois au cours du cycle de vie de ce composable

        // En mode ajout
        if (idMedicineP==MedicineDetailActivity.PARAM_MEDICINE_ADD){
            // T008 - Ajout d’un médicament
            // Initialise un objet Medecine vide dans le viewModel dans le UiState
            viewModel.initNewMedicine()
        }
        else{
            // Charge les données
            viewModel.loadMedicineByID(idMedicineP)
        }

    }

    MedicineDetailStateComposable(
        uiStateMedicineDetailP = uiStateMedicineDetail,
        loadMedicineByIDP = { viewModel.loadMedicineByID(idMedicineP) },
        decrementStockP = viewModel::decrementStock,
        incrementStockP = viewModel::incrementStock,
        updateOrInsertMedicineP = viewModel::updateOrInsertMedicine,
        onMedicineUpdated = onMedicineUpdated,
        bAddModeP = viewModel.bAddMode(),
        onInputNameChangedP = viewModel::onInputNameChanged,
        onInputAisleChangedP = viewModel::onInputAisleChanged
    )


}



@Composable
fun MedicineDetailStateComposable(
    uiStateMedicineDetailP: MedicineDetailUIState,
    loadMedicineByIDP : () -> Unit,
    decrementStockP : () -> Unit,
    incrementStockP : () -> Unit,
    updateOrInsertMedicineP : () -> Unit,
    onMedicineUpdated: () -> Unit,
    bAddModeP : Boolean,
    onInputNameChangedP : (String) -> Unit,
    onInputAisleChangedP : (String) -> Unit
) {


    Scaffold { contentPadding ->

        when (uiStateMedicineDetailP.currentStateMedicine) {

            // Chargement
            is CurrentMedicineUIState.IsLoading -> {
                LoadingComposable(Modifier.padding(contentPadding))
            }

            // Récupération des données avec succès
            is CurrentMedicineUIState.LoadSuccess -> {

                MedicineDetailSuccessComposable(
                    modifier=Modifier.padding(contentPadding),
                    medicineP = uiStateMedicineDetailP.currentStateMedicine.medicineValue,
                    formErrorP = uiStateMedicineDetailP.formError,
                    decrementStockP = decrementStockP,
                    incrementStockP = incrementStockP,
                    updateOrInsertMedicineP = updateOrInsertMedicineP,
                    bAddModeP = bAddModeP,
                    onInputNameChangedP = onInputNameChangedP,
                    onInputAisleChangedP = onInputAisleChangedP,
                    listAisleP = uiStateMedicineDetailP.listAisles
                )

            }

            // Ajout ou mise à jour avec succès
            is CurrentMedicineUIState.ValidateSuccess -> {
                onMedicineUpdated() // Retour à la liste avec notification de la modification
            }

            else -> {
                // Cas d'erreur

                val onRetry : () -> Unit
                val sError : String

                when (uiStateMedicineDetailP.currentStateMedicine) {
                    is CurrentMedicineUIState.LoadError -> {
                        sError = uiStateMedicineDetailP.currentStateMedicine.sError
                        onRetry = loadMedicineByIDP
                    }
                    is CurrentMedicineUIState.ValidateErrorRepository -> {
                        sError = uiStateMedicineDetailP.currentStateMedicine.sError
                        onRetry = updateOrInsertMedicineP
                    }
                    is CurrentMedicineUIState.ValidateErrorUserUnlogged -> {
                        sError = stringResource(R.string.impossible_to_validate_a_medicine_without_login)
                        onRetry = updateOrInsertMedicineP
                    }
                    else -> {
                        sError = "Unkown State"
                        onRetry = {}
                    }
                }

                ErrorComposable(
                    modifier=Modifier
                        .padding(contentPadding)
                    ,
                    sErrorMessage = sError,
                    onClickRetryP = onRetry
                )

            }
        }



    }

}

@Composable
fun MedicineDetailSuccessComposable(
    modifier: Modifier,
    medicineP: Medicine,
    formErrorP : FormErrorAddMedicine?,
    decrementStockP : () -> Unit,
    incrementStockP : () -> Unit,
    updateOrInsertMedicineP : () -> Unit,
    bAddModeP : Boolean,
    onInputNameChangedP : (String) -> Unit,
    onInputAisleChangedP : (String) -> Unit,
    listAisleP : List<Aisle>?,
) {


    LazyColumn(modifier = modifier
        .padding(16.dp)
        .fillMaxSize()
    ) {

        item {

            // j'ai utilisé item qui permet de faire une entête de lazyColumn. NestedScroll me parait trop compliqué pour ce cas.
            // .verticalScroll(rememberScrollState()) // pas besoin
            Column{
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_NAME),
                    value = medicineP.name,
                    isError = (formErrorP is FormErrorAddMedicine.NameError),
                    onValueChange = {
                        onInputNameChangedP(it)
                    },
                    label = { Text(stringResource(R.string.name)) },
                    enabled = bAddModeP,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledLabelColor  = MaterialTheme.colorScheme.onSurface
                    )
                )
                if (formErrorP is FormErrorAddMedicine.NameError) {
                    Text(
                        text = stringResource(id = R.string.mandatoryname),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                AisleSelectorComposable(
                    medicineP = medicineP,
                    formErrorP = formErrorP,
                    bAddModeP = bAddModeP,
                    onInputAisleChangedP = onInputAisleChangedP,
                    listAisleP = listAisleP
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
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .testTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_STOCK),
                        value = medicineP.stock.toString(),
                        isError = (formErrorP is FormErrorAddMedicine.StockError),
                        onValueChange = {}, // Paramètre obligatoire mais champ grisé => onValueChange jamais exécuté
                        label = { Text(stringResource(R.string.stock)) },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledLabelColor  = MaterialTheme.colorScheme.onSurface
                        )

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
                if (formErrorP is FormErrorAddMedicine.StockError) {
                    Text(
                        text = stringResource(id = R.string.mandatorystock),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // T003a - Mise à jour de stock : Ne doit pas bloquer le thread principal
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Text(text = stringResource(id = R.string.validate))
                    },
                    onClick = {
                        updateOrInsertMedicineP()
                    }
                )

                // Historique uniquement en mode détails
                if (!bAddModeP){
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = stringResource(R.string.history), style = MaterialTheme.typography.titleLarge)

                }


            }


        }

        items(medicineP.histories) { history ->
            HistoryItem(history = history)
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleSelectorComposable(
    medicineP: Medicine,
    formErrorP : FormErrorAddMedicine?,
    bAddModeP : Boolean,
    onInputAisleChangedP : (String) -> Unit,
    listAisleP : List<Aisle>?,
) {

    // État pour contrôler l'ouverture du menu déroulant
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Initialisation du FocusRequester
    val focusRequester = remember { FocusRequester() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {


        OutlinedTextField(
            modifier = Modifier
                .testTag(TestTags.MEDICINE_DETAIL_TEXT_FIELD_AISLE)
                .fillMaxWidth()
                .menuAnchor()
                .focusRequester(focusRequester) // Associe le FocusRequester ici
            ,
            value = medicineP.oAisle.name,
            isError = (
                    formErrorP is FormErrorAddMedicine.AisleErrorEmpty
                            ||
                            formErrorP is FormErrorAddMedicine.AisleErrorNoExist
                    ),
            onValueChange = {
                onInputAisleChangedP(it)
            },
            label = { Text(stringResource(R.string.aisle)) },
            enabled = bAddModeP,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledLabelColor  = MaterialTheme.colorScheme.onSurface
            )
        )

        // Ce composant affiche une pop-up avec la liste des allées
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listAisleP?.forEach { aisle ->
                DropdownMenuItem(
                    text = { Text(aisle.name) },
                    onClick = {
                        onInputAisleChangedP(aisle.name) // Met à jour l'option sélectionnée
                        expanded = false // Ferme le menu
                    }
                )
            }
        }

    }
    if (formErrorP is FormErrorAddMedicine.AisleErrorEmpty) {
        Text(
            text = stringResource(R.string.please_select_an_aisle),
            color = MaterialTheme.colorScheme.error,
        )
    }
    if (formErrorP is FormErrorAddMedicine.AisleErrorNoExist) {
        Text(
            text = stringResource(R.string.please_select_an_existing_aisle),
            color = MaterialTheme.colorScheme.error,
        )
    }

}


// T011a - Affichage de l’historique - Améliorer l’UI
@Composable
fun HistoryItem(history: History) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(5.dp)) {

            HistoryLine(stringResource(R.string.user),history.author.sEmail)

            // Pattern 18 juin 1985
            val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
            // Formater la date
            val formattedDate = formatter.format(history.date)

            HistoryLine(stringResource(R.string.date),formattedDate)

            HistoryLine(stringResource(R.string.details),history.details)

        }
    }
}

@Composable
fun HistoryLine(sLabelP : String, sValueP : String) {

    // Pourcentage de la largeur occupée pour l'affichage des labels
    val fTitlePercent = 0.25f

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            modifier = Modifier.weight(fTitlePercent),
            fontWeight = FontWeight.Bold,
            text = sLabelP,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            modifier = Modifier.weight(1-fTitlePercent),
            text = sValueP,
            style = MaterialTheme.typography.bodyMedium
        )
    }

}


@Preview("Medicine Detail Success - Mode Details")
@Composable
fun MedicineDetailStateComposableSuccessPreview() {


    val listFakeMedicines = StockFakeAPI.initFakeMedicines()
    val uiStateSuccess = MedicineDetailUIState(
        currentStateMedicine = CurrentMedicineUIState.LoadSuccess(listFakeMedicines[0]),
        formError = null
    )

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateSuccess,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateOrInsertMedicineP = {},
            onMedicineUpdated = {},
            bAddModeP = false,
            onInputNameChangedP= {},
            onInputAisleChangedP= {}
        )

    }


}


@Preview(
    name = "Medicine Detail Success - Mode Details - Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MedicineDetailStateComposableSuccessDarkPreview() {


    val listFakeMedicines = StockFakeAPI.initFakeMedicines()
    val uiStateSuccess = MedicineDetailUIState(
        currentStateMedicine = CurrentMedicineUIState.LoadSuccess(listFakeMedicines[0]),
        formError = null
    )

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateSuccess,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateOrInsertMedicineP = {},
            onMedicineUpdated = {},
            bAddModeP = false,
            onInputNameChangedP= {},
            onInputAisleChangedP= {}
        )

    }


}


@Preview("Medicine Detail Success - Mode Add")
@Composable
fun MedicineDetailStateComposableAdd() {


    val listFakeMedicines = StockFakeAPI.initFakeMedicines()
    val fakeMedicine = listFakeMedicines[2]
    val uiStateSuccess = MedicineDetailUIState(
        currentStateMedicine = CurrentMedicineUIState.LoadSuccess(fakeMedicine),
        formError = null,
        listAisles = StockFakeAPI.initFakeAisles()
    )

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateSuccess,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateOrInsertMedicineP = {},
            onMedicineUpdated = {},
            bAddModeP = true,
            onInputNameChangedP= {},
            onInputAisleChangedP= {}
        )

    }


}



@Preview("Medicine Detail Success - Mode Add - with errorForm")
@Composable
fun MedicineDetailStateComposableAddWithErrorForm() {


    val listFakeMedicines = StockFakeAPI.initFakeMedicines()
    val uiStateSuccess = MedicineDetailUIState(
        currentStateMedicine = CurrentMedicineUIState.LoadSuccess(listFakeMedicines[0]),
        formError = FormErrorAddMedicine.NameError
    )

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateSuccess,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateOrInsertMedicineP = {},
            onMedicineUpdated = {},
            bAddModeP = true,
            onInputNameChangedP= {},
            onInputAisleChangedP= {}
        )

    }


}



@Preview("Medicine Detail Loading")
@Composable
fun MedicineDetailStateComposableLoadingPreview() {

    val uiStateLoading = MedicineDetailUIState(
        currentStateMedicine = CurrentMedicineUIState.IsLoading,
        formError = null
    )

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateLoading,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateOrInsertMedicineP = {},
            onMedicineUpdated = {},
            bAddModeP = false,
            onInputNameChangedP= {},
            onInputAisleChangedP= {}
        )

    }
}


@Preview("Medicine Detail Error")
@Composable
fun MedicineDetailStateComposableErrorPreview() {

    val uiStateError = MedicineDetailUIState(
        currentStateMedicine = CurrentMedicineUIState.LoadError("Message de test pour la preview"),
        formError = null
    )

    RebonnteTheme {

        MedicineDetailStateComposable(
            uiStateMedicineDetailP = uiStateError,
            loadMedicineByIDP = {},
            decrementStockP = {},
            incrementStockP = {},
            updateOrInsertMedicineP = {},
            onMedicineUpdated = {},
            bAddModeP = false,
            onInputNameChangedP= {},
            onInputAisleChangedP= {}
        )
    }
}
