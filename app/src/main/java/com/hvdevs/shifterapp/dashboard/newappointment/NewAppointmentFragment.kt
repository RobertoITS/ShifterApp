package com.hvdevs.shifterapp.dashboard.newappointment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.dashboard.dashboardfragment.ProfessionAdapter
import com.hvdevs.shifterapp.databinding.FragmentNewAppointmentBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.timessquare.CalendarPickerView
import java.text.DateFormat
import java.util.*

class NewAppointmentFragment : Fragment() {
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!

    private val professionList: ArrayList<String> = arrayListOf()
    private val professionalList: ArrayList<Professional> = arrayListOf() //El modelo de los profesionales
    private val name: ArrayList<String> = arrayListOf() //Solamente los nombres de los profesionales
    //La posicion del modelo con los nombres, coordinan para la busqueda del uid
    private val takenShift: ArrayList<String> = arrayListOf()
    private val modelShift: ArrayList<ModelShift> = arrayListOf()
    private val finalList: ArrayList<String> = arrayListOf()

    var v: String = ""
    var profession = 0
    var time = 0
    var professional = 0

    var profNode = ""
    var profNameNode = ""
    var shiftNode = ""
    var selectedDate = ""
    var uid = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentNewAppointmentBinding.inflate(inflater, container, false)

        initRv()

        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        //Al tocar el fade del sliding panel, este se cierra
        binding.slidingPanel.setFadeOnClickListener {
            this.collapseSlidingUpPanel()
        }

        binding.slidingPanel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener{
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                //Funcionalidades sobre el drag de la vista
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                //Controla el cambio de estado del dragView
                if (binding.slidingPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    binding.included.noShift.visibility = View.GONE //Pasamos a invisible el TV
                }
                if (binding.slidingPanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    clearFunctions() // Reiniciamos los edittext y las listas
                }
            }
        })

        val diaActual = Date() //Obtenemos el dia actual
        val anoActual = Calendar.getInstance()
        anoActual.add(Calendar.MONTH, 2) //Obtenemos 2 meses mas del calendatio para mostrar
        binding.calendar.init(diaActual, anoActual.time).inMode(CalendarPickerView.SelectionMode.SINGLE).withSelectedDate(diaActual)

        binding.calendar.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener{
            override fun onDateSelected(date: Date?) {

                if (binding.slidingPanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    expandSlidingUpPanel() //Expande el dragView
                }

                /** La idea seria obtener los datos de la DB y comparar
                 * si el turno aparece en la parte de takenShift, no deberia estar disponible **/
                v = "${date?.date}-${date?.month}-${date?.year}" //Obtenemos el id del dia para comparar en la db

                getProfession() //Obtenemos las profesiones

                selectedDate =  DateFormat.getDateInstance(DateFormat.FULL).format(date!!)
                selectedDate = selectedDate.substring(0, 1).uppercase() + selectedDate.substring(1)

                binding.included.date.text = selectedDate

                Log.d("TIME", selectedDate)
                Log.d("TIME", date.day.toString()) //LUNES, MARTES ... DOMINGO
                Log.d("TIME", date.month.toString()) //NUMERO DEL MES, COMIENZA DESDE 0
                Log.d("TIME", date.date.toString()) //NUMERO DEL DIA
                Log.d("TIME", date.year.toString()) //NUMERO DEL AÑO CON UN 1 DELANTE: 122

            }

            override fun onDateUnselected(date: Date?) {

            }

        })

//        binding.included.autoCompleteProfession.setOnItemClickListener { adapterView, view, i, l ->
//            professionalList.clear()
//            name.clear()
//            profession = i //Guardamos la posicion de la lista
//            getProfessional(professionList[i]) //Obtenemos los profesionales en la profesion
////            binding.included.newAppointmentProfessional.visibility = View.VISIBLE
//        }
//
//        binding.included.autoCompleteProfessional.setOnItemClickListener { adapterView, view, i, l ->
//            professional = i //Guardamos la posicion de la lista
//            modelShift.clear() //Limpiamos las listas
//            takenShift.clear()
//            finalList.clear()
//            getModelShift(professionalList[i].uid) //Obtenemos el modelo de los turnos
//            binding.included.newAppointmentShift.visibility = View.VISIBLE
//        }

