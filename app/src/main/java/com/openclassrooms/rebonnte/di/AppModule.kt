package com.openclassrooms.rebonnte.di

import android.content.Context
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.repository.stock.StockAPI
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.repository.user.UserAPI
import com.openclassrooms.rebonnte.repository.user.UserFakeAPI
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
        //return StockFirestoreAPI()
        return StockFakeAPI()
    }

    @Provides
    @Singleton
    fun provideUserAPI(): UserAPI {
        //return UserFirestoreAPI()
        return UserFakeAPI()
    }


    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InjectedContext {
        return InjectedContext(context) // Contexte injecté dans le repository
    }

}