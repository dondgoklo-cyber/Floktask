package com.taskmanager.data.geofence

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        val transition = event.geofenceTransition
        if (transition != Geofence.GEOFENCE_TRANSITION_ENTER) return

        val triggered = event.triggeringGeofences ?: return
        for (geofence in triggered) {
            val title = "Location reminder"
            val text = "You have arrived near: ${geofence.requestId}"
            showNotification(context, title, text)
        }
    }

    private fun showNotification(context: Context, title: String, text: String) {
        val channelId = CHANNEL_ID
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        runCatching {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "location_reminders"

        fun pendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GeofenceReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
