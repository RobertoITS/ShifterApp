package com.hvdevs.shifterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.hvdevs.shifterapp.databinding.ActivityDashboardUserBinding
import com.hvdevs.shifterapp.newappointment.NewAppointmentFragment

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    var open = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bottomnav.setOnItemSelectedListener { item ->

            when(item.itemId) {
                R.id.home_page -> {
                    if (open != "a"){
//                        fragSelect(DashboardUserFragment())
                        open = "a"
                    }
                }
                R.id.myAppointment -> {
                    if (open != "b"){
//                        fragSelect(MyAppointmentFragment())
                        open = "b"
                    }
                }
                R.id.esp -> {  }
                R.id.myAccount -> {
                    if (open != "c"){
//                        fragSelect(MyAccountFragment())
                        fragAdd()
                        open = "c"
                    }
                }
            }
            true
        }
    }
    fun fragAdd(){
        val frag: Fragment? = supportFragmentManager.findFragmentById(R.id.frag)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frag, NewAppointmentFragment()).addToBackStack(null).commit()
    }
    private fun fragSelect(fragShow: Fragment) {
        //Instanciamos el contenedor
        val frag: Fragment? = supportFragmentManager.findFragmentById(R.id.frag)
        //Instanciamos la transicion
        val transaction = supportFragmentManager.beginTransaction()
        //Condicional para saber si el fragment ya esta añadido
        if (fragShow.isAdded){
            frag?.let {
                transaction
                    .hide(it)  //Esconde el fragment actual
                    .show(fragShow)  //Muestra el frament
            }
        } else {
            frag?.let {
                transaction
                    .hide(it)  //Esconde el fragment actual
                    .add(R.id.frag, fragShow)  //Añade el nuevo frament
            }
        }
        transaction.commit()
    }
    override fun onBackPressed() {
        //El boton de atras vuelve a la pantalla principal
        //Si se cumple la condicion, se cierra la app
        if (binding.bottomnav.selectedItemId == R.id.home_page){
            finish()
            super.onBackPressed()
        } else {
            //Con esta linea, selecciona un item del bottom nav!!
            binding.bottomnav.selectedItemId = R.id.home_page
        }
    }
}