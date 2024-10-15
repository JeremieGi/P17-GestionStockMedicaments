package com.openclassrooms.rebonnte.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme


/**
 * T004b - Indiquer les erreurs
 * Composant d'erreur : Affiche un message d'erreur et un bouton "Try Again"
 */
@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    sErrorMessage : String,
    onClickRetryP: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_error_36),
            contentDescription = null // Libellé Error ci-dessous sufisant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = sErrorMessage,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClickRetryP) {
            Text(stringResource(R.string.try_again))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorDialogPreview() {

    RebonnteTheme {
        ErrorComposable(
            sErrorMessage = "message d'erreur",
            onClickRetryP = { }
        )
    }

}