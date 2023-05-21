package com.example.h2oasis.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h2oasis.H2Oasis
import com.example.h2oasis.Models.Notificacion
import com.example.h2oasis.NotificacionAdapter
import com.example.h2oasis.R
import com.example.h2oasis.SQLConnection
import com.example.h2oasis.H2Oasis.Companion.prefs
import java.sql.PreparedStatement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ActivityNotifications : AppCompatActivity() {
    private var sqlConnection = SQLConnection()
    private lateinit var seenNotifications: List<Notificacion>
    private lateinit var unseenNotifications: List<Notificacion>
    private lateinit var rvNotifications: RecyclerView
    private lateinit var notificacionAdapter: NotificacionAdapter // convierte tu adaptador en una variable de instancia para que puedas acceder a él más tarde

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)

        loadNotifications()
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = notificacionAdapter


    }

    private fun fetchNotificacionesFromDatabase(notificacionVista: Boolean): List<Notificacion> {
        val listaDeNotificaciones = mutableListOf<Notificacion>()

        try {
            val sqlGetNotificaciones: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement(
                    "SELECT * FROM notificaciones as n " +
                    "INNER JOIN cisternas as c ON n.idCisterna = c.idCisterna " +
                    "WHERE idUsuario = ? AND notificacionVista = ? " +
                    "ORDER BY fechaHora DESC;"
                )!!
            sqlGetNotificaciones.setString(1, prefs.getId())
            sqlGetNotificaciones.setBoolean(2, notificacionVista)
            val resultNotificaciones = sqlGetNotificaciones.executeQuery()




            while (resultNotificaciones.next()) {
                val dateTimeString = resultNotificaciones.getString("fechaHora")
                val truncatedDateTimeString = dateTimeString.substringBeforeLast(".")

                val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTime = LocalDateTime.parse(truncatedDateTimeString, dateTimeFormatter)
                val outputFormatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
                val formattedDate = dateTime.format(outputFormatter)


                val notificacion = Notificacion(
                    resultNotificaciones.getInt("idNotificacion"),
                    resultNotificaciones.getString("mensaje"),
                    resultNotificaciones.getString("encabezado"),
                    formattedDate,
                    resultNotificaciones.getInt("idUsuario"),
                    resultNotificaciones.getInt("idCisterna"),
                    resultNotificaciones.getString("nombreCorto"),
                    resultNotificaciones.getBoolean("notificacionVista")
                )

                listaDeNotificaciones.add(notificacion)
            }
        } catch (ex: Exception) {
            Log.e("Error", ex.message.toString())
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
        return listaDeNotificaciones
    }

    fun loadNotifications() {
        seenNotifications = fetchNotificacionesFromDatabase(true)
        unseenNotifications = fetchNotificacionesFromDatabase(false)

        notificacionAdapter = NotificacionAdapter(this, unseenNotifications, seenNotifications)
        rvNotifications.adapter = notificacionAdapter
    }


}