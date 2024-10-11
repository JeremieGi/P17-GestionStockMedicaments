package com.openclassrooms.rebonnte.repositoryStock

/**
 * Classe permettant de gérer le chargement, l'erreur et le succès de chargement d'un objet générique T
 */
sealed class ResultCustom<out T> {

    data object Loading : ResultCustom<Nothing>()

    // C'est une classe de données qui représente l'état où l'opération a échoué. Elle peut contenir un message décrivant l'erreur survenue.
    data class Failure(
        val errorMessage: String? = null,
    ) : ResultCustom<Nothing>()

    // C'est une classe de données générique qui stocke le résultat de l'opération en cas de succès.
    // Elle prend un type générique R pour permettre de représenter différents types de résultats.
    data class Success<out R>(
        val value: R // Permet de récupérer les valeurs
    ) : ResultCustom<R>()

}