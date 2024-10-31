package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.repository.stock.StockRepository
import com.openclassrooms.rebonnte.repository.user.UserRepository
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Before

class MedicineDetailViewModelTest {

    // TODO JG : test unitaire => MedicineDetailViewModelTest

    // Utilisation de MockK pour le mock du repository
    @MockK
    lateinit var mockStockRepository: StockRepository
    @MockK
    lateinit var mockUserRepository: UserRepository

    // ViewModel que nous allons tester
    private lateinit var cutViewModel: MedicineDetailViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined) // Utile pour définir un dispatcher en mode test
        MockKAnnotations.init(this)
        cutViewModel = MedicineDetailViewModel(mockStockRepository,mockUserRepository)
    }

}