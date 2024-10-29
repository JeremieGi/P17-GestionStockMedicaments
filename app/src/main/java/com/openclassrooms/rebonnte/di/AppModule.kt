package com.openclassrooms.rebonnte.di

import android.content.Context
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.repository.stock.StockAPI
import com.openclassrooms.rebonnte.repository.stock.StockFirebaseAPI
import com.openclassrooms.rebonnte.repository.user.UserAPI
import com.openclassrooms.rebonnte.repository.user.UserFirebaseAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideStockAPI(): StockAPI {
        return StockFirebaseAPI()

        // TODO JG : Le mail du user est bien enregistré en base mais n'apparait pas dans l'interface
        //return StockFakeAPI()
    }

    @Provides
    @Singleton
    fun provideUserAPI(): UserAPI {
        return UserFirebaseAPI()
        //return UserFakeAPI()
    }


    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InjectedContext {
        return InjectedContext(context) // Contexte injecté dans le repository
    }

}