package com.example.todo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService

class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "sdfsdf", Toast.LENGTH_SHORT).show()

        val data = intent.getStringExtra("data")
        val arr = data.split(";")

        val name = arr[0]
        val date = arr[1]
        val priority = arr[2].toInt()
        val type = arr[3]

        val task = Task(name, date, priority, type, false)

        val CHANNEL_ID = "mojkanal"

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
        val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val draw = when (task.type) {
            "praca" -> R.drawable.ic_work_black_24dp
            "uczelnia" -> R.drawable.ic_local_library_black_24dp
            "zakupy" -> R.drawable.ic_local_grocery_store_black_24dp
            "auto" -> R.drawable.ic_directions_car_black_24dp
            else -> R.drawable.notification_icon_background
        }

        val builder = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle("Przypomnienie - ${task.type}")
            .setContentText("${task.name} - to już dziś, nie zapomnij!")
            .setAutoCancel(true)
            .setSmallIcon(draw)

        val myintent = Intent(context, MainActivity::class.java)
        val pending = PendingIntent.getActivity(context, 0, myintent, 0)
        builder.setContentIntent(pending)

        val notification = builder.build()
        manager.notify(1234, notification)


//        val notification = MainActivity.notification
//        val id = intent.getIntExtra("notification-id", 0)
//        notificationManager.notify(id, notification)
//        Log.i("pub", "noriffyyysdfdsfsadf")
    }

}
