package com.openclassrooms.rebonnte.model

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
    fun sDiff(updatedMedicine: Medicine): String {

        var sDiffResult = ""

        if (this.name!=updatedMedicine.name){
            sDiffResult += "New name : ${updatedMedicine.name}"
        }

        if (this.stock!=updatedMedicine.stock){
            sDiffResult += "New stock : ${updatedMedicine.stock}"
        }

        if (this.oAisle.id!=updatedMedicine.oAisle.id){
            sDiffResult += "New aisle : ${updatedMedicine.oAisle.name}"
        }

        return sDiffResult
    }
}
