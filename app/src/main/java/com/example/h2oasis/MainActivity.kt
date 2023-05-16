package com.example.h2oasis

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.h2oasis.databinding.ActivityMainBinding
import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.app.AlarmManager
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CHANNEL_ID = "my_channel"
        private const val CHANNEL_NAME = "My Channel"
        private const val NOTIFICATION_ID = 1
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var broadcastReceiver: BroadcastReceiver

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }

        // Configurar las notificaciones
        setupNotifications()
        registerNotificationReceiver()
    }

    private fun setupNotifications() {
        // Crear el canal de notificación (solo necesario en Android Oreo y superior)
        createNotificationChannel()

        // Obtener una instancia del NotificationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Configurar la repetición de las notificaciones
        configureNotificationRepeating()

        // Registrar el BroadcastReceiver para recibir las acciones de la notificación
        registerNotificationReceiver()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun configureNotificationRepeating() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 10) // Hora de la primera notificación (ejemplo: 8 AM)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Programar la primera notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

        // Sumar 12 horas para la segunda notificación
        calendar.add(Calendar.HOUR, 10)

        // Programar la segunda notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }




    private fun registerNotificationReceiver() {
        val filter = IntentFilter()
        filter.addAction("ACTION_NOTIFICATION")
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Aquí puedes manejar la acción de la notificación
                val notificationIntent = Intent(this@MainActivity, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    this@MainActivity,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Construir la notificación
                val notification = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
                    .setContentTitle("Recordatorio")
                    .setContentText("Ya checaste el status de tu tinaco")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                // Mostrar la notificación
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
        registerReceiver(broadcastReceiver, filter)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_NOTIFICATION") {
                // Aquí puedes manejar la acción de la notificación
                val notificationIntent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Construir la notificación
                val notification = NotificationCompat.Builder(context!!, CHANNEL_ID)
                    .setContentTitle("Recordatorio")
                    .setContentText("Ya checaste el status de tu tinaco")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                // Mostrar la notificación
                val notificationManager =
                    context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

}
