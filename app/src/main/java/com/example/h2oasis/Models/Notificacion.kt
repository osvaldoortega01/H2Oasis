package com.example.h2oasis.Models

import net.sourceforge.jtds.jdbc.DateTime

data class Notificacion(
    val idNotificacion: Int,
    val mensaje: String,
    val encabezado: String,
    val fechaHora: String,
    val idUsuario: Int,
    val idCisterna: Int,
    val notificacionVista: Boolean
)
