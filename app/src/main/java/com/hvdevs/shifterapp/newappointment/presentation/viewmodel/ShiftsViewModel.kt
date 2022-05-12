package com.hvdevs.shifterapp.newappointment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.hvdevs.shifterapp.Resource
import com.hvdevs.shifterapp.newappointment.domain.ShiftsUseCaseInterface
import kotlinx.coroutines.Dispatchers

class ShiftsViewModel(useCase: ShiftsUseCaseInterface): ViewModel() {
    val fetchShiftsData = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            val shifts = useCase.getShifts()
            emit(shifts)
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}