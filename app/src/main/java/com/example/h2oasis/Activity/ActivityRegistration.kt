package com.example.h2oasis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.example.h2oasis.R
import com.example.h2oasis.SQLConnection
import com.google.android.material.textfield.TextInputEditText
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ActivityRegistration : AppCompatActivity() {

    private var sqlConnection = SQLConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val btn_Register: Button = findViewById(R.id.btn_Register)
        btn_Register.setOnClickListener{
            registerUser()
        }
    }

    /**
     * Registra el usuario en la BD, realiza distintas validaciones y
     * al final inserta el usuario
     */
    private fun registerUser() {

        // Obtiene cada campo dentro del
        val etCompleteName: TextInputEditText = findViewById(R.id.tiet_CompleteName)
        val etUsername: TextInputEditText = findViewById(R.id.tiet_Username)
        val etPassword: TextInputEditText = findViewById(R.id.tiet_Password)
        val etConfirmPassword: TextInputEditText = findViewById(R.id.tiet_ConfirmPassword)
        val etEmailAddress: TextInputEditText = findViewById(R.id.tiet_EmailAddress)
        val fecha = LocalDateTime.now(ZoneOffset.UTC)

        val pas1 = etPassword.text.toString()
        val cpas1 = etConfirmPassword.text.toString()

        try {
            if (areFieldsNotEmpty(etUsername, etCompleteName, etPassword, etEmailAddress)) {
                if (arePasswordsMatching(pas1, cpas1)) {
                    if (isUserValid(etUsername)){
                        createNewUser(etCompleteName, etUsername, etPassword, etEmailAddress, fecha)
                        loginUser(etUsername, etPassword)
                    }
                    else {
                        showToast("El usuario ya existe")
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
     * Comprueba que el nombre de usuario que se está intentando agregar
     * no exista dentro de la BD
     */
    private fun isUserValid(etUsername: TextInputEditText): Boolean {
        val noRepeatUser: PreparedStatement = sqlConnection.dbConn()?.prepareStatement("SELECT idUsuario FROM usuarios WHERE usuario = ?")!!
        noRepeatUser.setString(1, etUsername.text.toString())
        val verifUser: ResultSet = noRepeatUser.executeQuery()
        return !verifUser.next()
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

    /**
     * Registra un usuario en la BD, obteniendo los campos necesarios
     * dentro del Front
     */
    private fun createNewUser(etCompleteName: TextInputEditText,
                              etUsername: TextInputEditText,
                              etPassword: TextInputEditText,
                              etEmailAddress: TextInputEditText,
                              fecha: LocalDateTime) {
        try {
            val nuevoUsuario: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement("INSERT INTO usuarios values (?,?,?,?,?, ?)")!!
            nuevoUsuario.setString(1, etCompleteName.text.toString())
            nuevoUsuario.setString(2, etUsername.text.toString())
            nuevoUsuario.setString(3, etPassword.text.toString())
            nuevoUsuario.setString(4, etEmailAddress.text.toString())
            nuevoUsuario.setString(5, fecha.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            nuevoUsuario.setBoolean(6, true)
            nuevoUsuario.executeUpdate()
            showToast("Cuenta Creada Exitosamente")
        } catch (ex: SQLException) {
            showToast(ex.message)
        }
    }

    /**
     * Se encarga de logear al usuario, guardando las prefs
     */
    private fun loginUser(etUsername: TextInputEditText, etPassword: TextInputEditText) {
        try {
            val getIdUsuario: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement("SELECT * FROM usuarios WHERE usuario = ? AND habilitado = 1")!!
            getIdUsuario.setString(1, etUsername.text.toString())
            val idUser: ResultSet = getIdUsuario.executeQuery()
            idUser.next()
            accessToDetail(etUsername, etPassword, idUser.getString(1), idUser.getString(2))
        } catch (ex: SQLException) {
            showToast(ex.message)
        }
    }

    /**
     * Comprueba que las nombre de Usuario y Contraseña no estén vacìas para continuar
     * guardando las credenciales en las preferencias de la app usando el método de saveUserCredentials()
     */
    private fun accessToDetail(etUsername: TextInputEditText,
                               etPassword: TextInputEditText,
                               idUser: String,
                               etUserLongName: String) {
        if (etUsername.text.toString().isNotEmpty() && etPassword.text.toString().isNotEmpty()) {
            saveUserCredentials(etPassword, etUsername, idUser, etUserLongName)
            openMainMenu()
        }
    }

    /**
     * Almacena las credenciales dentro de las preferencias de la app
     */

    private fun saveUserCredentials(etPassword: TextInputEditText,
                                    etUsername: TextInputEditText,
                                    idUser: String,
                                    etUserLongName: String) {
        prefs.saveUsername(etPassword.text.toString())
        prefs.savePassword(etUsername.text.toString())
        prefs.saveId(idUser)
        prefs.saveUserLongName(etUserLongName)
    }

    /**
     * Abre un mensaje Toast con el parámetro indicado
     */
    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Abre la actividad del Menú Principal
     */
    fun openMainMenu() {
        var intent = Intent(this, ActivityMainMenu::class.java)
        startActivity(intent)
    }

}