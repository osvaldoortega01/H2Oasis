package com.example.h2oasis.Models

import java.sql.Time

data class FrecuenciaAlertas(
    val idFrecuenciaAlertas: Int,
    val horaPrimeraNotificacion: Time,
    val frecuencia: Int,
    val idUsuario: Int,
    val tipoFrecuencia: String,
    val habilitadas: Boolean
)