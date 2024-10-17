package com.openclassrooms.rebonnte.ui.aisle.detail


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


@Composable
fun AisleDetailScreen(
    modifier: Modifier = Modifier,
    idAisleP : String,
    viewModel: AisleDetailViewModel = hiltViewModel()
) {

    // Lecture du post
    val uiStateAisleDetail by viewModel.uiStateAisleDetail.collectAsState()

    LaunchedEffect(idAisleP) {
        viewModel.loadAisleByID(idAisleP)
    }

    AisleDetailStateComposable(
        modifier=modifier,
        uiStateAisleDetailP = uiStateAisleDetail,
        loadAisleByIDP = { viewModel.loadAisleByID(idAisleP) }
    )


}

@Composable
fun AisleDetailStateComposable(
    modifier: Modifier = Modifier,
    uiStateAisleDetailP: AisleDetailUIState,
    loadAisleByIDP: () -> Unit) {

    // Une actionBar avec le nom de l'appli ici car l'activity à un thème avec ActionBar

    Scaffold { contentPadding ->
        when (uiStateAisleDetailP) {

            // Chargement
            is AisleDetailUIState.IsLoading -> {
                LoadingComposable(modifier.padding(contentPadding))
            }

            // Récupération des données avec succès
            is AisleDetailUIState.Success -> {

                AisleDetailSuccessComposable(
                    modifier=modifier.padding(contentPadding),
                    aisleP = uiStateAisleDetailP.aisle
                )

            }

            // Exception
            is AisleDetailUIState.Error -> {

                val error = uiStateAisleDetailP.sError ?: stringResource(
                    R.string.unknown_error
                )

                ErrorComposable(
                    modifier=modifier
                        .padding(contentPadding)
                    ,
                    sErrorMessage = error,
                    onClickRetryP = loadAisleByIDP
                )


            }
        }


    }

}

@Composable
fun AisleDetailSuccessComposable(
    modifier: Modifier,
    aisleP: Aisle
) {

    Column(
        modifier = modifier.padding(
            8.dp
        )
    ){
        Text(text = aisleP.name)
    }

}


@Preview("Aisle Item Loading")
@Composable
fun AisleDetailStateComposableLoadingPreview() {

    val uiStateLoading = AisleDetailUIState.IsLoading

    RebonnteTheme {

        AisleDetailStateComposable(
            uiStateAisleDetailP = uiStateLoading,
            loadAisleByIDP = {}
        )

    }
}


@Preview("Aisle Item Success")
@Composable
fun AisleDetailStateComposableSuccessPreview() {


    val listFakeAlsse = StockFakeAPI.initFakeAisles()
    val uiStateSuccess = AisleDetailUIState.Success(listFakeAlsse[0])

    RebonnteTheme {

        AisleDetailStateComposable(
            uiStateAisleDetailP = uiStateSuccess,
            loadAisleByIDP = {}
        )

    }


}


@Preview("Aisle Item Error")
@Composable
fun AisleDetailStateComposableErrorPreview() {

    val uiStateError = AisleDetailUIState.Error("Message de test pour la preview")

    RebonnteTheme {

        AisleDetailStateComposable(
            uiStateAisleDetailP = uiStateError,
            loadAisleByIDP = {}
        )
    }
}
