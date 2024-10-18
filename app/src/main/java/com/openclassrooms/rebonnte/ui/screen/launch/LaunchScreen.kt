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
import com.openclassrooms.rebonnte.ui.ErrorComposable


@Composable
fun LaunchScreen(
    viewModel: LaunchViewModel = hiltViewModel(),
    navigatebyMedicineListScreenP : () -> Unit
) {

    val context = LocalContext.current

    // Authentification réussie ?
    var isAuthenticated by remember { mutableStateOf(false) }

    var cancelledByUser by remember { mutableStateOf(false) }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { result ->
        // Callback avec le résultat de la connexion

        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {

            // Successfully signed in
            Toast
                .makeText(context, context.getString(R.string.connexion_ok), Toast.LENGTH_SHORT)
                .show()

            isAuthenticated = true

        } else {

            var sError : String? = null

            response?.error?.message?.let {
                sError = it
            }
                ?: run {
                    // Si errorCode est null, afficher un message d'erreur générique
                    sError = context.getString(R.string.loginCancel)
                }
            Toast.makeText(context, sError, Toast.LENGTH_SHORT).show()

            cancelledByUser = true

        }

    }




    // Vérifier l'état de connexion de l'utilisateur
    LaunchedEffect(Unit) {

        // Utilisateur non identifié ou authentification annulée
        if (viewModel.getCurrentUser()==null && !isAuthenticated ){

            launchAuthUI(signInLauncher)

        }

    }

    // Si l'utilisateur était déjà loggué ou il vient de se logguer avec succès
    if (viewModel.userLogged() || isAuthenticated ){
//        //  Utilisateur connecté
//        MedicineListScreen(
//            //modifier = modifier,
//            onClickBottomAisleP = onClickBottomAisleP,
//            onBackClickP = onBackClickP
//        )
        navigatebyMedicineListScreenP()
    }


    else{

        if (cancelledByUser){
            Column (
                modifier = Modifier.padding(
                    top = 50.dp
                )
            ) {

                ErrorComposable(
                    sErrorMessage = context.getString(R.string.loginCancel),
                    onClickRetryP = {
                        launchAuthUI(signInLauncher)
                    }
                )


            }

        }

    }



}

fun launchAuthUI(signInLauncher: ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>) {

    // Si l’utilisateur n’est pas connecté, redirige vers l’écran de création de compte / connexion

    //  Note importante :
    // Par sécurité, par défaut, Firebase ne permet pas la reconnexion avec un login existant (pour ne pas exposer les mails de ces utilisateurs)
    // Si on veut avoir ce comportement il faut aller dans la console Firebase -> Authentication -> Settings -> User Actions -> décocher "Email enueration protection

    // Pour avoir l'écran de login, il faut paramétrer dans Firebase, Authentication, Settings, User actions, => décocher Email enumeration protection

    // Authenfication mail / mot de passe
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build()
    )

    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setTheme(R.style.FirebaseLoginTheme)
        .setAlwaysShowSignInMethodScreen(true) // Affiche la fenêtre Sign in with (même si ici on a que le provider email/password ...)
        .setLogo(R.drawable.baseline_medical_services_300)
        .build()

    // Lance la fenêtre Firebase
    signInLauncher.launch(signInIntent)

}
