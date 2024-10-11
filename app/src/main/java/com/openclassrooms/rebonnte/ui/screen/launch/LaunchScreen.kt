package com.openclassrooms.rebonnte.ui.screen.launch

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.ui.medecine.list.MedicineListScreen


@Composable
fun LaunchScreen(
    //modifier: Modifier = Modifier,
    viewModel: LaunchViewModel = hiltViewModel(),
    onMedicineClickP: (Medicine) -> Unit = {},
    onClickAddP: () -> Unit,
    onClickAisleP : () -> Unit
) {

    val context = LocalContext.current

//    // Authentification réussie ?
//    var isAuthenticated by remember { mutableStateOf(false) }
//
//    var cancelledByUser by remember { mutableStateOf(false) }
//
//    val signInLauncher = rememberLauncherForActivityResult(
//        contract = FirebaseAuthUIActivityResultContract()
//    ) { result ->
//        // Callback avec le résultat de la connexion
//
//        val response = result.idpResponse
//        if (result.resultCode == RESULT_OK) {
//
//            // Successfully signed in
//            Toast
//                .makeText(context, context.getString(R.string.connexion_ok), Toast.LENGTH_SHORT)
//                .show()
//
//            // Insertion de l'utilisateur dans le repo
//            viewModel.insertCurrentUser()
//
//            isAuthenticated = true
//
//        } else {
//
//            var sError : String? = null
//
//            response?.error?.message?.let {
//                sError = it
//            }
//                ?: run {
//                    // Si errorCode est null, afficher un message d'erreur générique
//                    sError = context.getString(R.string.loginCancel)
//                }
//            Toast.makeText(context, sError, Toast.LENGTH_SHORT).show()
//
//            cancelledByUser = true
//
//        }
//
//    }




//    // Vérifier l'état de connexion de l'utilisateur
//    LaunchedEffect(Unit) {
//
//        // Utilisateur pas déjà identifié ou pas authentifié
//        if (viewModel.getCurrentUserID().isEmpty() && !isAuthenticated ){
//
//            launchAuthUI(signInLauncher)
//
//        }
//
//    }

    // Si l'utilisateur était déjà loggué ou il vient de se logguer avec succès
//    if (viewModel.getCurrentUserID().isNotEmpty() || isAuthenticated ){
        //  Utilisateur connecté
        MedicineListScreen(
            //modifier = modifier,
            onMedicineClickP = onMedicineClickP,
            onClickAddP = onClickAddP,
            onClickAisleP = onClickAisleP
        )
    }


//    else{
//
//        if (cancelledByUser){
//            Column (
//                modifier = Modifier.padding(
//                    top = 50.dp
//                )
//            ) {
//
//                ErrorComposable(
//                    sErrorMessage = context.getString(R.string.loginCancel),
//                    onClickRetryP = {
//                        launchAuthUI(signInLauncher)
//                    }
//                )
//
//
//            }
//
//        }
//
//    }



//}

//fun launchAuthUI(signInLauncher: ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>) {
//
//    // Si l’utilisateur n’est pas connecté, redirige vers l’écran de création de compte / connexion
//
//    // Pour avoir l'écran de login, il faut paramétrer dans Firebase, Authentication, Settings, User actions, => décocher Email enumerattion protection
//
//    // Ici : Authenfication mail / mot de passe + Google
//    val providers = arrayListOf(
//        AuthUI.IdpConfig.EmailBuilder().build(),
//        AuthUI.IdpConfig.GoogleBuilder().build()
//    )
//
//    // Create and launch sign-in intent
//    val signInIntent = AuthUI.getInstance()
//        .createSignInIntentBuilder()
//        .setAvailableProviders(providers)
//        .setTheme(R.style.FirebaseLoginTheme)
//        //.setAlwaysShowSignInMethodScreen(true) // Affiche la fenêtre Sign in with (même si ici on a que le provider email/password ...)
//        .setLogo(R.drawable.logo_eventorias)
//        .build()
//
//    // Lance la fenêtre Firebase
//    signInLauncher.launch(signInIntent)
//
//}
