package com.openclassrooms.rebonnte.model

import android.content.Context
import com.openclassrooms.rebonnte.R

data class Medicine(
    val id : String,
    val name: String,
    val stock: Int,
    val oAisle: Aisle,
    val histories: MutableList<History>
) {

    fun addHistory(newHistory: History) {
        this.histories.add(0, newHistory)
    }

    // Renvoie le détail de la différence entre les 2 objets
    fun sDiff(updatedMedicine: Medicine, context : Context): String {

        var sDiffResult = ""

        if (this.name!=updatedMedicine.name){
            sDiffResult += context.getString(R.string.new_name, updatedMedicine.name)
        }

        if (this.stock!=updatedMedicine.stock){
            sDiffResult += context.getString(R.string.new_stock, updatedMedicine.stock.toString())
        }

        if (this.oAisle.id!=updatedMedicine.oAisle.id){
            sDiffResult += context.getString(R.string.new_aisle, updatedMedicine.oAisle.name)
        }

        return sDiffResult
    }
}
