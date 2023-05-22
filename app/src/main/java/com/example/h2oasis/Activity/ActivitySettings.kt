package com.example.h2oasis.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import com.example.h2oasis.H2Oasis
import com.example.h2oasis.Models.FrecuenciaAlertas
import com.example.h2oasis.R
import com.example.h2oasis.SQLConnection
import java.sql.PreparedStatement

class ActivitySettings : AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var sbFrecuencia: SeekBar
    private lateinit var etFrecuencia: EditText
    private var sqlConnection = SQLConnection()
    private var item = "Semanas"
    var startpoint = 0
    var endpoint = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tiposAhorro = resources.getStringArray(R.array.tipo_notificacion)
        sbFrecuencia = findViewById(R.id.sbFrecuencia)
        etFrecuencia = findViewById(R.id.et_frecuencia)
        sbFrecuencia.min = 1
        val dropdown = findViewById<AutoCompleteTextView>(R.id.autotv_tipo) // Cambia AutoCompleteTextView por Spinner si es necesario
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            tiposAhorro
        )
        dropdown.setAdapter(adapter)

        val autotvTipoAhorro = findViewById<AutoCompleteTextView>(R.id.autotv_tipo)

        with(autotvTipoAhorro){
            setAdapter(adapter)
            onItemClickListener = this@ActivitySettings
        }
        configurarseekBar()

        var btnSaveChanges = findViewById<Button>(R.id.btn_save_changes)
        btnSaveChanges.setOnClickListener{
            editNotifSettings()
        }

        var btnDeactivateNotifications = findViewById<Button>(R.id.btn_deactivateNotifications)
        btnDeactivateNotifications.setOnClickListener{
            deactivateNotifications()
        }
    }

    private fun configurarseekBar(){
        sbFrecuencia.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {//Imprimo progreso del slide bar
                etFrecuencia.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {//Recupero punto de inicio
                if(seekBar != null){
                    startpoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {//Recupero punto final
                if(seekBar != null){
                    endpoint = seekBar.progress
                }
            }

        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {//Recupero item seleccionado
        item = parent?.getItemAtPosition(position).toString()
        try{

            when(item)
            {
                "Horas" -> sbFrecuencia.max = 24
                "Días" -> sbFrecuencia.max = 31
                "Semanas" -> sbFrecuencia.max = 52
                "Meses" -> sbFrecuencia.max = 12
            }
        } catch (ex: Exception){
            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun editNotifSettings(){
        try {
            val sqlUpdateNotificationsSettings: PreparedStatement = sqlConnection.dbConn()?.prepareStatement(
                "IF EXISTS (SELECT 1 FROM frecuenciaAlertas WHERE idUsuario = ?) " +
                        "UPDATE frecuenciaAlertas SET " +
                        "[horaPrimeraNotificacion] = ?, " +
                        "[frecuencia] = ?, " +
                        "[tipoFrecuencia] = ? , " +
                        "[habilitadas] = ? " +
                        "WHERE idUsuario = ? " +
                        "ELSE " +
                        "INSERT INTO frecuenciaAlertas ([horaPrimeraNotificacion], [frecuencia], [tipoFrecuencia], [habilitadas], [idUsuario]) " +
                        "VALUES (?, ?, ?, ?, ?)"
            )!!

            sqlUpdateNotificationsSettings.setString(1, H2Oasis.prefs.getId())
            sqlUpdateNotificationsSettings.setString(2, "10:00:00")
            sqlUpdateNotificationsSettings.setString(3, etFrecuencia.text.toString())
            sqlUpdateNotificationsSettings.setString(4, item.uppercase())
            sqlUpdateNotificationsSettings.setBoolean(5, true)
            sqlUpdateNotificationsSettings.setString(6, H2Oasis.prefs.getId())
            // Para el bloque de INSERT
            sqlUpdateNotificationsSettings.setString(7, "10:00:00")
            sqlUpdateNotificationsSettings.setString(8, etFrecuencia.text.toString())
            sqlUpdateNotificationsSettings.setString(9, item.uppercase())
            sqlUpdateNotificationsSettings.setBoolean(10, true)
            sqlUpdateNotificationsSettings.setString(11, H2Oasis.prefs.getId())

            val rowsAffected = sqlUpdateNotificationsSettings.executeUpdate()


            if (rowsAffected > 0) {
                Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
            }

        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun deactivateNotifications(){
        try {
            val sqlUpdateNotificationsSettings: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement(
                    "UPDATE frecuenciaAlertas SET [habilitadas] = ? WHERE idUsuario = ?;"
                )!!
            sqlUpdateNotificationsSettings.setBoolean(1, false)
            sqlUpdateNotificationsSettings.setString(2, H2Oasis.prefs.getId())

            val rowsAffected = sqlUpdateNotificationsSettings.executeUpdate()

            if (rowsAffected > 0) {
                Toast.makeText(this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
            }

        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }
}