//        binding.included.autoCompleteShift.setOnItemClickListener { adapterView, view, i, l ->
//            time = i //Guardamos la posicion de la lista
//            binding.included.select.visibility = View.VISIBLE
//        }

        binding.included.select.setOnClickListener {
            takeTheShift() //Se toma el turno
        }

        return binding.root
    }

    private fun initRv() {
        with(binding.included.speciality){
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.setHasFixedSize(true)
        }
        with(binding.included.specialist){
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.setHasFixedSize(true)
        }
        with(binding.included.shift){
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.setHasFixedSize(true)
        }
    }

    private fun clearFunctions() {
//        binding.included.newAppointmentShift.visibility = View.GONE
//        binding.included.newAppointmentProfessional.visibility = View.GONE
//        binding.included.select.visibility = View.GONE
//        binding.included.noShift.visibility = View.GONE
//        binding.included.autoCompleteProfession.setAdapter(null)
//        binding.included.autoCompleteProfessional.setAdapter(null)
//        binding.included.autoCompleteShift.setAdapter(null)
//        binding.included.autoCompleteProfession.setText("")
//        binding.included.autoCompleteProfessional.setText("")
//        binding.included.autoCompleteShift.setText("")
//        binding.included.autoCompleteProfession.clearFocus()
//        binding.included.autoCompleteProfessional.clearFocus()
//        binding.included.autoCompleteShift.clearFocus()
        professionList.clear() //Limpiamos las listas
        professionalList.clear()
        name.clear()
        finalList.clear()
        modelShift.clear()
        takenShift.clear()
    }

    private fun collapseSlidingUpPanel() { //Colapsa el panel
        binding.slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    private fun expandSlidingUpPanel() { //Expande el panel
        binding.slidingPanel.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun getProfession(){ //Obtenemos las profesiones (estan guardadas como las "key")
        val db = FirebaseDatabase.getInstance().getReference("profession").get()
        db.addOnSuccessListener { dataProf ->
            for (prof in dataProf.children){
                val profession = prof.key.toString()
                professionList.add(profession)
            }
            val pAdapter = ProfessionAdapter(professionList)
            binding.included.speciality.adapter = pAdapter
            pAdapter.setOnItemClickListener(object : ProfessionAdapter.OnItemClickListener{
                override fun onItemClick(position: Int) {
                    getProfessional(professionList[position])
                }
            })
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
                    if (profData != null) {
                        professionalList.add(profData)
                    }
                    if (profData != null) {
                        name.add(profData.name)
                    } //Pasamos a esta lista solo los nombres, luego nos sirve la posicion
                    //para comparar con la lista main
                }
            }
            val pAdapter = VariousAdapter(name)
            binding.included.specialist.adapter = pAdapter
            pAdapter.setOnItemClickListener(object : VariousAdapter.OnItemClickListener{
                override fun onItemClick(position: Int) {
                    Toast.makeText(context, "Asi", Toast.LENGTH_SHORT).show()
                    getModelShift(professionalList[position].uid)
                }

            })
        }
    }

    private fun getModelShift(uid: String) {
        val dbShift = FirebaseDatabase.getInstance().getReference("professional/$uid/modelShift")
        dbShift.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (shift in snapshot.children){
                        val sKey = shift.key //Obtenemos las key para ingresar a los datos
                        val dbChild = FirebaseDatabase.getInstance().getReference("professional/$uid/modelShift/$sKey")
                        dbChild.addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    for (s in snapshot.children){
                                        val time = s.value.toString()
                                        val model = ModelShift(
                                            s.value.toString(),
                                            sKey.toString()
                                        ) //Obtenemos el modelo de los turnos
                                        modelShift.add(model)
                                        finalList.add(time)
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                return
                            }
                        })
                    }
                }
                getTakenShift(uid)
            }
            override fun onCancelled(error: DatabaseError) {
                return
            }
        })
    }

    /**La disponibilidad es del profesional, no de la profesion:*/
    private fun getTakenShift(uid: String){
        val dbShift = FirebaseDatabase.getInstance().getReference("professional/$uid/takenShift")
        dbShift.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (profession in snapshot.children) {
                        val pKey = profession.key //Obtenemos las key del nodo de profesiones
                        dateObtain(uid, pKey.toString())
                    }
                }
                val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, finalList)
