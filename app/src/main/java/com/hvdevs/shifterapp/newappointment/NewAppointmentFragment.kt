package com.hvdevs.shifterapp.newappointment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.hvdevs.shifterapp.Resource
import com.hvdevs.shifterapp.databinding.FragmentNewAppointmentBinding

import com.hvdevs.shifterapp.newappointment.data.network.ShiftsRepoImplement
import com.hvdevs.shifterapp.newappointment.domain.ShiftsUseCaseImplement
import com.hvdevs.shifterapp.newappointment.presentation.viewmodel.ShiftsViewModel
import com.hvdevs.shifterapp.newappointment.presentation.viewmodel.ShiftsViewModelFactory
import com.squareup.timessquare.CalendarPickerView
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class NewAppointmentFragment : Fragment() {
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseReference

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ShiftsViewModelFactory(ShiftsUseCaseImplement(ShiftsRepoImplement()))
        )[ShiftsViewModel::class.java]
    }

    var v: String = ""
    var list: ArrayList<Shifts> = arrayListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAppointmentBinding.inflate(inflater, container, false)

//        binding.listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
//        arrayListOf("Copy", "Paste", "Cut", "Delete", "Convert", "Open"))

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayListOf("Item 1", "Item2"))
        binding.included.autoComplete.setAdapter(arrayAdapter)

        val diaActual = Date()
        val anoActual = Calendar.getInstance()
        anoActual.add(Calendar.MONTH, 2)
        binding.calendar.init(diaActual, anoActual.time).inMode(CalendarPickerView.SelectionMode.SINGLE).withSelectedDate(diaActual)
        Log.d("DIAS", "${diaActual.time}, $anoActual")

        getData()
        binding.calendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener{
            override fun onDateSelected(date: Date?) {
                binding.dragView.performClick() //Llama al click de la vista
                val selectedDate = DateFormat
                    .getDateInstance(
                        DateFormat.FULL
                    )
                    .format(date!!)
                Toast
                    .makeText(
                        context,
                        selectedDate,
                        Toast.LENGTH_SHORT
                    )
                    .show()
                Log.d("TIME", date.toString())
                Log.d("TIME", date.day.toString()) //LUNES, MARTES ... DOMINGO
                Log.d("TIME", date.month.toString()) //NUMERO DEL MES
                Log.d("TIME", date.date.toString()) //NUMERO DEL DIA
                Log.d("TIME", date.year.toString()) //NUMERO DEL AÑO CON UN 1 DELANTE: 122

                /**
                 * La idea seria obtener los datos de la DB y comparar
                 * si el turno aparece en la parte de takenShift, no deberia estar disponible
                 * **/
                v = "${date.year}-${date.month}-${date.date}"
                ShiftsRepoImplement().v = v
                getData()
            }

            override fun onDateUnselected(date: Date?) {

            }

        })

        return binding.root
    }
    fun getData(){
        viewModel.fetchShiftsData.observe(viewLifecycleOwner){ result->
            when (result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    list = result.data
                    Log.d("FIREBASE", list.toString())
                    if ("11:00 - 12:30 am" == list[0].time){
                        Toast.makeText(context, "Funciono!", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Failure -> {}
            }

        }
    }
}