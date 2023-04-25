package com.example.h2oasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.h2oasis.H2Oasis.Companion.prefs
import java.sql.SQLException

class ActivityMainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val btn_cisternas: Button = findViewById(R.id.btn_FillWaterTank)
        changeUsername()
        btn_cisternas.setOnClickListener{ openCisternas() }
    }

    /**
     * Abre la actividad de Cisternas, que contiene el DataGridView
     */
    private fun openCisternas(){
        var intent = Intent(this, ActivityCisternas::class.java)
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
}