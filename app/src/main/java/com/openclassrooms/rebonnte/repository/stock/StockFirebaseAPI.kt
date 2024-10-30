package com.openclassrooms.rebonnte.repository.stock

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.repository.ResultCustom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class StockFirebaseAPI : StockAPI {


    companion object {

        // Nom des collections en base de données
        private const val COLLECTION_MEDICINE: String = "medicines"
        private const val COLLECTION_AISLES: String = "aisles"

    }

    // Get the Collection Reference
    private fun getMedicinesCollection(): CollectionReference {
        // collection() permet de récupérer la référence d'une collection dont le chemin est renseignée en paramètre de la méthode.
        return FirebaseFirestore.getInstance().collection(COLLECTION_MEDICINE)
    }

    private fun getAislesCollection(): CollectionReference {
        // collection() permet de récupérer la référence d'une collection dont le chemin est renseignée en paramètre de la méthode.
        return FirebaseFirestore.getInstance().collection(COLLECTION_AISLES)
    }


    override fun loadAllMedicines(
        sFilterNameP: String,
        eSortItemP: StockRepository.EnumSortedItem
    ): Flow<ResultCustom<List<Medicine>>> {

        val queryEvents = requestListMedicines(eSortItemP)

        // Cette méthode crée un Flow qui est basé sur des callbacks, ce qui est idéal pour intégrer des API asynchrones comme Firestore.
        return callbackFlow {

            trySend(ResultCustom.Loading)

            queryEvents.get()
                .addOnSuccessListener { documents ->

                    val medicinesDTOList = documents.map { document ->
                        document.toObject(FirebaseMedicineDTO::class.java)
                    }

                    var medicines = medicinesDTOList.map {
                        it.toModel()
                    }

                    if (sFilterNameP.isNotEmpty()){
                        medicines = medicines.filter { it.name.contains(sFilterNameP) }
                    }

                    trySend(ResultCustom.Success(medicines)) // Émettre la liste

                }
                .addOnFailureListener { exception ->
                    trySend(ResultCustom.Failure(exception.message))
                }

            // awaitClose : Permet d'exécuter du code quand le flow n'est plus écouté
            awaitClose {
                //listenerRegistration.remove() // Ferme le listener pour éviter une fuite mémoire
            }

        }

    }

    private fun requestListMedicines(eSortItemP: StockRepository.EnumSortedItem): Query {

        var resultMedicines : Query = FirebaseFirestore.getInstance().collection(COLLECTION_MEDICINE)

       // Il n'y a pas de condition "content" en firebase (je vais donc filter lors de la lecture de la requête)

        when (eSortItemP){
            StockRepository.EnumSortedItem.NONE -> {
                // Pas de tri
                // TODO Denis => Pas de tri => ne fait rien : On peut annuler un tri ?
            }
            StockRepository.EnumSortedItem.NAME -> {
                resultMedicines = resultMedicines.orderBy(FirebaseMedicineDTO.RUB_NAME)
            }
            StockRepository.EnumSortedItem.STOCK -> {
                resultMedicines = resultMedicines.orderBy(FirebaseMedicineDTO.RUB_STOCK)
            }
        }

        return resultMedicines

    }

    override fun addMedicine(medicine: Medicine): Flow<ResultCustom<Medicine>> {
        return insertOrUpdateMedicine(medicine)
    }

    override fun updateMedicine(
        updatedMedicine: Medicine
    ): Flow<ResultCustom<Medicine>> {
        return insertOrUpdateMedicine(updatedMedicine)
    }

    // En Firebase set() insère ou met à jour
    private fun insertOrUpdateMedicine(medicine: Medicine) : Flow<ResultCustom<Medicine>> {

        return callbackFlow {

            // Utilisation de l'ID pour créer une référence de document
            val medicineDocument = getMedicinesCollection().document(medicine.id)

            // Mise à jour dans la base de données Firestore
            val medicineDTO = FirebaseMedicineDTO(medicine)
            medicineDocument.set(medicineDTO)
                .addOnSuccessListener {
                    // Succès de l'ajout dans Firestore
                    trySend(ResultCustom.Success(medicine))
                }
                .addOnFailureListener { firestoreException ->
                    // Gestion des erreurs lors de l'ajout dans Firestore
                    trySend(ResultCustom.Failure("Failed to add medicine to Firestore: ${firestoreException.message}"))
                }

                .addOnCanceledListener {
                    trySend(ResultCustom.Failure("addOnCanceledListener"))
                }

            awaitClose {

            }

        }

    }

    override fun loadMedicineByID(idMedicine: String): Flow<ResultCustom<Medicine>> {

        val queryEventByID = requestMedicineByID(idMedicine)

        return callbackFlow {

            queryEventByID.get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->

                    if (!querySnapshot.isEmpty) {
                        // Récupérer le premier document (puisque ID est unique)
                        val documentSnapshot = querySnapshot.documents[0]
                        val medicineDTO = documentSnapshot.toObject(FirebaseMedicineDTO::class.java)
                        val medicine = medicineDTO?.toModel()
                        if (medicine==null){
                            trySend(ResultCustom.Failure("Echec du toObject"))
                        }
                        else{
                            trySend(ResultCustom.Success(medicine))
                        }

                    } else {
                        trySend(ResultCustom.Failure("Aucun document trouvé"))
                    }
                }
                .addOnFailureListener { exception ->
                    trySend(ResultCustom.Failure(exception.message))
                }

            // awaitClose : Suspend la coroutine actuelle jusqu'à ce que le canal soit fermé ou annulé et appelle le bloc donné avant de reprendre la coroutine.
            awaitClose {

            }
        }

    }

    // Recherche par ID dans firebase
    private fun requestMedicineByID(idMedicine: String): Query {

        return this.getMedicinesCollection()
            .whereEqualTo(FirebaseMedicineDTO.RUB_ID, idMedicine)

    }

    // T009 - Suppression d’un médicament
    override fun deleteMedicineByID(idMedicine: String): Flow<ResultCustom<String>> {

        return callbackFlow {

            getMedicinesCollection().document(idMedicine).delete()
                .addOnSuccessListener {
                    trySend(ResultCustom.Success(""))
                }
                .addOnFailureListener { exception ->
                    trySend(ResultCustom.Failure(exception.message))
                }
            awaitClose {

            }
        }

    }

    override fun loadAllAisles(): Flow<ResultCustom<List<Aisle>>> {

        return callbackFlow {

            getAislesCollection().get()
                .addOnSuccessListener { documents ->

                    val aisles = documents.map { documentSnapshot ->
                        documentSnapshot.toObject(FirebaseAisleDTO::class.java).toModel()
                    }
                    trySend(ResultCustom.Success(aisles))

                }
                .addOnFailureListener { exception ->
                    trySend(ResultCustom.Failure(exception.message))
                }
            awaitClose {

            }
        }

    }

    override fun loadAisleByID(idAisle: String): Flow<ResultCustom<Aisle>> {

        return callbackFlow {

            getAislesCollection().document(idAisle).get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {
                        // Convertir le document en objet Aisle
                        val aisle = document.toObject(FirebaseAisleDTO::class.java)?.toModel()
                        if (aisle==null){
                            trySend(ResultCustom.Failure("Echec du toObject"))
                        }
                        else{
                            trySend(ResultCustom.Success(aisle))
                        }
                    } else {
                        trySend(ResultCustom.Failure("Aisle with ID $idAisle unknown"))
                    }


                }
                .addOnFailureListener { exception ->
                    trySend(ResultCustom.Failure(exception.message))
                }
            awaitClose {

            }
        }

    }

    override fun addAisle(aisle: Aisle): Flow<ResultCustom<String>> {

        return callbackFlow {

            // Utilisation de l'ID pour créer une référence de document
            val aisleDocument = getAislesCollection().document(aisle.id)

            // Mise à jour dans la base de données Firestore
            val aisleDTO = FirebaseAisleDTO(aisle)
            aisleDocument.set(aisleDTO)
                .addOnSuccessListener {
                    // Succès de l'ajout dans Firestore
                    trySend(ResultCustom.Success(""))
                }
                .addOnFailureListener { firestoreException ->
                    // Gestion des erreurs lors de l'ajout dans Firestore
                    trySend(ResultCustom.Failure("Failed to add aisle to Firestore: ${firestoreException.message}"))
                }

                .addOnCanceledListener {
                    trySend(ResultCustom.Failure("addOnCanceledListener"))
                }

            awaitClose {

            }

        }
    }
}