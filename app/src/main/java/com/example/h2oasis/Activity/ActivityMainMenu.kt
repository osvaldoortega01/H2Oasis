package com.example.h2oasis.Activity

import android.app.Notification
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Data
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.example.h2oasis.Models.Notificacion
import com.example.h2oasis.R
import com.example.h2oasis.Worknoti
import java.sql.SQLException
import java.time.LocalDate
import java.util.Calendar
import java.util.UUID

class ActivityMainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        changeUsername()
        val btn_cisternas: Button = findViewById(R.id.btn_FillWaterTank)
        btn_cisternas.setOnClickListener{ openCisternas() }


        val btn_perfil: ImageView = findViewById(R.id.iv_perfil)
        btn_perfil.setOnClickListener{ openPerfil() }

        val btnLogOut: Button = findViewById(R.id.btn_LogOut)
        btnLogOut.setOnClickListener {
            logOut()
        }

        val btnStatistics: Button = findViewById(R.id.btn_Statistics)
        btnStatistics.setOnClickListener{
            openEstadisticas()
        }

        val btnNotifications: Button = findViewById(R.id.btn_Notifications)
        btnNotifications.setOnClickListener{
            openNotificaciones()
        }

        val btnSettings: Button = findViewById(R.id.btn_Settings)
        var notifications =  Notificacion(
            1,
            "Mensaje",
            "Encabezado",
            "12-05-2023",
        1,
            2,
            "nombreCorto",
            false
        )

        btnSettings.setOnClickListener{
            prueba(notifications)
        }
    }

    private fun generatekey (): String {
        return UUID.randomUUID().toString()
    }
    private fun EnviarData(titulo:String, detalle:String, id_noti:Int): Data {
        return Data. Builder ()
            .putString("Titulo", titulo)
            .putString("Detalle", detalle)
            .putInt("idnoti", id_noti). build()
    }

    fun prueba(notification: Notificacion){
        val tag = generatekey()
//        val alertTime = calendar.timeInMillis - System.currentTimeMillis()
        val alertTime = 3000.toLong()
        val random = (Math.random()*50+1).toInt () // IdNotificacion
        val data = EnviarData(notification.encabezado, notification.mensaje, random)
        Worknoti.GuardarNoti(alertTime, data, "tag1")
        Toast.makeText( this,"Notificacion Guardada.", Toast.LENGTH_SHORT).show()
    }
    /**
     * Abre la actividad de Cisternas, que contiene el DataGridView
     */
    private fun openCisternas(){
        var intent = Intent(this, ActivityCisternas::class.java)
        startActivity(intent)
    }

    /**
     * Abre la actividad de Perfil
     */
    private fun openPerfil(){
        var intent = Intent(this, ActivityUserProfile::class.java)
        startActivity(intent)
    }

    private fun openEstadisticas(){
        var intent = Intent(this, ActivityMeasurement::class.java)
        startActivity(intent)
    }

    private fun openNotificaciones(){
        var intent = Intent(this, ActivityNotifications::class.java)
        startActivity(intent)
    }

    /**
     * Cambia el nombre del usuario que se encuentra en la pantalla principal
     * Seleccionando el nombre que se encuentra guardado en las preferencias
     */
    private fun changeUsername(){
        val tvUsername: TextView = findViewById(R.id.TV_Username)
        try{
            tvUsername.setText(prefs.getUserLongName())
        }catch(ex: SQLException){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun logOut(){
        prefs.wipe()
        var intent = Intent(this, ActivityLogin::class.java)
        startActivity(intent)
        finish()
    }
}