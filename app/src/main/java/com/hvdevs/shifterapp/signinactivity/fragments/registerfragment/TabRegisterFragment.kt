package com.hvdevs.shifterapp.signinactivity.fragments.registerfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hvdevs.shifterapp.databinding.FragmentTabRegisterBinding
import com.hvdevs.shifterapp.signinactivity.fragments.registerfragment.constructor.User
import com.hvdevs.shifterapp.signinactivity.utilities.ChangePage
import java.util.*

class TabRegisterFragment : Fragment() {
    private var _binding: FragmentTabRegisterBinding? = null
    private val binding get() = _binding!!

    // VARIABLES MAIN
    private var auth: FirebaseAuth? = null
    private var database: DatabaseReference? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabRegisterBinding.inflate(inflater, container, false)

        // ## Obtenemos las instancias de Auth y de Firebase RTDB
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        // #### CUANDO PULSAMOS EL BOTON DE REGISTRO ####
        binding.next.setOnClickListener {
            binding.user.error = null
            binding.pass.error = null
            binding.repeatPass.error = null
            if (binding.userInput.text.isNullOrEmpty()||
                binding.passInput.text.isNullOrEmpty()||binding.repeatPassInput.text.isNullOrEmpty()){
                    with(binding.user){
                        this.error = "Complete los campos"
                        this.startAnimation(shakeError())
                    }
            }
            else{
                //!!! IMPORTANTE: LOS DATOS DE LOS EDITTEXT SE CAPTURAN DENTRO DEL METODO ONCLICKLISTENER!!, EVITA ERRORES COMO LOS MIOS :'( !!!

                // ## Obtenemos los datos desde editText instanciado mas arriba
                val registerEmail = binding.userInput.text.toString()
                val registerPassword = binding.passInput.text.toString()
                val registerCPassword = binding.repeatPassInput.text.toString()

                // #### Condiciones: ####
                // ## 1- si la contrasena es menor a 6, enviar toast ##
                if (registerPassword.length >= 6) {

                    // ## 2- si la contrasena no coincide con la confirmacion de la contrasena, enviar toast ##
                    if (registerPassword == registerCPassword) {

                        // ## Si pasamos las dos condiciones, ejecutamos el metodo createAccount y navegamos a la siguiente pagina ##
                        createAccount(registerEmail, registerPassword)
                    } else {
                        with(binding.repeatPass){
                            this.error = "Las contraseñas no coinciden!"
                            this.startAnimation(shakeError())
                        }
                    }
                } else {
                    with(binding.pass){
                        this.error = "La contraseña es muy corta!"
                        this.startAnimation(shakeError())
                    }
                }
            }
        }

        binding.back.setOnClickListener {
            (activity as? ChangePage)?.pageSelect(-2) //Cambiamos las paginas
        }
        return binding.root
    }

    // #### METODO PRINCIPAL DE REGISTRO Y ALMACENAMIENTO DE DATOS EN FIREBASE RTDB ####
    private fun createAccount(email: String?, password: String?) {

        // ## Utilizamos el metodo createUser FirebaseAuth, y le agregamos un listener para ver si el registro fue exitoso
        auth!!.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(
            requireActivity()
        ) { task: Task<AuthResult?> ->

            // ## Si fue exitoso haremos lo siguiente:
            if (task.isSuccessful) {

                // ## Una vez generado el usuario capturamos su token (llamado UID - que es un identificador unico del usuario)
                val uid =
                    Objects.requireNonNull(auth!!.currentUser)?.uid

                // ## Creamos un objeto con el nombre y el email capturados a fin de guardarlos en la base de datos
                val user = User(email, "", "", password, "", "User", uid)

                // ## Una vez obtenemos el uid lo usamos para guardar el usuario en la base de datos
//                database!!.child("users/users").child(uid.toString()).setValue(user)
                //Guardamos la contraseña en la base de datos para manipularla mas adelante
                database!!.child("users/$uid/data/user").setValue(email)
                database!!.child("users/$uid/data/lastName").setValue("")
                database!!.child("users/$uid/data/name").setValue("")
                database!!.child("users/$uid/data/pass").setValue(password)
                database!!.child("users/$uid/data/phone").setValue("")
                database!!.child("users/$uid/data/type").setValue("User")
                database!!.child("users/$uid/data/uid").setValue(uid)
                database!!.child("users/$uid/data/birth").setValue("")
                database!!.child("users/$uid/data/email").setValue("")



                // ## Indicamos a traves de un toast que el usuario fue creado exitosamente
                Toast.makeText(context, "Registro correcto, complete sus datos", Toast.LENGTH_LONG)
                    .show()

                // ## Navegamos a la pagina de inicio
                (activity as? ChangePage)?.pageSelect(1) //Cambiamos las paginas
            } else {
                val errorMessage =
                    Objects.requireNonNull(task.exception)
                        ?.localizedMessage
                Toast.makeText(context, "Hubo un error$errorMessage", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    //Una animacion de sacudida para los text field
    private fun shakeError(): TranslateAnimation {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(7f)
        return shake
    }
}