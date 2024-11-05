package com.openclassrooms.rebonnte.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


/**
 * T004a - Indiquer les chargements
 * Composable qui s'affiche lors du chargement
 */
@Composable
fun LoadingComposable(modifier: Modifier = Modifier){

    val currentContext = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            // "En chargement" soit annoncé par talkBack
            .semantics(mergeDescendants = true) {}
            .clearAndSetSemantics {
                contentDescription =
                    currentContext.getString(R.string.loading)
            }
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.loading),
            modifier = modifier,
            color = MaterialTheme.colorScheme.onSurface
        )

        CircularProgressIndicator(
            modifier = modifier.padding(
                top = 10.dp // Espace avec le texte
            )
        )

    }

}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    RebonnteTheme {
        LoadingComposable()
    }
}
