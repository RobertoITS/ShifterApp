package com.hvdevs.shifterapp.newappointment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvdevs.shifterapp.newappointment.domain.ShiftsUseCaseInterface

class ShiftsViewModelFactory(private val useCase: ShiftsUseCaseInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ShiftsUseCaseInterface::class.java).newInstance(useCase)
    }
}