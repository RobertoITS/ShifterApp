package com.hvdevs.shifterapp.newappointment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.hvdevs.shifterapp.databinding.FragmentNewAppointmentBinding
import com.squareup.timessquare.CalendarPickerView
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewAppointmentFragment : Fragment() {
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseReference

    private val professionList: ArrayList<String> = arrayListOf()
    private val professionalList: ArrayList<Professional> = arrayListOf() //El modelo de los profesionales
    private val name: ArrayList<String> = arrayListOf() //Solamente los nombres de los profesionales
    //La posicion del modelo con los nombres, coordinan para la busqueda del uid
    private val takenShift: ArrayList<String> = arrayListOf()
    private val newShift: ArrayList<String> = arrayListOf()

    var v: String = ""
    var list: ArrayList<Shifts> = arrayListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentNewAppointmentBinding.inflate(inflater, container, false)

        val diaActual = Date()
        val anoActual = Calendar.getInstance()
        anoActual.add(Calendar.MONTH, 2)
        binding.calendar.init(diaActual, anoActual.time).inMode(CalendarPickerView.SelectionMode.SINGLE).withSelectedDate(diaActual)
        Log.d("DIAS", "${diaActual.time}, $anoActual")

        binding.calendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener{
            override fun onDateSelected(date: Date?) {
                binding.dragView.performClick() //Llama al click de la vista

                professionList.clear()

                getProfession()

                val selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date!!)

                Toast.makeText(context, selectedDate, Toast.LENGTH_SHORT).show()

                Log.d("TIME", date.toString())
                Log.d("TIME", date.day.toString()) //LUNES, MARTES ... DOMINGO
                Log.d("TIME", date.month.toString()) //NUMERO DEL MES
                Log.d("TIME", date.date.toString()) //NUMERO DEL DIA
                Log.d("TIME", date.year.toString()) //NUMERO DEL AÑO CON UN 1 DELANTE: 122

                /**
                 * La idea seria obtener los datos de la DB y comparar
                 * si el turno aparece en la parte de takenShift, no deberia estar disponible
                 * **/
                v = "${date.date}-${date.month}-${date.year}"
                Log.d("TIME", v)
            }

            override fun onDateUnselected(date: Date?) {

            }

        })

        binding.included.autoCompleteProfession.setOnItemClickListener { adapterView, view, i, l ->
            professionalList.clear()
            getProfessional(professionList[i])
        }

        binding.included.autoCompleteProfessional.setOnItemClickListener { adapterView, view, i, l ->
            takenShift.clear()
            newShift.clear()
            getShift(professionalList[i].uid)
        }

        return binding.root
    }

    private fun getProfession(){
        db = FirebaseDatabase.getInstance().getReference("profession")
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (profession in snapshot.children){
                        val prof = profession.key
                        professionList.add(prof!!)
                    }
                }
                Log.d("FIREBASE", professionList.toString())
                val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, professionList)
                binding.included.autoCompleteProfession.setAdapter(arrayAdapter)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    /**Obtenemos los id's en los nodos de la profesion, y por el id, lo buscamos nuevamente profesional por profesional: */
    private fun getProfessional(path: String) {
        db = FirebaseDatabase.getInstance().getReference("profession/$path/professional")
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (professional in snapshot.children){
                        val uid = professional.value.toString()
                        val dbProf = FirebaseDatabase.getInstance().getReference("professional/$uid")
                        dbProf.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    val prof = snapshot.getValue(Professional::class.java)
                                    professionalList.add(prof!!)
                                    name.add(prof.name) //Pasamos a esta lista solo los nombres, luego nos sirve la posicion
                                    //para comparar con la lista main
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                }
                Log.d("FIREBASE", professionList.toString())
                val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, name)
                binding.included.autoCompleteProfessional.setAdapter(arrayAdapter)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getShift(uid: String) {
        TODO("Not yet implemented")
    }

    private fun getDate(/*Aca pondriamos la uid del profesional que se selecciona antes*/){

    }
}