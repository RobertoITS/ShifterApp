package com.hvdevs.shifterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.hvdevs.shifterapp.dashboardfragment.DashboardFragment
import com.hvdevs.shifterapp.databinding.ActivityDashboardUserBinding
import com.hvdevs.shifterapp.myaccountfragment.MyAccountFragment
import com.hvdevs.shifterapp.newappointment.NewAppointmentFragment

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction
//            .add(R.id.frag, DashboardFragment())
//            .commit()
//
//        binding.bottomNav.setItemSelected(R.id.home_page)
//
//        bottomNav()
//
        val adapter = ViewPager2Adapter(this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.setPageTransformer(DepthPageTransformer())
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{binding.bottomNav.setItemSelected(R.id.home_page)}
                    1->{binding.bottomNav.setItemSelected(R.id.myAppointment)}
                    2->{binding.bottomNav.setItemSelected(R.id.esp)}
                    3->{binding.bottomNav.setItemSelected(R.id.myAccount)}
                }
            }
        })

        binding.bottomNav.setOnItemSelectedListener { item->
            when (item){
                R.id.home_page->{ binding.viewPager2.currentItem = 0 }
                R.id.myAppointment->{ binding.viewPager2.currentItem = 1 }
                R.id.esp->{ binding.viewPager2.currentItem = 2 }
                R.id.myAccount->{ binding.viewPager2.currentItem = 3 }
            }
        }
    }
//    private fun bottomNav(){
//        //Recurri a algo basico, no sabia como controlar la doble seleccion de los botones de navegacion :P
//        var open = ""
//        binding.bottomNav.setOnItemSelectedListener { item ->
//
//            when(item) {
//                R.id.home_page -> {
//                    if (open != "a"){
//                        fragSelect(DashboardFragment())
//                        open = "a"
//                    }
//                }
//                R.id.myAppointment -> {
//                    if (open != "b"){
//                        fragSelect(NewAppointmentFragment())
//                        open = "b"
//                    }
//                }
//                R.id.esp -> {  }
//                R.id.myAccount -> {
//                    if (open != "c"){
//                        fragSelect(MyAccountFragment())
//                        open = "c"
//                    }
//                }
//            }
//        }
//    }
//
//    private fun fragSelect(fragShow: Fragment) {
//        //Instanciamos el contenedor
//        val frag: Fragment? = supportFragmentManager.findFragmentById(R.id.frag)
//        //Instanciamos la transicion
//        val transaction = supportFragmentManager.beginTransaction()
//        //Condicional para saber si el fragment ya esta añadido
//        if (fragShow.isAdded){
//            frag?.let {
//                transaction
//                    .hide(it)  //Esconde el fragment actual
//                    .show(fragShow)  //Muestra el frament
//            }
//        } else {
//            frag?.let {
//                transaction
//                    .hide(it)  //Esconde el fragment actual
//                    .add(R.id.frag, fragShow)  //Añade el nuevo frament
//            }
//        }
//        transaction.commit()
//    }
//
//    override fun onBackPressed() {
//        //El boton de atras vuelve a la pantalla principal
//        //Si se cumple la condicion, se cierra la app
//        if (binding.bottomNav.getSelectedItemId() == R.id.home_page){
//            finish()
//            super.onBackPressed()
//        } else {
//            //Con esta linea, selecciona un item del bottom nav!!
//            binding.bottomNav.setItemSelected(R.id.home_page)
//        }
//    }
}