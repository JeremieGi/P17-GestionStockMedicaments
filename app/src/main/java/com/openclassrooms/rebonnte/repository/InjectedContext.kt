package com.openclassrooms.rebonnte.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


/**
 * Classe injectée dans les repository pour savoir si l'accès au réseau est possible
 */
class InjectedContext (
    private val _context: Context // Injecté par Hilt -> Voir dans AppModule
) {

    /**
     * Renvoie Vrai si l'application a accès à Internet, faux sinon
     */
    fun isInternetAvailable(): Boolean {

        val connectivityManager = _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

//        } else {
//                API 23 alors que version minimale de l'appli = 24
//            val networkInfo = connectivityManager.activeNetworkInfo
//            return networkInfo != null && networkInfo.isConnected
//        }

    }

    /**
     * Accès au contexte (permet de récupérer les ressources Strings depuis un répository par exemple)
     */
    fun getContext() : Context {
        return _context
    }

}