package com.example.h2oasis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.h2oasis.Activity.ActivityMainMenu
import java.util.*
import java.util.concurrent.TimeUnit

class Worknoti (context: Context,workerParameters: WorkerParameters): Worker(context,workerParameters) {
    override fun doWork(): Result {
        val titulo = inputData.getString("Titulo")
        val detalle = inputData.getString("Detalle")
        val id = inputData.getLong("idnoti", 0).toInt()
        oreo(titulo,detalle)
        return Result.success()
    }

    companion object{
        fun GuardarNoti(duracion:Long, data: Data?, tag:String?){
            val noti = OneTimeWorkRequest.Builder(Worknoti::class.java)
                .setInitialDelay(duracion,TimeUnit.MILLISECONDS).addTag(tag!!)
                .setInputData(data!!).build()
            val instance = WorkManager.getInstance()
            instance.enqueue(noti)
        }
    }

    private fun oreo(t: String?, d: String?) {
        val id = "message"
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, id)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nc = NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH)
            nc.description = "Notificacion FCM"
            nc.setShowBadge(true)
            nm.createNotificationChannel(nc)
        }
        val intent = Intent(applicationContext,ActivityMainMenu::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_ONE_SHOT
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, pendingIntentFlags)

        builder.setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(t)
            .setTicker("Nueva notificacion")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(d)
            .setContentIntent(pendingIntent)
            .setContentInfo("Nuevo")
        val random = Random()
        val idNotify = random.nextInt(8000)
        assert(nm != null)
        nm.notify(idNotify, builder.build())
    }
}