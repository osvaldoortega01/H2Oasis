package com.example.h2oasis.Models

data class Cisterna(
    val idCisterna: Int,
    val capacidad: String,
    val idAjuste: Int,
    val nombreCorto: String,
    val descripcion: String,
    val habilitado: Boolean
){
    override fun toString(): String {
        return nombreCorto
    }
}
