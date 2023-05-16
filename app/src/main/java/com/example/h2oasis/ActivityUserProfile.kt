package com.example.h2oasis

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.sql.PreparedStatement
import java.sql.SQLException

class ActivityUserProfile : AppCompatActivity() {

    private var sqlConnection = SQLConnection()
    lateinit var tietCompleteName: TextInputEditText
    lateinit var tietUsername: TextInputEditText
    lateinit var tietPassword: TextInputEditText
    lateinit var tietEmailAddress: TextInputEditText
    lateinit var btnSaveChanges: MaterialButton
    lateinit var btnDeleteAccount: MaterialButton
    lateinit var contrasena: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        tietCompleteName = findViewById(R.id.tiet_CompleteName)
        tietUsername = findViewById(R.id.tiet_Username)
        tietPassword = findViewById(R.id.tiet_Password)
        tietEmailAddress = findViewById(R.id.tiet_EmailAddress)
        btnSaveChanges = findViewById(R.id.btn_save_changes)
        btnDeleteAccount = findViewById(R.id.btn_deleteAccount)
        var btnChangePassword: TextView = findViewById(R.id.tv_changePassword)

        loadUserData()

        btnSaveChanges.setOnClickListener {
            if(areFieldsNotEmpty(tietUsername, tietCompleteName, tietEmailAddress))
            {
                if(contrasena == tietPassword.text.toString()){
                    saveChanges()
                }
                else{
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnChangePassword.setOnClickListener{
            openChangePassword()
        }

        btnDeleteAccount.setOnClickListener{
            showConfirmation()
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

    /**
     * Comprueba que los campos no estén vacíos para continuar con la función
     */
    private fun areFieldsNotEmpty(vararg fields: TextInputEditText): Boolean {
        val errorMessages = listOf(
            "El nombre de usuario es requerido",
            "El nombre completo es requerido",
            "El correo es requerido")

        fields.forEachIndexed { index, field ->
            if (TextUtils.isEmpty(field.text)) {
                field.error = errorMessages[index]
                return false
            }
        }
        return true
    }

    private fun openChangePassword(){
        var intent = Intent(this, ActivityChangePassword::class.java)
        startActivity(intent)
    }

    private fun showConfirmation(): Boolean{
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Realmente quieres eliminar tu cuenta?")
            .setIcon(R.drawable.baseline_delete_24)
            .setPositiveButton(R.string.delete) { _, _ ->
                (deleteUser())
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
            }.show()
        return false
    }

    private fun deleteUser(){
        try {
            val editCisternaSQL: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement("UPDATE usuarios SET habilitado = 0 WHERE idUsuario = ?")!!
            editCisternaSQL.setString(1, prefs.getId())
            editCisternaSQL.executeUpdate()

            Toast.makeText(this, "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show()

            // Regresa a la pantalla principal
            logOut()
        } catch (ex: SQLException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(
            applicationContext, "Usuario eliminada con éxito",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun logOut(){
        prefs.wipe()
        var intent = Intent(this, ActivityLogin::class.java)
        startActivity(intent)
        finish()
    }


}
