package com.hvdevs.shifterapp.signinactivity.utilities

interface ChangePage {
    fun pageSelect(position: Int) //Usamos esta interfaz para cambiar las paginas, se implementa en la actividad principal y se castea desde los fragment
}