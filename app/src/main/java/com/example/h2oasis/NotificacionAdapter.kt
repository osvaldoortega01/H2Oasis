package com.example.h2oasis
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.h2oasis.Activity.ActivityNotifications
import com.example.h2oasis.Models.Notificacion
import java.sql.PreparedStatement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
class NotificacionAdapter(
    private val context: Context,
    private val unseenNotifications: List<Notificacion>,
    private val seenNotifications: List<Notificacion>
) : RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder>() {

    private val allNotifications = unseenNotifications + seenNotifications // Unificar las dos listas en una sola
    private var  sqlConnection = SQLConnection()

    inner class NotificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val encabezado: TextView = itemView.findViewById(R.id.tv_encabezado)
        val mensaje: TextView = itemView.findViewById(R.id.tv_mensaje)
        val nombreCortoCisterna: TextView = itemView.findViewById(R.id.tv_nombreCisterna)
        val fechaHora: TextView = itemView.findViewById(R.id.tv_fecha_hora)
        val imgCheck: ImageView = itemView.findViewById(R.id.imgCheck)
        val layoutCard: CardView = itemView.findViewById(R.id.card_layout) // Agrega el layout principal del CardView

        fun bind(notificacion: Notificacion) {
            encabezado.text = notificacion.encabezado
            mensaje.text = notificacion.mensaje
            fechaHora.text = notificacion.fechaHora.toString() // convierte DateTime a String según tus necesidades
            nombreCortoCisterna.text = notificacion.nombreCortoCisterna

            // Controla la visibilidad del botón y el color de fondo según el estado de la notificación
            if (notificacion.notificacionVista) {
                imgCheck.visibility = View.GONE
                val typedValue = TypedValue()
                context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurfaceVariant, typedValue, true)
                val colorSurfaceVariant = typedValue.data
                layoutCard.setCardBackgroundColor(colorSurfaceVariant)  // Puedes reemplazar este valor por el color de tu elección
            } else {
                imgCheck.visibility = View.VISIBLE
                val typedValue = TypedValue()
                context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
                val colorSurface = typedValue.data
                layoutCard.setCardBackgroundColor(colorSurface) // Puedes reemplazar este valor por el color de tu elección
                imgCheck.setOnClickListener {
                    markNotificationAsRead(notificacion.idNotificacion)
                    (context as ActivityNotifications).loadNotifications()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacion, parent, false)
        return NotificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        val notificacion = allNotifications[position]
        holder.bind(notificacion)
    }

    override fun getItemCount() = allNotifications.size

    private fun markNotificationAsRead(idNotificacion: Int) {
        try {
            val sqlUpdateNotification: PreparedStatement = sqlConnection.dbConn()
                ?.prepareStatement(
                    "UPDATE notificaciones SET notificacionVista = ? WHERE idNotificacion = ?;"
                )!!
            sqlUpdateNotification.setBoolean(1, true) // marca la notificación como leída
            sqlUpdateNotification.setInt(2, idNotificacion)

            val rowsAffected = sqlUpdateNotification.executeUpdate()

            if (rowsAffected > 0) {
                Toast.makeText(context, "Notificación marcada como leída", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error al marcar la notificación como leída", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }
}
