package com.openclassrooms.rebonnte.ui.aisle.detail


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.TestTags
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


@Composable
fun AisleDetailScreen(
    modifier: Modifier = Modifier,
    idAisleP : String,
    viewModel: AisleDetailViewModel = hiltViewModel(),
    onAisleInsertedP : () -> Unit,
) {

    // Lecture du post
    val uiStateAisleDetail by viewModel.uiStateAisleDetail.collectAsState()

    LaunchedEffect(idAisleP) {

        // En mode ajout
        if (idAisleP== AisleDetailActivity.PARAM_AISLE_ADD){
            // T008 - Ajout d’un médicament
            // Initialise un objet Medecine vide dans le viewModel dans le UiState
            viewModel.initNewAisle()
        }
        else{
            // Charge les données
            viewModel.loadAisleByID(idAisleP)
        }
    }

    AisleDetailStateComposable(
        modifier=modifier,
        uiStateAisleDetailP = uiStateAisleDetail,
        bAddModeP = viewModel.bAddMode(),
        loadAisleByIDP = { viewModel.loadAisleByID(idAisleP) },
        onInputNameChangedP = viewModel::onInputNameChanged,
        insertAisleP = viewModel::addAisle,
        onAisleInsertedP = onAisleInsertedP
    )


}

@Composable
fun AisleDetailStateComposable(
    modifier: Modifier = Modifier,
    uiStateAisleDetailP: AisleDetailUIState,
    loadAisleByIDP: () -> Unit,
    bAddModeP: Boolean,
    onInputNameChangedP : (String) -> Unit,
    insertAisleP : () -> Unit,
    onAisleInsertedP : () -> Unit,
) {

    // Une actionBar avec le nom de l'appli ici car l'activity à un thème avec ActionBar

    Scaffold { contentPadding ->
        when (uiStateAisleDetailP.currentStateAisle) {

            // Chargement
            is CurrentAisleUIState.IsLoading -> {
                LoadingComposable(modifier.padding(contentPadding))
            }

            // Récupération des données avec succès
            is CurrentAisleUIState.LoadSuccess -> {

                AisleDetailSuccessComposable(
                    modifier=modifier.padding(contentPadding),
                    aisleP = uiStateAisleDetailP.currentStateAisle.aisle,
                    formErrorP = uiStateAisleDetailP.formError,
                    bAddModeP = bAddModeP,
                    onInputNameChangedP = onInputNameChangedP,
                    insertAisleP = insertAisleP
                )

            }

            is CurrentAisleUIState.ValidateSuccess -> {
                onAisleInsertedP() // Retour à la liste avec notification de la modification
            }

            else -> {
                // Cas d'erreur

                val onRetry : () -> Unit
                val sError : String

                when (uiStateAisleDetailP.currentStateAisle) {
                    is CurrentAisleUIState.LoadError -> {
                        sError = uiStateAisleDetailP.currentStateAisle.sError?:stringResource(R.string.unknown_error)
                        onRetry = loadAisleByIDP
                    }
                    is CurrentAisleUIState.ValidateError -> {
                        sError = uiStateAisleDetailP.currentStateAisle.sError
                        onRetry = insertAisleP
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
fun AisleDetailSuccessComposable(
    modifier: Modifier,
    aisleP: Aisle,
    bAddModeP: Boolean,
    formErrorP : FormErrorAddAisle?,
    onInputNameChangedP : (String) -> Unit,
    insertAisleP : () -> Unit,
) {

    Column(
        modifier = modifier.padding(
            8.dp
        )
    ){


        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TestTags.AISLE_DETAIL_TEXT_FIELD_NAME),
            value = aisleP.name,
            isError = (
                    formErrorP is FormErrorAddAisle.NameErrorEmpty
                    ||
                    formErrorP is FormErrorAddAisle.NameErrorAlreadyExist
                    ),
            onValueChange = {
                onInputNameChangedP(it)
            },
            label = { Text(stringResource(R.string.name)) },
            enabled = bAddModeP,

        )
        if (formErrorP is FormErrorAddAisle.NameErrorEmpty) {
            Text(
                text = stringResource(R.string.mandatoryname),
                color = MaterialTheme.colorScheme.error,
            )
        }
        if (formErrorP is FormErrorAddAisle.NameErrorAlreadyExist) {
            Text(
                text = stringResource(R.string.this_aisle_already_exists),
                color = MaterialTheme.colorScheme.error,
            )
        }


        if (bAddModeP){

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Text(text = stringResource(id = R.string.validate))
                },
                onClick = {
                    insertAisleP()
                }
            )

        }


    }




}

@Preview("Aisle Detail Mode Success")
@Composable
fun AisleDetailStateComposableSuccessPreview() {


    val listFakeAlsse = StockFakeAPI.initFakeAisles()
    val uiStateSuccess = AisleDetailUIState(
        currentStateAisle = CurrentAisleUIState.LoadSuccess(listFakeAlsse[0])
    )

    RebonnteTheme {

        AisleDetailStateComposable(
            uiStateAisleDetailP = uiStateSuccess,
            loadAisleByIDP = {},
            bAddModeP = false,
            onInputNameChangedP = {},
            insertAisleP = {},
            onAisleInsertedP = {}
        )

    }


}

@Preview("Aisle Add Mode")
@Composable
fun AisleDetailStateComposableAddModePreview() {


    val listFakeAlsse = StockFakeAPI.initFakeAisles()
    val uiStateSuccess = AisleDetailUIState(
        currentStateAisle = CurrentAisleUIState.LoadSuccess(listFakeAlsse[0])
    )

    RebonnteTheme {

        AisleDetailStateComposable(
            uiStateAisleDetailP = uiStateSuccess,
            loadAisleByIDP = {},
            bAddModeP = true,
            onInputNameChangedP = {},
            insertAisleP = {},
            onAisleInsertedP = {}
        )

    }


}


@Preview("Aisle Item Loading")
@Composable
fun AisleDetailStateComposableLoadingPreview() {

    val uiStateLoading = AisleDetailUIState(
        currentStateAisle = CurrentAisleUIState.IsLoading
    )

    RebonnteTheme {

        AisleDetailStateComposable(
            uiStateAisleDetailP = uiStateLoading,
            loadAisleByIDP = {},
            bAddModeP = false,
            onInputNameChangedP = {},
            insertAisleP = {},
            onAisleInsertedP = {}
        )

    }
}


@Preview("Aisle Item Error")
@Composable
fun AisleDetailStateComposableErrorPreview() {

    val uiStateError = AisleDetailUIState(
        currentStateAisle = CurrentAisleUIState.LoadError("Message de test pour la preview")
    )

    RebonnteTheme {

        AisleDetailStateComposable(
            uiStateAisleDetailP = uiStateError,
            loadAisleByIDP = {},
            bAddModeP = false,
            onInputNameChangedP = {},
            insertAisleP = {},
            onAisleInsertedP = {}
        )
    }
}
