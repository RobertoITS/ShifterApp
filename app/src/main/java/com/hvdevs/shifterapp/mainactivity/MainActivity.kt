package com.hvdevs.shifterapp.mainactivity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.hvdevs.shifterapp.dashboard.DashboardUserActivity
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.tutorialactivity.TutorialActivity
import com.hvdevs.shifterapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // view binding: para reemplazar el metodo find view by id vamos a usar la funcion binding view
    // de esta manera mejoramos la fluidez en la navegacion
    // documentacion: https://developer.android.com/topic/libraries/view-binding?hl=es-419

    // creamos la variable binding diciendo que es una instancia de la clase ActivityMainBinding
    private lateinit var binding: ActivityMainBinding

    // creamos la variable auth que gestionara los metodos de inicio de sesion usando el modulo de Auth de Firebase.
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // llamamos instanciamos la variable con el metodo inflate de la clase ActivityMainBinding ......
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // obtenemos instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance()

        debounce()

        //manejar cuando tocan el boton de registro
        binding.button.setOnClickListener{
            startActivity(Intent(this, TutorialActivity::class.java))
            finish()
            this.overridePendingTransition(R.anim.enter, R.anim.leave)
        }

    }

    private fun debounce(){
        val sharedPref = getSharedPreferences("remember", Context.MODE_PRIVATE)
        val remember = sharedPref.getBoolean("session", false)
        Handler(Looper.getMainLooper()).postDelayed({
            // decimos que si current user esta vacio, el usuario no ha iniciado sesion, con lo cual lo mandamos la pagina de registro o login, usando la funcion debounce.
            if (auth.currentUser == null || !remember){
                binding.progress.visibility = View.GONE
                binding.button.visibility = View.VISIBLE
            }
            // si no es nulo, es decir que ya inicio sesion, por lo cual sus datos estan guardados, si ese es el caso lo mandamos a la pagina inicial.
            else {
                startActivity(Intent(this, DashboardUserActivity::class.java))
                finish()
                this.overridePendingTransition(R.anim.enter, R.anim.leave)
            }
        }, 2000) // 3000 milisegundos = 3 segundos
    }
}