package com.example.h2oasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.h2oasis.H2Oasis.Companion.prefs
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class ActivityMainDRAFT : AppCompatActivity() {

    private var sqlConnection = SQLConnection()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_draft)

        val btn_cisternas: Button = findViewById(R.id.btn_FillWaterTank)
        changeUsername()
        btn_cisternas.setOnClickListener{ openCisternas() }
    }

    private fun openCisternas(){
        var intent = Intent(this, ActivityCisternas::class.java)
        startActivity(intent)
    }

    private fun changeUsername(){
        val tvUsername: TextView = findViewById(R.id.TV_Username)
        val iduser = prefs.getId()
        try{
            val txtsaldo: PreparedStatement = sqlConnection.dbConn()?.prepareStatement("SELECT nombreCompleto FROM usuarios WHERE idUsuario = ?")!!
            txtsaldo.setString(1, iduser)
            val tvsaldo: ResultSet = txtsaldo.executeQuery()
            tvsaldo.next()
            tvUsername.setText(tvsaldo.getString(1))
        }catch(ex: SQLException){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }
}