package com.openclassrooms.rebonnte.ui

sealed class Screen(
    val route: String
) {

    data object Launch : Screen("launchScreen")

    data object MedicinesList : Screen(CTE_MEDICINE_LIST_SCREEN)


    data object MedicineDetail : Screen("medecineDetail/{$CTE_PARAM_ID_MEDECINE}"){
        // Configurer la Route avec des Arguments
        fun createRoute(eventId: String) = "medecineDetail/$eventId"
    }

    data object MedicineAdd : Screen("medecineAdd")

    data object AisleList : Screen(CTE_AISLE_LIST_SCREEN)

    data object AisleDetail : Screen("aisleDetail/{$CTE_PARAM_ID_AISLE}"){
        // Configurer la Route avec des Arguments
        fun createRoute(eventId: String) = "aisleDetail/$eventId"
    }

    companion object {

        const val CTE_PARAM_ID_MEDECINE: String = "medicineID"
        const val CTE_PARAM_ID_AISLE: String = "aisleID"

        const val CTE_MEDICINE_LIST_SCREEN: String = "MedicinesList"
        const val CTE_AISLE_LIST_SCREEN: String = "AisleList"

    }
}