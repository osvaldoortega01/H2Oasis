package com.example.h2oasis.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.example.h2oasis.Models.Usuario
import com.example.h2oasis.R
import com.example.h2oasis.SQLConnection
import com.google.android.material.textfield.TextInputEditText
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class ActivityLogin : AppCompatActivity() {

    private var sQLConnection = SQLConnection();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.btn_login)
        loginButton.setOnClickListener{ validateLogin() }

        val registerButton: Button = findViewById(R.id.btn_Register)
        registerButton.setOnClickListener{ openRegister() }

        checkUserValues()
    }

    /**
     * Validates user input and attempts to log in if input is valid.
     */
    private fun validateLogin() {
        val tietUsername: TextInputEditText = findViewById(R.id.tiet_Username)
        val tietPassword: TextInputEditText = findViewById(R.id.tiet_Password)

        if (isInputValid(tietUsername, tietPassword)) {
            val username = tietUsername.text.toString()
            val password = tietPassword.text.toString()

            val user = getUserByUsername(username)

            if (user != null && password == user.password) {
                Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                accessToDetail(username, password, user.id, user.completeName)
            } else {
                Toast.makeText(this, "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Checks if the input fields are not empty and sets error messages if needed.
     * @param tietUsername The username input field.
     * @param tietPassword The password input field.
     * @return true if both fields are not empty, false otherwise.
     */
    private fun isInputValid(tietUsername: TextInputEditText, tietPassword: TextInputEditText): Boolean {
        var isValid = true

        if (TextUtils.isEmpty(tietUsername.text)) {
            tietUsername.setError("El nombre de usuario es requerido")
            isValid = false
        }
        if (TextUtils.isEmpty(tietPassword.text)) {
            tietPassword.setError("La contraseña es requerida")
            isValid = false
        }

        return isValid
    }

    /**
     * Retrieves user information from the database by username.
     * @param username The username to search for.
     * @return a Usuario object containing user data if found, null otherwise.
     */
    private fun getUserByUsername(username: String): Usuario? {
        var user: Usuario? = null

        try {
            val getUser: PreparedStatement = sQLConnection
                .dbConn()
                ?.prepareStatement("SELECT * FROM usuarios WHERE habilitado = 1 AND usuario = ?")!!
            getUser.setString(1, username)
            val resultSet: ResultSet = getUser.executeQuery()

            if (resultSet.next()) {
                user = Usuario(
                    id = resultSet.getString(1),
                    completeName = resultSet.getString(2),
                    username = resultSet.getString(3),
                    password = resultSet.getString(4),
                    emailAddress = resultSet.getString(5),
                    registrationDate = resultSet.getString(6),
                    enabled = resultSet.getBoolean(7)
                )
            }
        } catch (ex: SQLException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

        return user
    }

    /**
     * Permite mantener la sesión iniciada
     */
    private fun checkUserValues(){
        if(prefs.getId().isNotEmpty()){
            openMainMenu()
        }
    }

    /**
     * Guarda las preferencias del usuario para mantenerlas globales
     */
    private fun accessToDetail(userName: String, password: String, idUser: String, userLongName: String){
        prefs.saveUsername(userName)
        prefs.savePassword(password)
        prefs.saveId(idUser)
        prefs.saveUserLongName(userLongName)
        openMainMenu()
    }

    private fun openMainMenu() {
        var intent = Intent(this, ActivityMainMenu::class.java)
        startActivity(intent)
    }

    private fun openRegister(){
        var intent = Intent(this, ActivityRegistration::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}