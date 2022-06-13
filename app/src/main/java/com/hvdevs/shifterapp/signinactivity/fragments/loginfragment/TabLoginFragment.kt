package com.hvdevs.shifterapp.signinactivity.fragments.loginfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.hvdevs.shifterapp.signinactivity.utilities.ChangePage
import com.hvdevs.shifterapp.dashboard.DashboardUserActivity
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.databinding.FragmentTabLoginBinding
import java.util.*

class TabLoginFragment : Fragment() {
    private var _binding: FragmentTabLoginBinding? = null
    private val binding get() = _binding!!

    // VARIABLES MAIN
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabLoginBinding.inflate(inflater, container, false)

        // ## Obtenemos la instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // #### CUANDO PULSAMOS EL BOTON DE INGRESO ####
        binding.login.setOnClickListener {

            // ## Obtenemos los datos desde editText instanciado mas arriba
            val loginEmailText = binding.userInput.text.toString()
            val loginPasswordText = binding.passInput.text.toString()

            // #### Condiciones: ####
            // ## 1- si la contrasena es menor a 6, enviar toast ##
            if (loginPasswordText.length >= 6) {
                // ## Utilizamos el metodo login para acceder con las credenciales proporcionadas
                login(loginEmailText, loginPasswordText)
            } else {
                Toast.makeText(context, "La contrasena es muy corta", Toast.LENGTH_LONG).show()
            }
        }

        binding.back.setOnClickListener {
            (activity as? ChangePage)?.pageSelect(-1) //Cambiamos las paginas
        }

        binding.forgot.setOnClickListener {
            (activity as? ChangePage)?.pageSelect(3) //Cambiamos las paginas
        }

        binding.createAccount.setOnClickListener {
            (activity as? ChangePage)?.pageSelect(1) //Cambiamos las paginas
        }

        return binding.root
    }


    // #### METODO PRINCIPAL DE INGRESO POR EMAIL Y CONTRASENA ####
    private fun login(email: String?, password: String?) {
        //En caso de querer recordar el inicio de sesion:
        val sharedPref = activity?.getSharedPreferences("remember", Context.MODE_PRIVATE)
        if (binding.remember.isChecked){
            with (sharedPref!!.edit()) {
                putBoolean("session", true)
                apply()
            }
        }

        // ## Utilizamos el metodo signInWithEmailAndPassword de FirebaseAuth, y le agregamos un listener para ver si el registro fue exitoso
        auth!!.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(requireActivity()) { task: Task<AuthResult?> ->

                // ## Si el ingreso fue exitoso haremos lo siguiente:
                if (task.isSuccessful) {
                    // ## Indicamos a traves de un toast que el usuario ingreso exitosamente
                    Toast.makeText(context, "Login exitoso, bienvenido", Toast.LENGTH_LONG).show()

                    // ## Navegamos a la pagina de inicio
                    val intent = Intent(context, DashboardUserActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    activity?.overridePendingTransition(R.anim.enter, R.anim.leave)
                } else {
                    val errorMessage = Objects.requireNonNull(task.exception)?.localizedMessage
                    Toast.makeText(context, "Hubo un error$errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }

    //Animaciones de transicion
    private fun translationAnimY(view: View){
        view.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(300).start()
    }
    private fun translationAnimX(view: View){
        view.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(300).start()
    }
    private fun startAnimY(view: View){
        view.translationY = -200f
        view.alpha = 0f
        translationAnimY(view)
    }
    private fun startAnimX(view: View){
        view.translationX = 800f
        view.alpha = 0f
        translationAnimX(view)
    }
}