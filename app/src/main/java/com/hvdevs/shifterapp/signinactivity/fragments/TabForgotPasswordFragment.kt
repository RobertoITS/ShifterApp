package com.hvdevs.shifterapp.signinactivity.fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.hvdevs.shifterapp.R
import com.hvdevs.shifterapp.databinding.FragmentTabForgotPasswordBinding
import com.hvdevs.shifterapp.signinactivity.utilities.ChangePage

class TabForgotPasswordFragment : Fragment() {
    private var _binding: FragmentTabForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabForgotPasswordBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        progress = ProgressDialog(context)

        binding.back.setOnClickListener {
            (activity as? ChangePage)?.pageSelect(-3) //Cambiamos las paginas
        }

        binding.send.setOnClickListener{
            val email = binding.emailInput.text.toString()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(context, "Para continuar, debe ingresar su EMAIL", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun resetPassword(email: String) {
        auth.setLanguageCode("es")
        auth.sendPasswordResetEmail(email).addOnCompleteListener(object : OnCompleteListener<Void>{
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful){
                    progress.setMessage("Espere un momento...")
                    progress.setCanceledOnTouchOutside(false)
                    progress.show()
                    Toast.makeText(context, "Se ha enviado el correo para restablecer su contraseña, revise su casilla", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se pudo enviar el correo para restablecer contraseña, intente nuevamente", Toast.LENGTH_SHORT).show()
                }
                progress.dismiss()
            }
        })
    }
}