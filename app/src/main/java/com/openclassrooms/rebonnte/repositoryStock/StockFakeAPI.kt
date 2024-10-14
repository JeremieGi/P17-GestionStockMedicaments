package com.openclassrooms.rebonnte.repositoryStock

import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repositoryStock.StockRepository.EnumSortedItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Calendar
import java.util.Date

class StockFakeAPI : StockAPI {

    private val _listMedicines : MutableList<Medicine> = initFakeMedicines()
    private val _listAisle : MutableList<Aisle> = initFakeAisles()

    companion object {

        // J'utilise cette procédure statique pour les previews Compose et les tests
        fun initFakeMedicines() : MutableList<Medicine> {

            val aisles = initFakeAisles()

            val dates = initDates(3)


            return mutableListOf(

                Medicine(
                    id = "1",
                    name = "Medecine 1",
                    stock = 1,
                    oAisle = aisles[0],
                    histories = listOf(
                        History("User1",dates[0],"Details 1"),
                        History("User1",dates[1],"Details 2")
                    )
                ),

                Medicine(
                    id = "2",
                    name = "Medecine 2",
                    stock = 2,
                    oAisle = aisles[1],
                    histories = listOf(
                        History("User2",dates[0],"Details 1"),
                        History("User2",dates[1],"Details 2")
                    )
                ),

                Medicine(
                    id = "3",
                    name = "Medecine 3",
                    stock = 3,
                    oAisle = aisles[2],
                    histories = listOf(
                        History("User1",dates[1],"Details 1"),
                        History("User3",dates[2],"Details 2")
                    )
                )
            )
        }

        // Génère des dates
        private fun initDates(nNbDatesP : Int): List<Date> {

            val resultDates = MutableList<Date>(nNbDatesP) { Date() }

            // Créer une instance de Calendar
            val calendar = Calendar.getInstance()

            // Date fixe de départ
            calendar.set(2024, Calendar.JUNE, 1)

            for (i in 0 until nNbDatesP) {

                // Obtenir la date actuelle
                resultDates[i] = calendar.time

                // Enlève un mois
                calendar.add(Calendar.MONTH, -1)

            }

            return resultDates

        }

        fun initFakeAisles() : MutableList<Aisle> {
            return mutableListOf(
                Aisle("1","A1"),
                Aisle("2","A2"),
                Aisle("3","A3"),
            )
        }

    }

    override fun loadAllMedecines(sFilterNameP : String, eSortItemP : EnumSortedItem): Flow<ResultCustom<List<Medicine>>> {

        return callbackFlow {

            trySend(ResultCustom.Loading)

            var list : List<Medicine>  = _listMedicines

            if (sFilterNameP.isNotEmpty()){
                list = list.filter { it.name.contains(sFilterNameP) }
            }

            when (eSortItemP) {
                EnumSortedItem.NONE -> {}

                EnumSortedItem.NAME -> {
                    list = list.sortedBy { it.name }
                }

                EnumSortedItem.STOCK -> {
                    list = list.sortedBy { it.stock }
                }
            }


            trySend(ResultCustom.Success(list))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun addMedicine(medicine: Medicine): Flow<ResultCustom<Medicine>> {

        val isAdded = _listMedicines.add(medicine)

        return callbackFlow {

            trySend(ResultCustom.Loading)
            //delay(1*1000)

            if (isAdded){
                trySend(ResultCustom.Success(medicine))
            }
            else{
                trySend(ResultCustom.Failure("Impossible to add medicine"))
            }

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>> {

        val medicine = _listMedicines.find { it.id == idMedicine }

        return callbackFlow {

            trySend(ResultCustom.Loading)
            //delay(1*1000)

            if (medicine == null){
                trySend(ResultCustom.Failure("No medicine find with ID = $idMedicine"))
            }
            else{
                trySend(ResultCustom.Success(medicine))
            }

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun deleteMedicineByID(idMedicine: String): Flow<ResultCustom<String>> {

        // Vrai si un élément a été supprimé
        val bSup = _listMedicines.removeIf { it.id == idMedicine }

        return callbackFlow {

            trySend(ResultCustom.Loading)
            //delay(1*1000)

            if (bSup){
                trySend(ResultCustom.Success(""))
            }
            else{
                trySend(ResultCustom.Failure("No medicine find with ID = $idMedicine"))
            }

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun loadAllAisles(): Flow<ResultCustom<List<Aisle>>> {

        return callbackFlow {

            trySend(ResultCustom.Loading)

            val list : List<Aisle>  = _listAisle

            trySend(ResultCustom.Success(list))

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }

    override fun loadAisleByID(idAisle: String): Flow<ResultCustom<Aisle>> {

        val aisles = _listAisle.find { it.id == idAisle }

        return callbackFlow {

            trySend(ResultCustom.Loading)
            //delay(1*1000)

            if (aisles == null){
                trySend(ResultCustom.Failure("No aisle find with ID = $idAisle"))
            }
            else{
                trySend(ResultCustom.Success(aisles))
            }

            // awaitClose : Permet de fermer le listener dès que le flow n'est plus écouté (pour éviter les fuites mémoire)
            awaitClose {

            }
        }

    }


}