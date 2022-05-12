package com.hvdevs.shifterapp.newappointment.domain

import com.hvdevs.shifterapp.Resource
import com.hvdevs.shifterapp.newappointment.Shifts
import com.hvdevs.shifterapp.newappointment.data.network.ShiftsRepoInterface

class ShiftsUseCaseImplement(private val repo: ShiftsRepoInterface): ShiftsUseCaseInterface {
    override suspend fun getShifts(): Resource<ArrayList<Shifts>> = repo.getShiftsRepo()
}