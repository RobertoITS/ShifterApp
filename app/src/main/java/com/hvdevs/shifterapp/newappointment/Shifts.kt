package com.hvdevs.shifterapp.newappointment

data class Shifts(
    var date: String = "",
    var dayKey: String = "", //La key del nodo del dia del turno
    var image: String = "",
    var profession: String = "",
    var professional: String = "",
    var professionalNode: String = "",
    var shiftKey: String = "", //La key del nodo de turno
    var time: String = ""
)