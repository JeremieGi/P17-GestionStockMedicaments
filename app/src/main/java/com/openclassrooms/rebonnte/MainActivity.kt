package com.openclassrooms.rebonnte

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.ui.Screen
import com.openclassrooms.rebonnte.ui.aisle.list.AisleListScreen
import com.openclassrooms.rebonnte.ui.medecine.list.MedicineListScreen
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.screen.launch.LaunchScreen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var myBroadcastReceiver: MyBroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = this

        setContent {
            // On appelle le NavController
            val navController = rememberNavController()

            RebonnteTheme {
                NavGraph(
                    navController = navController
                )
            }
        }

        startBroadcastReceiver()
    }

    // TODO Denis : C'est quoi ce BroadcastReceiver ?
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
            val intent = Intent("com.rebonnte.ACTION_UPDATE")
            sendBroadcast(intent)
        }, 5000)
    }


    class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(mainActivity, "Update reçu", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        lateinit var mainActivity: MainActivity
    }
}

@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Launch.route   // Point d'entrée de l'application
    ) {

        // Fenêtre de lancement (Login ou directement liste des évènènements)
        composable(route = Screen.Launch.route) {

            LaunchScreen(
//                onMedicineClickP = { medicine ->
//                    navController.navigate(Screen.MedicineItem.createRoute(medicine.id))
//                },
                onClickAddP = {
                    navController.navigate(Screen.MedicineAdd.route)
                },
                onClickBottomAisleP = {
                    navController.navigate(Screen.AisleList.route)
                }
            )

        }


        // Liste des médicaments
        composable(Screen.MedicinesList.route) {

            MedicineListScreen(

                onClickAddP = {
                    navController.navigate(Screen.MedicineAdd.route)
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


//        // Fenêtre d'un médicament (TODO : Pour l'instant lance une activity)
//        composable(Screen.MedicineDetail.route) { backStackEntry -> // BackStackEntry ici permet de récupérer les paramètres
//
//            val medecineId = backStackEntry.arguments?.getString(Screen.CTE_PARAM_ID_MEDECINE)
//                ?: error("Missing required argument medicineId") // pour lever une exception de type IllegalArgumentException avec le message spécifié.
//
//            MedicineDetailScreen(
//                idMedicineP = medecineId,
//                onBackClick = { navController.navigateUp() }
//            )
//
//        }

        composable(route = Screen.MedicineAdd.route) {
            // TODO JG : MedicineAddScreen
//            MedicineAddScreen(
//                onBackClick = { navController.navigateUp() }
//            )
        }

        composable(route = Screen.AisleList.route) {
            AisleListScreen(
                onClickAddP = {
                    navController.navigate(Screen.MedicineAdd.route)
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

//@Composable
//fun currentRoute(navController: NavController): String? {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    return navBackStackEntry?.destination?.route
//}

@Composable
fun EmbeddedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf(query) }
    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }

    val shape: Shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchActive) {
            IconButton(onClick = { activeChanged(false) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        BasicTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                onQueryChange(query)
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            }
        )

        if (isSearchActive && searchQuery.isNotEmpty()) {
            IconButton(onClick = {
                searchQuery = ""
                onQueryChange("")
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}