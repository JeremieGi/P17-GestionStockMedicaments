package com.openclassrooms.rebonnte.ui.aisle.detail

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.model.Aisle
import com.openclassrooms.rebonnte.model.Medicine
import com.openclassrooms.rebonnte.ui.medecineDetail.MedicineDetailActivity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: AisleDetailViewModel = hiltViewModel(),
    name: String,
) {

//    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
//
//    val filteredMedicines = medicines.filter { it.nameAisle == name }
//    val context = LocalContext.current
//
//    Scaffold { paddingValues ->
//        LazyColumn(
//            contentPadding = paddingValues,
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(filteredMedicines) { medicine ->
//                MedicineItem(medicine = medicine, onClick = { name ->
//                    val intent = Intent(context, MedicineDetailActivity::class.java).apply {
//                        putExtra("nameMedicine", name)
//                    }
//                    context.startActivity(intent)
//                })
//            }
//        }
//    }
}

@Composable
fun MedicineItem(medicine: Medicine, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(medicine.name) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicine.name, fontWeight = FontWeight.Bold)
            Text(text = "Stock: ${medicine.stock}", color = Color.Gray)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Arrow")
    }
}