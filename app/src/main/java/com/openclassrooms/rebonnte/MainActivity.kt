package com.openclassrooms.rebonnte


import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.aisle.list.AisleListScreen
import com.openclassrooms.rebonnte.ui.medicine.list.MedicineListScreen
import com.openclassrooms.rebonnte.ui.screen.launch.LaunchScreen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //private lateinit var myBroadcastReceiver: MyBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //mainActivity = this

        // Le broadcast provoque une fuite mémoire légère compensée par le garbage collector.
        // Néanmoins, je commente ce broadcast inutile dans ce projet.
        //startBroadcastReceiver()

        setContent {
            // On appelle le NavController
            val navController = rememberNavController()

            RebonnteTheme {
                NavGraph(
                    navController = navController
                )
            }
        }

    }


/*
    private fun startBroadcastReceiver() {
        myBroadcastReceiver = MyBroadcastReceiver()
        val filter = IntentFilter().apply {
            addAction("com.rebonnte.ACTION_UPDATE")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myBroadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(myBroadcastReceiver, filter)
        }


        Handler().postDelayed({
            startMyBroadcast()
        }, 200)
    }

    private fun startMyBroadcast() {
        val intent = Intent("com.rebonnte.ACTION_UPDATE")
        sendBroadcast(intent)
        startBroadcastReceiver()
    }


    class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //Toast.makeText(mainActivity, "Update reçu", Toast.LENGTH_SHORT).show()
            Log.v("DBG","Update reçu")
        }
    }
*/
//    companion object {
//        lateinit var mainActivity: MainActivity
//    }
}

@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Launch.route   // Point d'entrée de l'application
    ) {

        // Fenêtre de lancement (Login ou directement liste des médicaments)
        composable(route = Screen.Launch.route) {

            LaunchScreen(
//                navigatebyMedicineListScreenP = {
//                   navController.navigate(Screen.MedicinesList.route)
//                },
                onClickBottomAisleP = {
                    navController.navigate(Screen.AisleList.route)
                },
                navigateLaunchScreenP = {
                    navController.navigate(Screen.Launch.route)
                }
            )

        }


        // Liste des médicaments (fenêtre d'accueil)
        composable(Screen.MedicinesList.route) {

            MedicineListScreen(
                navigateLaunchScreenP = {
                    navController.navigate(Screen.Launch.route)
                },
                onClickBottomAisleP = {
                    navController.navigate(Screen.AisleList.route){
                        // permet de ne pas ouvrir un nouvel écran (pour ne pas surcharger la pile)
                        popUpTo(navController.graph.startDestinationId)  { saveState = true }
                        launchSingleTop = true      // reselecting the same item
                        restoreState = true         // Restore state when reselecting a previously selected item
                    }
                }
            )


        }


//        // Fenêtre d'un médicament (Pas besoin de route pour cet écran => une activité dédiée sera ouverte)
//        composable(Screen.MedicineDetail.route) { backStackEntry -> // BackStackEntry ici permet de récupérer les paramètres

        composable(route = Screen.AisleList.route) {
            AisleListScreen(
                navigateLaunchScreenP = {
                    navController.navigate(Screen.Launch.route)
                },
                onClickMedicineOnBottomBarP = {
                    navController.navigate(Screen.MedicinesList.route){
                        // permet de ne pas ouvrir un nouvel écran (pour ne pas surcharger la pile)
                        popUpTo(navController.graph.startDestinationId)  { saveState = true }
                        launchSingleTop = true      // reselecting the same item
                        restoreState = true         // Restore state when reselecting a previously selected item
                    }
                }
            )
        }

    }

}

// T004c - Charger les données aux bons moments => chaque viewModel est appelé quand il est necessaire
// Vieux code refactorisé
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyApp() {
//    val navController = rememberNavController()
//    val medicineViewModel: MedicineViewModel = viewModel()
//    val aisleViewModel: AisleListViewModel = viewModel()
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val route = navBackStackEntry?.destination?.route
//
//    RebonnteTheme {
//
//    }
//}

