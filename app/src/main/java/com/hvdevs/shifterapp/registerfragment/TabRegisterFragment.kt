package com.hvdevs.shifterapp.registerfragment

import android.content.Intent
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
import com.hvdevs.shifterapp.DashboardUserActivity
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.databinding.FragmentTabRegisterBinding
import com.hvdevs.shifterapp.registerfragment.constructor.User
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

        binding.registerName.setOnFocusChangeListener { view, b ->
            if (b)
                view.background = resources.getDrawable(R.drawable.shape_edit_text01)
        }
        binding.registerPassword.setOnFocusChangeListener { view, b ->
            if (b)
                view.background = resources.getDrawable(R.drawable.shape_edit_text01)
        }
        binding.registerConfirmPassword.setOnFocusChangeListener { view, b ->
            if (b)
                view.background = resources.getDrawable(R.drawable.shape_edit_text01)
        }

        // #### CUANDO PULSAMOS EL BOTON DE REGISTRO ####
        binding.btnRegister2.setOnClickListener {
            binding.registerName.error = null
            binding.registerPassword.error = null
            binding.registerConfirmPassword.error = null
            if (binding.registerEmail.text.isNullOrEmpty()||
                binding.registerName.text.isNullOrEmpty()||
                binding.registerPassword.text.isNullOrEmpty()||binding.registerConfirmPassword.text.isNullOrEmpty()){
                    with(binding.registerName){
                        this.error = "Complete los campos"
                        this.startAnimation(shakeError())
                        this.background = resources.getDrawable(R.drawable.shape_edit_text_error)
                    }
            }else{
                //!!! IMPORTANTE: LOS DATOS DE LOS EDITTEXT SE CAPTURAN DENTRO DEL METODO ONCLICKLISTENER!!, EVITA ERRORES COMO LOS MIOS :'( !!!

                // ## Obtenemos los datos desde editText instanciado mas arriba
                val registerEmail = binding.registerEmail.text.toString()
                val registerName = binding.registerName.text.toString()
                val registerPassword = binding.registerPassword.text.toString()
                val registerCPassword = binding.registerConfirmPassword.text.toString()

                // #### Condiciones: ####
                // ## 1- si la contrasena es menor a 6, enviar toast ##
                if (registerPassword.length >= 6) {

                    // ## 2- si la contrasena no coincide con la confirmacion de la contrasena, enviar toast ##
                    if (registerPassword == registerCPassword) {

                        // ## Si pasamos las dos condiciones, ejecutamos el metodo createAccount y navegamos a la siguiente pagina ##
                        createAccount(registerEmail, registerPassword, registerName)
                    } else {
                        with(binding.registerConfirmPassword){
                            this.error = "Las contraseñas no coinciden!"
                            this.startAnimation(shakeError())
                        }
                    }
                } else {
                    with(binding.registerPassword){
                        this.error = "La contraseña es muy corta!"
                        this.startAnimation(shakeError())
                    }
                }
            }
        }


        return binding.root
    }

    // #### METODO PRINCIPAL DE REGISTRO Y ALMACENAMIENTO DE DATOS EN FIREBASE RTDB ####
    private fun createAccount(email: String?, password: String?, name: String?) {

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
                val user = User(email, "", name, password, "", "User", uid)

                // ## Una vez obtenemos el uid lo usamos para guardar el usuario en la base de datos
                database!!.child("users/users").child(uid.toString()).setValue(user)
                //Guardamos la contraseña en la base de datos para manipularla mas adelante
                database!!.child("users/$uid/data/email").setValue(email)
                database!!.child("users/$uid/data/lastName").setValue(name)
                database!!.child("users/$uid/data/name").setValue(name)
                database!!.child("users/$uid/data/pass").setValue(password)
                database!!.child("users/$uid/data/phone").setValue("")
                database!!.child("users/$uid/data/type").setValue("User")
                database!!.child("users/$uid/data/uid").setValue(uid)



                // ## Indicamos a traves de un toast que el usuario fue creado exitosamente
                Toast.makeText(context, "Registro correcto, bienvenido", Toast.LENGTH_LONG)
                    .show()

                // ## Navegamos a la pagina de inicio
                val intent = Intent(context, DashboardUserActivity::class.java)
                startActivity(intent)
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