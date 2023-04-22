package com.example.h2oasis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import com.example.h2oasis.H2Oasis.Companion.prefs
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
            Toast.makeText(
                applicationContext, courseList[position].courseName + " selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createWaterTankList() {
        try {
            val sqlGetWaterTanks: PreparedStatement =
                sqlConnection.dbConn()
                    ?.prepareStatement(
                        "SELECT c.*\n" +
                                "FROM cisternas c\n" +
                                "INNER JOIN usuarioCisterna uc ON c.idCisterna = uc.idCisterna\n" +
                                "INNER JOIN usuarios u ON uc.idUsuario = u.idUsuario\n" +
                                "WHERE u.idUsuario = ?;")!!
            sqlGetWaterTanks.setString(1, prefs.getId())
            val waterTankList = sqlGetWaterTanks.executeQuery()
            val cisternas = ArrayList<Cisterna>()
            while (waterTankList.next()){
                var cisterna = Cisterna(
                    waterTankList.getString(1).toInt(),
                    waterTankList.getString(2),
                    waterTankList.getString(3).toInt(),
                    waterTankList.getString(4),
                    waterTankList.getString(5)
                )
                cisternas.add(cisterna)
            }

            for(cisterna in cisternas){
                courseList = courseList + GridViewModalWaterTank(cisterna.nombreCorto, R.drawable.baseline_circle_24)
            }
        }
        catch (ex: java.lang.Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

    }
}