package com.openclassrooms.rebonnte


import android.content.Context
import com.openclassrooms.rebonnte.di.AppModule
import com.openclassrooms.rebonnte.repository.InjectedContext
import com.openclassrooms.rebonnte.repository.stock.StockAPI
import com.openclassrooms.rebonnte.repository.stock.StockFakeAPI
import com.openclassrooms.rebonnte.repository.user.UserAPI
import com.openclassrooms.rebonnte.repository.user.UserFakeAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]   // Remplace l'injection de dépendance de production
)
class DependencyInjectionTest {

    // Branchement des Fake API pour les tests

    @Provides
    @Singleton
    fun provideStockAPI(): StockAPI {
        return StockFakeAPI()
    }

    @Provides
    @Singleton
    fun provideUserAPI(): UserAPI {
        return UserFakeAPI()
    }

    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context): InjectedContext {
        return InjectedContext(context) // Contexte injecté dans le repository
    }
}