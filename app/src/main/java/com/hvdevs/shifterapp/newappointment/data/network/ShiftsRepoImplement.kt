package com.hvdevs.shifterapp.newappointment.data.network

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.hvdevs.shifterapp.Resource
import com.hvdevs.shifterapp.newappointment.Shifts
import kotlinx.coroutines.tasks.await

class ShiftsRepoImplement: ShiftsRepoInterface {
    var v = ""
    private var shiftsList: ArrayList<Shifts> = arrayListOf()
    override suspend fun getShiftsRepo(): Resource<ArrayList<Shifts>> {
        val db: DataSnapshot? = FirebaseDatabase.getInstance().getReference("profession/Depilacion definitiva/professional/takenShift/$v").get().await()
        if (db!!.exists()){
            for (sn in db.children){
                val time = sn.getValue(Shifts::class.java)
                shiftsList.add(time!!)
            }
        }
        return Resource.Success(shiftsList)
    }

}