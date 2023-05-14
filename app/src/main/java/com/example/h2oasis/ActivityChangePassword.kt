package com.example.h2oasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.google.android.material.textfield.TextInputEditText
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime

class ActivityChangePassword : AppCompatActivity() {

    private var sqlConnection = SQLConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        val btn_changePassword: Button = findViewById(R.id.btn_changePassword)
        btn_changePassword.setOnClickListener{
            changePassword()
        }
    }

    /**
     * Registra el usuario en la BD, realiza distintas validaciones y
     * al final inserta el usuario
     */
    private fun changePassword() {

        // Obtiene cada campo dentro del
        val etActualPassword: TextInputEditText = findViewById(R.id.tiet_actualPassword)
        val etNewPassword: TextInputEditText = findViewById(R.id.tiet_NewPassword)
        val etConfirmPassword: TextInputEditText = findViewById(R.id.tiet_ConfirmPassword)

        val pas = etActualPassword.text.toString()
        val pas1 = etNewPassword.text.toString()
        val cpas1 = etConfirmPassword.text.toString()

        try {
            if (areFieldsNotEmpty(etActualPassword, etNewPassword, etConfirmPassword)) {
                if (arePasswordsMatching(pas1, cpas1)) {
                    if (pas==  prefs.getPassword()){
                        updatePassword(pas1)
                    }
                    else {
                        showToast("Contraseña actual incorrecta")
                    }
                } else {
                    showToast("Las contraseñas no coinciden")
                }
            }
        } catch (ex: SQLException) {
            showToast(ex.message)
        }
    }
    /**
     * Comprueba que las contraseñas sean correctas
     */
    private fun arePasswordsMatching(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    /**
     * Comprueba que los campos no estén vacíos para continuar con la función
     */
    private fun areFieldsNotEmpty(vararg fields: TextInputEditText): Boolean {
        val errorMessages = listOf(
            "El nombre de usuario es requerido",
            "El nombre completo es requerido",
            "La contraseña es requerida",
            "El correo es requerido")

        fields.forEachIndexed { index, field ->
            if (TextUtils.isEmpty(field.text)) {
                field.error = errorMessages[index]
                return false
            }
        }
        return true
    }

    private fun updatePassword(newPassword: String) {
        try {
            val updatePasswordSQL: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement("UPDATE usuarios SET contrasena = ? WHERE idUsuario = ?")!!
            updatePasswordSQL.setString(1, newPassword)
            updatePasswordSQL.setString(2, prefs.getId())
            updatePasswordSQL.executeUpdate()
            Toast.makeText(this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()

            openLogin()
        } catch (ex: SQLException) {
            showToast(ex.message)
        }
    }

    /**
     * Abre un mensaje Toast con el parámetro indicado
     */
    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun openLogin(){
        var intent = Intent(this, ActivityLogin::class.java)
        startActivity(intent)
    }
}