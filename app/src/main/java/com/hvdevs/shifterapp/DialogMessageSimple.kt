package com.hvdevs.shifterapp

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.EditText

class DialogMessageSimple {

    //Creamos la interfaz que comunique la clase y reaccione a los datos enviados
    interface Data{
        //Esta funcion comunicaria la interfaz con los datos del fragment
        fun password(pass: String){
        }
    }

    //Instanciamos la interfaz
    private lateinit var communicator: Data

    //Creamos la funcion para que ejecute el layout
    fun dialogMessageSimple(context: Context?, activity: Data){

        //Le damos el valor de la actividad
        communicator = activity

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        //Guardo esta linea, Messirve
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_alert_pass)

        val text: EditText = dialog.findViewById(R.id.pass)
        val btn: Button = dialog.findViewById(R.id.acept)

//        text.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        btn.setOnClickListener {
            //Llamamos la funcion de la interfaz para comunicar los datos
            //Mandamos los datos del edittext
            communicator.password(text.text.toString())
            dialog.dismiss()
        }

        dialog.show()

    }

}