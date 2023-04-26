package com.example.h2oasis

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.sql.PreparedStatement

class ActivityUserProfile : AppCompatActivity() {

    private var sqlConnection = SQLConnection()
    lateinit var tietCompleteName: TextInputEditText
    lateinit var tietUsername: TextInputEditText
    lateinit var tietPassword: TextInputEditText
    lateinit var tietEmailAddress: TextInputEditText
    lateinit var btnSaveChanges: MaterialButton
    lateinit var btnCancel: MaterialButton
    lateinit var contrasena: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        tietCompleteName = findViewById(R.id.tiet_CompleteName)
        tietUsername = findViewById(R.id.tiet_Username)
        tietPassword = findViewById(R.id.tiet_Password)
        tietEmailAddress = findViewById(R.id.tiet_EmailAddress)
        btnSaveChanges = findViewById(R.id.btn_save_changes)
        btnCancel = findViewById(R.id.btn_cancel)
        var btnChangePassword: TextView = findViewById(R.id.tv_changePassword)

        loadUserData()

        btnSaveChanges.setOnClickListener {
            if(contrasena == tietPassword.text.toString()){
                saveChanges()
            }
            else{
                Toast.makeText(this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }

        }

        btnChangePassword.setOnClickListener{
            openChangePassword()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadUserData() {
        try {
            val sqlGetUserData: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement(
                    "SELECT * FROM usuarios WHERE idUsuario = ?;"
                )!!
            sqlGetUserData.setString(1, prefs.getId())
            val userData = sqlGetUserData.executeQuery()

            if (userData.next()) {
                tietCompleteName.setText(userData.getString("nombreCompleto"))
                tietUsername.setText(userData.getString("usuario"))
                contrasena = userData.getString("contrasena")
                tietEmailAddress.setText(userData.getString("correo"))
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveChanges() {

        try {
            val sqlUpdateUserData: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement(
                    "UPDATE usuarios SET nombreCompleto = ?, usuario = ?,  correo = ? WHERE idUsuario = ?;"
                )!!
            sqlUpdateUserData.setString(1, tietCompleteName.text.toString())
            sqlUpdateUserData.setString(2, tietUsername.text.toString())
            sqlUpdateUserData.setString(3, tietEmailAddress.text.toString())
            sqlUpdateUserData.setString(4, prefs.getId())

            val rowsAffected = sqlUpdateUserData.executeUpdate()

            if (rowsAffected > 0) {
                Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
            }

        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun openChangePassword(){
        // TODO: To be implemented
    }
}
