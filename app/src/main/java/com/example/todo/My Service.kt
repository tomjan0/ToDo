package com.example.todo

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import java.time.LocalDate

class MyService : Service() {

    private val TAG = "MyService"
    var end = false

    override fun onCreate() {
        Log.d("am2019", "OnCreate")

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("am2019", "OnStartCommand")


        if (intent != null) {
            var value = intent.getStringExtra("key")
            Log.d("am2019", "OnStartCommand: $value")
        }

        val db = Room.databaseBuilder(this, AppDatabase::class.java, "task.db").build()

        var tasks: List<Task> = listOf(Task("", "", 0, "", true))
        AsyncTask.execute {
            tasks = db.taskDao().getAll()
        }

        Thread {
            for (i in 1..100) {

                if(end) {
                    break
                }
//            for (i in 1..5) {
                Log.d(TAG, "Loop: $i")
                Thread.sleep(5000)
//            }
                val currentDate = LocalDate.now()
                val selectedYear = currentDate.year.toString()
                var selectedMonth = currentDate.monthValue.toString()
                var selectedDay = currentDate.dayOfMonth.toString()

                if (selectedDay.length == 1) {
                    selectedDay = "0$selectedDay"
                }
                if (selectedMonth.length == 1) {
                    selectedMonth = "0$selectedMonth"
                }

                val date = "$selectedYear-$selectedMonth-$selectedDay"


                for (task in tasks) {
                    Log.i(TAG, task.name)
                    if (!task.notified) {
                        Log.i(TAG, "checking date")

                        if (task.date == date) {
                            Log.i(TAG, "notify")
                            makeNotification(task)
                            task.notified = true
                            AsyncTask.execute {
                                db.taskDao().updateTasks(task)
                            }
                        }
                    }

                }
            }

        }.start()


        return START_STICKY
    }

    private val CHANNEL_ID = "mojkanal"

    private fun makeNotification(task: Task) {

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
        val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val draw = when (task.type) {
            "praca" -> R.drawable.ic_work_black_24dp
            "uczelnia" -> R.drawable.ic_local_library_black_24dp
            "zakupy" -> R.drawable.ic_local_grocery_store_black_24dp
            "auto" -> R.drawable.ic_directions_car_black_24dp
            else -> R.drawable.notification_icon_background
        }

        val builder = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Przypomnienie - ${task.type}")
            .setContentText("${task.name} - to już dziś, nie zapomnij!")
            .setAutoCancel(true)
            .setSmallIcon(draw)

        val myintent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(this, 0, myintent, 0)
        builder.setContentIntent(pending)

        val notification = builder.build()
        manager.notify(1234, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("am2019", "OnDestroy")

        end = true

        val broadcastIntent = Intent(this, Restarter::class.java)
        sendBroadcast(broadcastIntent)
    }

}
