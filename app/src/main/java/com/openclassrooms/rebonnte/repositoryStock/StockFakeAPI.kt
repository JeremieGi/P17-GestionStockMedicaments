package com.openclassrooms.rebonnte.repositoryStock

import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.History
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.ResultCustom
import com.openclassrooms.rebonnte.repositoryStock.StockRepository.EnumSortedItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class StockFakeAPI : StockAPI {

    private val _listMedicines : MutableList<Medicine> = initFakeMedicines()
    private val _listAisle : MutableList<Aisle> = initFakeAisles()

    companion object {

        // J'utilise cette procédure statique pour les previews Compose et les tests
        fun initFakeMedicines() : MutableList<Medicine> {

            return mutableListOf(

                Medicine(name = "Medecine 1",
                    stock = 1,
                    nameAisle = "Aisle 1",
                    histories = listOf(
                        History("Medecine 1","1","Date1","Details 1"),
                        History("Medecine 1","1","Date2","Details 2")
                    )
                ),

                Medicine(name = "Medecine 2",
                    stock = 2,
                    nameAisle = "Aisle 2",
                    histories = listOf(
                        History("Medecine 2","1","Date1","Details 1"),
                        History("Medecine 2","1","Date2","Details 2")
                    )
                ),

                Medicine(name = "Medecine 3",
                    stock = 3,
                    nameAisle = "Aisle 3",
                    histories = listOf(
                        History("Medecine 3","1","Date1","Details 1"),
                        History("Medecine 3","1","Date2","Details 2")
                    )
                )
            )
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
        TODO("Not yet implemented")
    }

    override fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>> {
        TODO("Not yet implemented")
    }

    override fun deleteMedicineByID(idMedicine: String): Flow<ResultCustom<String>> {
        TODO("Not yet implemented")
    }

    override fun loadAllAisles(): Flow<ResultCustom<List<Aisle>>> {

        return callbackFlow {

            trySend(ResultCustom.Loading)

            var list : List<Aisle>  = _listAisle

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