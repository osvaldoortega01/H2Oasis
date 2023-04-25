package com.example.h2oasis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.h2oasis.H2Oasis.Companion.prefs
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

        // checkUserValues()
    }

    private fun validateLogin(){
        // Obtiene los campos rellenados en el Front
        var tiet_Username: TextInputEditText = findViewById(R.id.tiet_Username)
        var tiet_Password: TextInputEditText = findViewById(R.id.tiet_Password)

        // Comprueba que los campos no estén vacíos
        if (TextUtils.isEmpty(tiet_Username.text)) {
            tiet_Username.setError("El nombre de usuario es requerido")
        }
        else if (TextUtils.isEmpty(tiet_Password.text)){
            tiet_Password.setError("La contraseña es requerida")
        }
        else {
            try{
                // Obtiene la contraseña de la tabla Usuarios
                val usuario: PreparedStatement = sQLConnection.
                            dbConn()?.
                            prepareStatement("SELECT contrasena FROM usuarios WHERE usuario = ?")!!
                usuario.setString(1, tiet_Username.text.toString())
                var contrasena: ResultSet = usuario.executeQuery()
                contrasena.next()

                // Si la contraseña de la BD y la ingresada son iguales entonces continúa
                if (tiet_Password.text.toString() == contrasena.getString(1)){
                    Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                    try{
                        // Obtiene el idUsuario en sesión y lo registra en prefs para tenerlo global
                        val getidusuario: PreparedStatement = sQLConnection.
                                dbConn()?.
                                prepareStatement("SELECT * FROM usuarios WHERE usuario = ?")!!
                        getidusuario.setString(1, tiet_Username.text.toString())
                        val iduser: ResultSet = getidusuario.executeQuery()
                        iduser.next()
                        accessToDetail(
                            tiet_Username.text.toString(),
                            tiet_Password.text.toString(),
                            iduser.getString(1),
                            iduser.getString(2)
                        )
                    }catch (ex: SQLException){
                        Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this, "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show()
                }
            }
            catch (ex: SQLException){
                Toast.makeText(this, "${ex.message}", Toast.LENGTH_SHORT).show()
                Log.d("Error", ex.message.toString())
            }

        }
    }

    /**
     * Permite mantener la sesión iniciada
     */
    private fun checkUserValues(){
        if(prefs.getUsername().isNotEmpty()){
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
}