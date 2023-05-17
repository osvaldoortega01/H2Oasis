package com.example.h2oasis

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.h2oasis.H2Oasis.Companion.prefs
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import net.sourceforge.jtds.jdbc.DateTime
import java.sql.PreparedStatement
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ActivityMeasurement : AppCompatActivity() {
    private var sqlConnection = SQLConnection()
    private lateinit var lineChart: LineChart
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement)

        lineChart = findViewById(R.id.chart)
        lineChart.setNoDataText("Seleccione un tinaco")
        lineChart.setDrawBorders(true)
        lineChart.setBorderColor(Color.CYAN)
        lineChart.description.setEnabled(false)
        lineChart.setDrawGridBackground(true)

        spinner = findViewById(R.id.spinner)
        configureSpinner()
    }

    private fun configureSpinner(){
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.filter_time,
            android.R.layout.simple_spinner_item
        )
        // Aplica el adaptador al spinner.
        spinner.adapter = adapter

        // Especifica el layout que utilizarás cuando la lista de opciones aparezca.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Obtiene la posición del ítem "Semanal" en el array
        val defaultPosition = adapter.getPosition("Semanal")

        // Configura la selección predeterminada para el spinner
        spinner.setSelection(defaultPosition)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // Un elemento ha sido seleccionado. Puedes recuperar la selección utilizando
                val selected = parent.getItemAtPosition(pos).toString()
                when(selected){
                    "Diario" -> loadGraphData(ChronoUnit.HOURS, "24", "hour", 24, 4f, 25) // Para el caso diario
                    "Semanal" -> loadGraphData(ChronoUnit.DAYS, "7", "day", 7, 1f, 8) // Para el caso semanal
                    "Mensual" -> loadGraphData(ChronoUnit.WEEKS, "1", "month", 4, 1f, 5) // Para el caso mensual

                }
                Toast.makeText(this@ActivityMeasurement, selected, Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Algunas veces no necesitas manejar nada aquí.
            }
        }
    }

    private fun loadGraphData(timeUnit: ChronoUnit, subtractValue: String, subtractUnit: String, maxValue: Int, granularity: Float, labelCount: Int) {
        val entries = ArrayList<Entry>()

        try {
            val sqlGetMeasurements: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement(
                    "SELECT * FROM mediciones WHERE idCisterna = ? AND fechaHora >= DATEADD($subtractUnit, -$subtractValue, GETDATE()) ORDER BY fechaHora;"
                )!!
            sqlGetMeasurements.setInt(1, 2)
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



}
