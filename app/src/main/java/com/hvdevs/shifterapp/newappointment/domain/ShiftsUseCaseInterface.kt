package com.hvdevs.shifterapp.newappointment.domain

import com.hvdevs.shifterapp.Resource
import com.hvdevs.shifterapp.newappointment.Shifts

interface ShiftsUseCaseInterface {
    suspend fun getShifts(): Resource<ArrayList<Shifts>>
}