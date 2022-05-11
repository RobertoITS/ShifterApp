package com.hvdevs.shifterapp.loginfragment

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
import com.hvdevs.shifterapp.DashboardUserActivity
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

        // ## Animacion de los campos de texto
        startAnimX(binding.loginEmail)
        startAnimX(binding.loginPassword)
        startAnimX(binding.fgtPass)
        startAnimY(binding.loginBtn)


        // #### CUANDO PULSAMOS EL BOTON DE INGRESO ####
        binding.loginBtn.setOnClickListener {

            // ## Obtenemos los datos desde editText instanciado mas arriba
            val loginEmailText = binding.loginEmail.text.toString()
            val loginPasswordText = binding.loginPassword.text.toString()

            // #### Condiciones: ####
            // ## 1- si la contrasena es menor a 6, enviar toast ##
            if (loginPasswordText.length >= 6) {
                // ## Utilizamos el metodo login para acceder con las credenciales proporcionadas
                login(loginEmailText, loginPasswordText)
            } else {
                Toast.makeText(context, "La contrasena es muy corta", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }


    // #### METODO PRINCIPAL DE INGRESO POR EMAIL Y CONTRASENA ####
    private fun login(email: String?, password: String?) {

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