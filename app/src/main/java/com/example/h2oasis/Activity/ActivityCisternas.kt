package com.example.h2oasis.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.h2oasis.GridViewModalWaterTank
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.example.h2oasis.Models.Cisterna
import com.example.h2oasis.R
import com.example.h2oasis.SQLConnection
import com.gtappdevelopers.kotlingfgproject.GridRVAdapter
import java.sql.PreparedStatement
import java.sql.SQLException


class ActivityCisternas : AppCompatActivity() {

    private var sqlConnection = SQLConnection()

    // on below line we are creating
    // variables for grid view and course list
    lateinit var courseGRV: GridView
    lateinit var courseList: List<GridViewModalWaterTank>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cisternas)

        val btn_addCisternas: Button = findViewById(R.id.btn_addWaterTank)
        btn_addCisternas.setOnClickListener{ openAddWaterTank(null) }

        // initializing variables of grid view with their ids.
        courseGRV = findViewById(R.id.idGRV)
        courseList = ArrayList<GridViewModalWaterTank>()

        // on below line we are adding data to
        // our course list with image and course name.

        createWaterTankList()

        // on below line we are initializing our course adapter
        // and passing course list and context.
        val courseAdapter = GridRVAdapter(courseList = courseList, this@ActivityCisternas)

        // on below line we are setting adapter to our grid view.
        courseGRV.adapter = courseAdapter

        // on below line we are adding on item
        // click listener for our grid view.
        courseGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // inside on click method we are simply displaying
            // a toast message with course name.

            openAddWaterTank(courseList[position].courseId)
            Toast.makeText(
                applicationContext, courseList[position].courseName+ " seleccionada",
                Toast.LENGTH_SHORT
            ).show()
        }

        courseGRV.onItemLongClickListener = AdapterView.OnItemLongClickListener{ _, _, position, _ ->
            showConfirmation(courseList[position].courseId)
//            Toast.makeText(
//                applicationContext, courseList[position].courseId.toString() + " deleted",
//                Toast.LENGTH_SHORT
//            ).show()
            true
        }
    }

    /**
     * Obtiene la lista que permite desplegar el RecyclerView con todas las cisternas
     */
    private fun createWaterTankList() {
        try {

            // Obtiene una lista de las Cisternas registradas con el idUsuario
            val sqlGetWaterTanks: PreparedStatement =
                sqlConnection.dbConn()
                    ?.prepareStatement(
                        "SELECT DISTINCT c.*\n" +
                                "FROM cisternas c\n" +
                                "INNER JOIN usuarioCisterna uc ON c.idCisterna = uc.idCisterna\n" +
                                "INNER JOIN usuarios u ON uc.idUsuario = u.idUsuario\n" +
                                "WHERE u.idUsuario = ? AND c.habilitado = 1;")!!
            sqlGetWaterTanks.setString(1, prefs.getId())
            val waterTankList = sqlGetWaterTanks.executeQuery()

            // Genera una lista del modelo Cisterna con los registros obtenidos
            val cisternas = ArrayList<Cisterna>()
            while (waterTankList.next()){
                var cisterna = Cisterna(
                    waterTankList.getString(1).toInt(),
                    waterTankList.getString(2),
                    waterTankList.getString(3).toInt(),
                    waterTankList.getString(4),
                    waterTankList.getString(5),
                    waterTankList.getBoolean(6)
                )
                cisternas.add(cisterna)
            }

            // Inyecta cada registro de las cisternas dentro del DataGridView
            for(cisterna in cisternas){
                courseList = courseList + GridViewModalWaterTank(cisterna.idCisterna, cisterna.nombreCorto, R.drawable.baseline_water_drop_24)
            }
        }
        catch (ex: java.lang.Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

    }


    /**
     * Eliminar la cisterna de manera lógica en la BD
     * @param idWaterTank a eliminar
     */
    private fun deleteWaterTank(idWaterTank: Int){
        try {
            val editCisternaSQL: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement("UPDATE cisternas SET habilitado = 0 WHERE idCisterna = ?")!!
            editCisternaSQL.setInt(1, idWaterTank!!)
            editCisternaSQL.executeUpdate()

            Toast.makeText(this, "Cisterna eliminada exitosamente", Toast.LENGTH_SHORT).show()

            // Regresa a la pantalla principal
            openCisternas()
        } catch (ex: SQLException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(
            applicationContext, "Cisterna eliminada con éxito",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Muestra un diálogo para confirmar la eliminación de la cisterna
     * @param idWaterTank a eliminar
     */
    private fun showConfirmation(idWaterTank: Int?): Boolean{
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Realmente quieres eliminar la cisterna?")
            .setIcon(R.drawable.baseline_delete_24)
            .setPositiveButton(R.string.delete) { _, _ ->
                (deleteWaterTank(idWaterTank!!))
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
            }.show()
        return false
    }

    /**
     * Abre la actividad de Cisternas
     */
    private fun openCisternas(){
        var intent = Intent(this, ActivityCisternas::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Abre el formulario de las cisternas
     * @param idWaterTank si es seleccionado para edición o nulo si es inserción
     */
    private fun openAddWaterTank(idWaterTank: Int?){
        var intent = Intent(this, ActivityFormularioCisterna::class.java)
        intent.putExtra("idCisterna", idWaterTank)
        startActivity(intent)
    }

}