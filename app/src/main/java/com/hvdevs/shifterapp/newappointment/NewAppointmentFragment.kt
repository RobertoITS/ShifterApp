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

    private val professionList: ArrayList<String> = arrayListOf()
    private val professionalList: ArrayList<Professional> = arrayListOf() //El modelo de los profesionales
    private val name: ArrayList<String> = arrayListOf() //Solamente los nombres de los profesionales
    //La posicion del modelo con los nombres, coordinan para la busqueda del uid
    private val takenShift: ArrayList<String> = arrayListOf()
    private val modelShift: ArrayList<String> = arrayListOf()

    var v: String = ""
    var profession = ""
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

                v = "${date?.date}-${date?.month}-${date?.year}" //Obtenemos el id del dia para comparar en la db

                binding.dragView.performClick() //Llama al click de la vista

                professionList.clear() //Limpiamos las listas
                professionalList.clear()
                modelShift.clear()
                takenShift.clear()

                getProfession()

                val selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date!!)

                Toast.makeText(context, selectedDate, Toast.LENGTH_SHORT).show()

                Log.d("TIME", date.toString())
                Log.d("TIME", date.day.toString()) //LUNES, MARTES ... DOMINGO
                Log.d("TIME", date.month.toString()) //NUMERO DEL MES, COMIENZA DESDE 0
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
            profession = professionList[i]
            getProfessional(profession)
        }

        binding.included.autoCompleteProfessional.setOnItemClickListener { adapterView, view, i, l ->
            modelShift.clear()
            takenShift.clear()
            getModelShift(professionalList[i].uid)
        }

        return binding.root
    }

    private fun getProfession(){ //Obtenemos las profesiones (estan guardadas como las "key")
        val db = FirebaseDatabase.getInstance().getReference("profession").get()
        db.addOnSuccessListener { dataProf ->
            for (prof in dataProf.children){
                val profession = prof.key
                professionList.add(profession!!)
            }
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, professionList)
            binding.included.autoCompleteProfession.setAdapter(arrayAdapter)
        }
    }

    /**Obtenemos los id's en los nodos de la profesion, y por el id,
     * lo buscamos nuevamente profesional por profesional: */
    private fun getProfessional(path: String) {
        val db = FirebaseDatabase.getInstance().getReference("profession/$path/professional").get()
        db.addOnSuccessListener { dataProfessional ->
            for (professional in dataProfessional.children) {
                val uid = professional.value.toString()
                val dbProf = FirebaseDatabase.getInstance().getReference("professional/$uid").get()
                dbProf.addOnSuccessListener { prof ->
                    val profData = prof.getValue(Professional::class.java)
                    professionalList.add(profData!!)
                    name.add(profData.name) //Pasamos a esta lista solo los nombres, luego nos sirve la posicion
                    //para comparar con la lista main
                }
            }
            val arrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, name)
            binding.included.autoCompleteProfessional.setAdapter(arrayAdapter)
        }
    }

    private fun getModelShift(uid: String) {
        val dbShift = FirebaseDatabase.getInstance().getReference("professional/$uid/modelShift")
        dbShift.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (shift in snapshot.children){
                        val s = shift.key
                        val dbChild = FirebaseDatabase.getInstance().getReference("professional/$uid/modelShift/$s")
                        dbChild.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    for (s in snapshot.children){
                                        val ss = s.value.toString()
                                        modelShift.add(ss)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                }
                getTakenShift(uid)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getTakenShift(uid: String){
        val dbShift = FirebaseDatabase.getInstance().getReference("professional/$uid/takenShift/$profession/$v")
        dbShift.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (shift in snapshot.children) {
                        val s = shift.key
                        val dbChild = FirebaseDatabase.getInstance()
                            .getReference("professional/$uid/takenShift/$profession/$v/$s")
                        dbChild.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    for (ss in snapshot.children){
                                        val sss = ss.value.toString()
                                        takenShift.add(sss)
                                        Log.d("ASD", sss)
                                        for (i in 0 until modelShift.size - 1){
                                            if (sss == modelShift[i]) modelShift.removeAt(i)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                    }
                }
                val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modelShift)
                binding.included.autoCompleteShift.setAdapter(arrayAdapter)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
//
//    private fun getTakenShift(uid: String){
//        val dbShift = FirebaseDatabase.getInstance().getReference("professional/$uid/takenShift/$profession/$v").get()
//        dbShift.addOnSuccessListener { shiftKey ->
//            for (keyShift in shiftKey.children){
//                val key = shiftKey.key
//                val dbChild = FirebaseDatabase.getInstance()
//                    .getReference("professional/$uid/takenShift/$profession/$v/$key").get()
//                dbChild.addOnSuccessListener { shift ->
//                    for (fShift in shift.children){
//                        val tShift = fShift.value.toString()
////                        takenShift.add(tShift)
//                        Log.d("ASD", tShift)
//                        for (i in 0 until modelShift.size - 1){
//                            if (tShift == modelShift[i]) modelShift.removeAt(i)
//                        }
//                    }
//                }
//            }
//            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modelShift)
//            binding.included.autoCompleteShift.setAdapter(arrayAdapter)
//        }
//    }
}