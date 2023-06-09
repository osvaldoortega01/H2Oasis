package com.example.h2oasis.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.room.Transaction
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.example.h2oasis.R
import com.example.h2oasis.SQLConnection
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement


class ActivityFormularioCisterna : AppCompatActivity() {
    private var sqlConnection = SQLConnection()
    private var esEditar = false

    private lateinit var etShortName: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etCapacity: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cisterna)

        val bundle = intent.extras
        val dato = bundle?.getInt("idCisterna")

        etShortName = findViewById(R.id.tiet_ShortName)
        etDescription = findViewById(R.id.tiet_Description)
        etCapacity = findViewById(R.id.tiet_Capacity)

        esEditar = dato != null && dato != 0

        val btn_AddWaterTank: ExtendedFloatingActionButton = findViewById(R.id.btn_addWaterTank)
        val btn_DeleteWaterTank: ExtendedFloatingActionButton = findViewById(R.id.btn_deleteWaterTank)
        if(esEditar){
            loadWaterTank(dato!!)
            btn_AddWaterTank.text = "Editar"
            btn_AddWaterTank.setIconResource(R.drawable.baseline_edit_24)
            btn_DeleteWaterTank.visibility = View.VISIBLE
        }
        btn_DeleteWaterTank.setOnClickListener{
            deleteWaterTank()
        }
        btn_AddWaterTank.setOnClickListener{
            if(esEditar){
                editWaterTank()
            }
            else{
                addWaterTank()
            }
        }
    }

    /**
     * Añade una cisterna a la BD, primero realiza la validación de si los campos no están vacíos
     * Para después crear el registro en la tabla cisternas y la relación en la tabla
     * usuarioCisterna
     */
    @Transaction
    private fun addWaterTank(){

        try {
            if(areFieldsNotEmpty(etCapacity, etShortName, etDescription)) {
                // Crea un nuevo registro en la tabla Cisternas
                val nuevaCisternaSQL: PreparedStatement = sqlConnection.dbConn()
                    ?.prepareStatement(
                        "INSERT INTO cisternas  VALUES(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS
                    )!!
                nuevaCisternaSQL.setString(1, etCapacity.text.toString())
                nuevaCisternaSQL.setInt(2, 1)
                nuevaCisternaSQL.setString(3, etShortName.text.toString())
                nuevaCisternaSQL.setString(4, etDescription.text.toString())
                nuevaCisternaSQL.setBoolean(5, true)
                nuevaCisternaSQL.executeUpdate()
                // Permite obtener el id de la nueva Cisterna agregada
                var rs: ResultSet = nuevaCisternaSQL.generatedKeys
                if (rs.next() != null) {
                    // Agrega un nuevo registro en la tabla usuarioCisterna
                    val nuevaCisternaUsuarioSQL: PreparedStatement = sqlConnection.dbConn()
                        ?.prepareStatement("INSERT INTO usuarioCisterna  VALUES(?,?)")!!
                    // Obtiene el IdUsuario de las prefs registradas
                    nuevaCisternaUsuarioSQL.setString(1, prefs.getId())
                    // Obtiene el IdCisterna obtenido de la ùltima cisterna agregada
                    nuevaCisternaUsuarioSQL.setString(2, rs.getInt(1).toString())

                    nuevaCisternaUsuarioSQL.executeUpdate()

                    Toast.makeText(this, "Cisterna agregada exitosamente", Toast.LENGTH_SHORT)
                        .show()

                    // Regresa a la pantalla principal
                    openCisternas()
                }
            }
        }
        catch (ex: SQLException){
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Elimina una cisterna mediante la variable que se pasó como intent.extra
     * Este método solo está disponible si se tiene una cisterna selecccionada (Modo Edición)
     * Solo elimina la cisterna de manera lógica
     */
    private fun deleteWaterTank(){
        val idCisterna = intent.extras?.getInt("idCisterna")
        try {
            val editCisternaSQL: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement("UPDATE cisternas SET habilitado = 0 WHERE idCisterna = ?")!!
            editCisternaSQL.setInt(1, idCisterna!!)
            editCisternaSQL.executeUpdate()

            Toast.makeText(this, "Cisterna eliminada exitosamente", Toast.LENGTH_SHORT).show()

            // Regresa a la pantalla principal
            openCisternas()
        } catch (ex: SQLException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Comprueba que los campos no estén vacíos para continuar con la función
     */
    private fun areFieldsNotEmpty(vararg fields: TextInputEditText): Boolean {
        val errorMessages = listOf(
            "La capacidad es requerido",
            "El nombre corto es requerido")

        fields.forEachIndexed { index, field ->
            if (TextUtils.isEmpty(field.text)) {
                field.error = errorMessages[index]
                return false
            }
        }
        return true
    }

    /**
     * Utiliza el intent.extra para obtener el idCisterna y utilizarlo
     * para entrar en modo edición para poner actualizar los campos
     */
    private fun editWaterTank() {
        val idCisterna = intent.extras?.getInt("idCisterna")

        try {
            if (areFieldsNotEmpty(etCapacity, etShortName)) {
                val editCisternaSQL: PreparedStatement = sqlConnection.dbConn()
                    ?.prepareStatement("UPDATE cisternas " +
                            "SET capacidad = ?, " +
                                "nombreCorto = ?, " +
                                "descripcion = ? " +
                            "WHERE idCisterna = ?")!!
                editCisternaSQL.setString(1, etCapacity.text.toString())
                editCisternaSQL.setString(2, etShortName.text.toString())
                editCisternaSQL.setString(3, etDescription.text.toString())
                editCisternaSQL.setInt(4, idCisterna!!)
                editCisternaSQL.executeUpdate()

                Toast.makeText(this, "Cisterna actualizada exitosamente", Toast.LENGTH_SHORT).show()

                // Regresa a la pantalla principal
                openCisternas()
            }
        }
        catch (ex: SQLException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Carga una cisterna, utilizado al estar en modo edición, pues como se pasa el idCisterna como parámetro
     * Lo utiliza para cargar los EditText
     */
    private fun loadWaterTank(idCisterna: Int) {

        try {
            val cisternaExistenteSQL: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement("SELECT capacidad, " +
                                        "nombreCorto, " +
                                        "descripcion " +
                                    "FROM cisternas " +
                                    "WHERE idCisterna = ?")!!
            cisternaExistenteSQL.setInt(1, idCisterna)
            val cisternaExistenteRS = cisternaExistenteSQL.executeQuery()

            if (cisternaExistenteRS.next()) {
                etCapacity.setText(cisternaExistenteRS.getString("capacidad"))
                etShortName.setText(cisternaExistenteRS.getString("nombreCorto"))
                etDescription.setText(cisternaExistenteRS.getString("descripcion"))
                esEditar = true
            }
        } catch (ex: SQLException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Abre la actividad de Cisternas
     */
    private fun openCisternas(){
        var intent = Intent(this, ActivityCisternas::class.java)
        startActivity(intent)
        finish()
    }

}