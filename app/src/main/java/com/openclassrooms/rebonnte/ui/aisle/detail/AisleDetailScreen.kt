package com.openclassrooms.rebonnte.ui.aisle.detail


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.ui.ErrorComposable
import com.openclassrooms.rebonnte.ui.LoadingComposable


@Composable
fun AisleDetailScreen(
    modifier: Modifier = Modifier,
    idAisleP : String,
    viewModel: AisleDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {

    // Lecture du post
    val uiStateAisleDetail by viewModel.uiStateAisleDetail.collectAsState()

    LaunchedEffect(idAisleP) {
        viewModel.loadAisleByID(idAisleP)
    }

    AisleDetailStateComposable(
        modifier=modifier,
        uiStateAisleDetailP = uiStateAisleDetail,
        loadAisleByIDP = { viewModel.loadAisleByID(idAisleP) },
        onBackClick = onBackClick
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailStateComposable(
    modifier: Modifier,
    uiStateAisleDetailP: AisleDetailUIState,
    onBackClick: () -> Unit,
    loadAisleByIDP: () -> Unit) {

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    if (uiStateAisleDetailP is AisleDetailUIState.Success){
                        Text(uiStateAisleDetailP.aisle.name)
                    }
                    else{
                        Text(stringResource(id = R.string.aisle))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                        )
                    }
                }
            )
        }
    ) { contentPadding ->

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
        modifier = modifier
    ){
        Text(text = aisleP.name)
    }

}