//                binding.included.autoCompleteShift.setAdapter(arrayAdapter)
                Log.d("ASD", finalList.toString())
                val mAdapter = VariousAdapter(finalList)
                binding.included.shift.adapter = mAdapter
                mAdapter.setOnItemClickListener(object: VariousAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {
                        Toast.makeText(context, "ando", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                return
            }
        })
    }

    private fun dateObtain(uid: String, pKey: String) {
        val dbChild = FirebaseDatabase.getInstance()
            .getReference("professional/$uid/takenShift/$pKey/$v")
        dbChild.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (date in snapshot.children){
                        val dKey = date.key.toString() //Obtenemos las key de las fechas
                        shiftObtain(uid, pKey, dKey)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                return
            }
        })
    }

    private fun shiftObtain(uid: String, pKey: String, dKey: String) {
        val dbChild2 = FirebaseDatabase.getInstance()
            .getReference("professional/$uid/takenShift/$pKey/$v/$dKey")
        dbChild2.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (time in snapshot.children){
                        val timeData = time.value.toString() //Obtenemos los turnos
                        Log.d("ASD", timeData)
                        finalList.remove(timeData) //Removemos los repetidos
                        modelShift.remove(ModelShift(timeData, dKey))
                        Log.d("ASD", modelShift.toString())
                    }
                    if (finalList.isEmpty()){
                        binding.included.noShift.visibility = View.VISIBLE
//                        binding.included.newAppointmentShift.visibility = View.GONE
                    } else {
//                        binding.included.newAppointmentShift.visibility = View.VISIBLE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                return
            }
        })
    }

    /** La idea es usar la modelShift, para sacar la uid y guardarla asi en la db*/
    private fun takeTheShift(){
        //Guardamos los nodos donde se meterian los datos:
        profNameNode = professionalList[professional].uid
        profNode = professionList[profession]
        shiftNode = modelShift[time].uid

        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("professional/$profNameNode/takenShift/$profNode/$v/$shiftNode").child("time")
            .setValue(finalList[time]) //Del lado del profesional
        val uDatabase = FirebaseDatabase.getInstance().reference
        with(uDatabase.child("users/$uid/shifts/$v/$shiftNode")){ //Del lado del cliente
            this.child("image").setValue("ASD")
            this.child("profession").setValue(profNode)
            this.child("professional").setValue(profNameNode)
            this.child("time").setValue(finalList[time])
            this.child("date").setValue(selectedDate)
        }

        binding.slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED //Colapsamos la barra
        onSNACK() //Llamamos al snackBar, para que, en caso de querer anular, se pueda
    }

    private fun cancelShift(){ //Se cancela el turno
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("professional/$profNameNode/takenShift/$profNode/$v/$shiftNode").child("time")
            .removeValue()
        mDatabase.child("users/$uid/shifts/$v/$shiftNode").child("time")
            .removeValue()
    }

    private fun onSNACK(){
        // create an instance of the snackbar
        val snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Turno guardado", Snackbar.LENGTH_LONG)
            .setAction("DESHACER") {
                // action here
                cancelShift()
            }.setAnchorView(R.id.bottomNav) //Colocamos el an
        // call show() method to
        // display the snackbar
        snackbar.show()
    }
}