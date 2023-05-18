package com.example.h2oasis

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.example.h2oasis.Models.Cisterna
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.sql.PreparedStatement
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ActivityMeasurement : AppCompatActivity() {
    private var sqlConnection = SQLConnection()
    private lateinit var lineChart: LineChart
    private lateinit var spinner: Spinner
    private lateinit var empty_view: TextView
    private var idSelectedCisterna: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)

        lineChart = findViewById(R.id.chart)
        lineChart.setNoDataText("Seleccione una cisterna")
        lineChart.setDrawBorders(true)
        lineChart.setBorderColor(Color.CYAN)
        lineChart.description.setEnabled(false)
        lineChart.setDrawGridBackground(true)
        lineChart.isAutoScaleMinMaxEnabled = true

        var chipGroup: ChipGroup = findViewById(R.id.chipGroup)

        chipGroup.check(R.id.chip_weekly)

        empty_view= findViewById(R.id.empty_view)

        spinner = findViewById(R.id.spinner)
        configureSpinner()

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById(checkedId)
            when(chip?.text.toString()){
                getString(R.string.daily) -> loadGraphData(ChronoUnit.HOURS, "24", "hour", 24, 4f, 25) // Para el caso diario
                getString(R.string.weekly) -> loadGraphData(ChronoUnit.DAYS, "7", "day", 7, 1f, 8) // Para el caso semanal
                getString(R.string.monthly) -> loadGraphData(ChronoUnit.WEEKS, "1", "month", 4, 1f, 5) // Para el caso mensual
            }
        }
    }

    private fun loadGraphData(timeUnit: ChronoUnit, subtractValue: String, subtractUnit: String, maxValue: Int, granularity: Float, labelCount: Int) {
        val entries = ArrayList<Entry>()

        if(idSelectedCisterna > 0){
        try {
            val sqlGetMeasurements: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement(
                    "SELECT * FROM mediciones WHERE idCisterna = ? AND fechaHora >= DATEADD($subtractUnit, -$subtractValue, GETDATE()) ORDER BY fechaHora;"
                )!!
            sqlGetMeasurements.setInt(1, idSelectedCisterna)
            val measurements = sqlGetMeasurements.executeQuery()

            while (measurements.next()) {
                val timestamp = measurements.getTimestamp("fechaHora")
                val instant = Instant.ofEpochMilli(timestamp.getTime())
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val now = LocalDateTime.now()
                var time = maxValue - Math.abs(timeUnit.between(dateTime, now)).toInt()
                if (time < 0) time = 0
                if (time > maxValue) time = maxValue
                var quantity = measurements.getFloat("nivelAgua")
                entries.add(Entry(time.toFloat(), quantity))
            }

            var dataSet = LineDataSet(entries, "Porcentaje Nivel Tinaco")

            var lineData = LineData(dataSet)
            dataSet.valueTextSize = 10f
            dataSet.lineWidth = 3f
            dataSet.setDrawCircleHole(false)
            dataSet.setCircleColor(Color.BLUE)
            lineChart.data = lineData

            // Configuración del eje X
            val xAxis: XAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = granularity
            xAxis.labelCount = labelCount
            xAxis.axisMinimum = 0f
            xAxis.axisMaximum = maxValue.toFloat()
            xAxis.textSize = 20f

            val typedValue = TypedValue()
            val theme = ContextThemeWrapper(this, R.style.Base_Theme_H2Oasis).theme
            theme.resolveAttribute(com.google.android.material.R.attr.colorOnBackground, typedValue, true)
            val color = ContextCompat.getColor(this, typedValue.resourceId)
            xAxis.textColor = color
            xAxis.isGranularityEnabled = true
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return (maxValue - value.toInt()).toString()
                }
            }

            // Configuración del Eje Y
            val yAxisLeft: YAxis = lineChart.axisLeft
            yAxisLeft.textColor = color
            yAxisLeft.textSize = 20f

            val yAxisRight: YAxis = lineChart.axisRight
            yAxisRight.isEnabled = false

            lineChart.invalidate() // refresh

        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
        }
        else{
            Toast.makeText(this, "Seleccione una cisterna", Toast.LENGTH_LONG).show()
        }

    }

    private fun configureSpinner(){

        // Obtener lista de cisternas que traiga el Id y Nombre Corto
        val cisternas = createWaterTankList()
        val adapter: ArrayAdapter<Cisterna> = ArrayAdapter<Cisterna>(
            this,
            android.R.layout.simple_spinner_item,
            cisternas
        )
        // Aplica el adaptador al spinner.
        spinner.adapter = adapter
        // Especifica el layout que utilizarás cuando la lista de opciones aparezca.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        if(cisternas.isNotEmpty()){
            spinner.setSelection(0)
            idSelectedCisterna = cisternas[0].idCisterna
            spinner.visibility = View.VISIBLE
            empty_view.visibility = View.GONE
        }
        else{
            spinner.visibility = View.GONE
            empty_view.visibility = View.VISIBLE
        }
        // Configura la selección predeterminada para el spinner


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // Un elemento ha sido seleccionado. Puedes recuperar la selección utilizando
                val selectedCisterna = parent.getItemAtPosition(pos) as Cisterna
                idSelectedCisterna = selectedCisterna.idCisterna
                loadGraphData(ChronoUnit.DAYS, "7", "day", 7, 1f, 8) // Para el caso semanal
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Algunas veces no necesitas manejar nada aquí.
            }
        }
    }

    private fun createWaterTankList(): ArrayList<Cisterna>  {
        val cisternas = ArrayList<Cisterna>()
        try {

            // Obtiene una lista de las Cisternas registradas con el idUsuario
            val sqlGetWaterTanks: PreparedStatement =
                sqlConnection.dbConn()
                    ?.prepareStatement(
                        "SELECT DISTINCT c.*\n" +
                                "FROM cisternas c\n" +
                                "INNER JOIN usuarioCisterna uc ON c.idCisterna = uc.idCisterna\n" +
                                "INNER JOIN usuarios u ON uc.idUsuario = u.idUsuario\n" +
                                "WHERE u.idUsuario = ? AND c.habilitado = 1\n" +
                                "ORDER BY idCisterna")!!
            sqlGetWaterTanks.setString(1, prefs.getId())
            val waterTankList = sqlGetWaterTanks.executeQuery()

            // Genera una lista del modelo Cisterna con los registros obtenidos
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
        }
        catch (ex: java.lang.Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
        return cisternas
    }



}
