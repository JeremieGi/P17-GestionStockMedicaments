package com.openclassrooms.rebonnte.ui

sealed class Screen(
    val route: String
) {

    data object Launch : Screen("launchScreen")

    data object MedicinesList : Screen(CTE_MEDICINE_LIST_SCREEN)

//    // Pas besoin de route pour cet écran => une activité dédiée sera ouverte
//    data object MedicineDetail : Screen("medecineDetail/{$CTE_PARAM_ID_MEDECINE}"){
//        // Configurer la Route avec des Arguments
//        fun createRoute(medecineId: String) = "medecineDetail/$medecineId"
//    }

    data object MedicineAdd : Screen("medecineAdd")

    data object AisleList : Screen(CTE_AISLE_LIST_SCREEN)

//    // Pas besoin de route pour cet écran => une activité dédiée sera ouverte
//    data object AisleDetail : Screen("aisleDetail/{$CTE_PARAM_ID_AISLE}"){
//        // Configurer la Route avec des Arguments
//        fun createRoute(aisleId: String) = "aisleDetail/$aisleId"
//    }

    companion object {

        const val CTE_PARAM_ID_MEDECINE: String = "medicineID"
        const val CTE_PARAM_ID_AISLE: String = "aisleID"

        const val CTE_MEDICINE_LIST_SCREEN: String = "MedicinesList"
        const val CTE_AISLE_LIST_SCREEN: String = "AisleList"

    }
}