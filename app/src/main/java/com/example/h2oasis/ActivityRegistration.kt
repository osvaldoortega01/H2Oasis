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
import java.time.LocalDate
import java.time.LocalDateTime

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

    private fun registerUser(){
        val etcompletename: TextInputEditText = findViewById(R.id.tiet_CompleteName)
        val etusername: TextInputEditText = findViewById(R.id.tiet_Username)
        val etpassword: TextInputEditText = findViewById(R.id.tiet_Password)
        val etconfirmpassword: TextInputEditText = findViewById(R.id.tiet_ConfirmPassword)
        val etemailaddress: TextInputEditText = findViewById(R.id.tiet_EmailAddress)
        val fecha = LocalDateTime.now()

        val pas1 = etpassword.text.toString()
        val cpas1 = etconfirmpassword.text.toString()
        try {
            // Se encapsula todos los peticiones SQL con un try catch, para que no crasheé la app
            val norepeatuser: PreparedStatement = sqlConnection.dbConn()?.prepareStatement("SELECT idUsuario FROM usuarios WHERE usuario = ?")!!
            norepeatuser.setString(1, etusername.text.toString())
            val verifuser: ResultSet = norepeatuser.executeQuery()

            if(!verifuser.next() ){
                if (pas1==cpas1){
                    if (TextUtils.isEmpty(etusername.text))
                    {
                        etusername.setError("El nombre de usuario es requerido")
                    }
                    else if(TextUtils.isEmpty(etcompletename.text))
                    {
                        etcompletename.setError("El nombre completo es requerido")
                    }
                    else if (TextUtils.isEmpty(etpassword.text))
                    {
                        etpassword.setError("La contraseña es requerida")
                    }
                    else if (TextUtils.isEmpty(etemailaddress.text))
                    {
                        etemailaddress.setError("El correo es requerido")
                    }
                    else {

                        try {
                            val nuevoUsuario: PreparedStatement = sqlConnection.dbConn()
                                ?.prepareStatement("INSERT INTO usuarios values (?,?,?,?,?)")!!
                            nuevoUsuario.setString(1, etcompletename.text.toString())
                            nuevoUsuario.setString(2, etusername.text.toString())
                            nuevoUsuario.setString(3, etpassword.text.toString())
                            nuevoUsuario.setString(4, etemailaddress.text.toString())
                            nuevoUsuario.setString(5, fecha.toString())
                            nuevoUsuario.executeUpdate()
                            Toast.makeText(this, "Cuenta Creada Existosamente", Toast.LENGTH_SHORT)
                                .show()
                            try {
                                val getidusuario: PreparedStatement = sqlConnection.dbConn()
                                    ?.prepareStatement("SELECT idUsuario FROM usuarios WHERE usuario = ?")!!
                                getidusuario.setString(1, etusername.text.toString())
                                val iduser: ResultSet = getidusuario.executeQuery()
                                iduser.next()
                                fun accessToDetail() {
                                    if (etusername.text.toString()
                                            .isNotEmpty() && etpassword.text.toString().isNotEmpty()
                                    ) {
                                        prefs.saveUsername(etpassword.text.toString())
                                        prefs.savePassword(etusername.text.toString())
                                        prefs.saveId(iduser.getString(1))
                                        openMainMenu()
                                    } else {

                                    }
                                }
                                accessToDetail()
                            } catch (ex: SQLException) {
                                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                            }
                        } catch (ex: SQLException) {
                            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_LONG).show()
            }

        }
        catch (ex: SQLException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    fun openMainMenu() {
        var intent = Intent(this, ActivityMainDRAFT::class.java)
        startActivity(intent)
    }

}