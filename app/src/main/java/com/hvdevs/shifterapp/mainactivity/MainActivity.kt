package com.hvdevs.shifterapp.mainactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.databinding.ActivityMainBinding
import com.hvdevs.shifterapp.signinactivity.SignInActivity

class MainActivity : AppCompatActivity() {
    // view binding: para reemplazar el metodo find view by id vamos a usar la funcion binding view
    // de esta manera mejoramos la fluidez en la navegacion
    // documentacion: https://developer.android.com/topic/libraries/view-binding?hl=es-419

    // creamos la variable binding diciendo que es una instancia de la clase ActivityMainBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // llamamos instanciamos la variable con el metodo inflate de la clase ActivityMainBinding ......
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //manejar cuando tocan el boton de registro
        binding.loginBtn.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            this.overridePendingTransition(R.anim.enter, R.anim.leave)
        }

    }
}