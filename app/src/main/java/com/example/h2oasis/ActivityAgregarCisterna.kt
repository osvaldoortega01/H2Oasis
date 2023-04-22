package com.example.h2oasis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import java.sql.PreparedStatement
import java.sql.SQLException


class ActivityAgregarCisterna : AppCompatActivity() {
    private var sqlConnection = SQLConnection()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_cisterna)
        val btn_AddWaterTank: Button = findViewById(R.id.btn_addWaterTank)
        btn_AddWaterTank.setOnClickListener{ addWaterTank() }
    }

    private fun addWaterTank(){
        val et_ShortName: TextInputEditText = findViewById(R.id.tiet_ShortName)
        val et_Description: TextInputEditText = findViewById(R.id.tiet_Description)
        val et_Capacity: TextInputEditText = findViewById(R.id.tiet_Capacity)

        try {
            val nuevaCisternaSQL : PreparedStatement = sqlConnection.dbConn()
                    ?.prepareStatement("INSERT INTO cisternas  VALUES(?,?,?,?)")!!
            nuevaCisternaSQL.setString(1, et_Capacity.text.toString())
            nuevaCisternaSQL.setString(2, "1")
            nuevaCisternaSQL.setString(3, et_ShortName.text.toString())
            nuevaCisternaSQL.setString(4, et_Description.text.toString())
            nuevaCisternaSQL.executeQuery()

            Toast.makeText(this, "Cisterna agregada exitosamente", Toast.LENGTH_SHORT).show()
        }
        catch (ex: SQLException){
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()

        }
    }
}