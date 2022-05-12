package com.hvdevs.shifterapp.newappointment.data.network

import com.hvdevs.shifterapp.Resource
import com.hvdevs.shifterapp.newappointment.Shifts

interface ShiftsRepoInterface {
    suspend fun getShiftsRepo(): Resource<ArrayList<Shifts>>
}