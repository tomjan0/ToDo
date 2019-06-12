package com.example.todo

import android.app.*
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import android.app.ActivityManager
import android.content.Context
import android.icu.util.Calendar
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_NEW_TASK = 1
        val taskList = ArrayList<Task>()
        var notification = Notification()

    }

    lateinit var mServiceIntent : Intent
    lateinit var mService : MyService
    private lateinit var taskAdapter: TaskAdapter
    private var sortType = "name"
    private lateinit var db: AppDatabase

//    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
//        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.name == service.service.className) {
//                Log.i("isMyServiceRunning?", true.toString() + "")
//                return true
//            }
//        }
//        Log.i("isMyServiceRunning?", false.toString() + "")
//        return false
//    }

    override fun onDestroy() {
//        stopService(mServiceIntent)
//        Log.i("MainActivity", "onDestroy!")
        super.onDestroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        mService = MyService()
//        mServiceIntent = Intent(this, mService.javaClass)

//        if(!isMyServiceRunning(mService.javaClass)){
//            startService(mServiceIntent)
//        }

        db = Room.databaseBuilder(this, AppDatabase::class.java, "task.db").build()
        taskList.clear()
        AsyncTask.execute {
            for (task in db.taskDao().getAll()) {
                taskList.add(task)
            }
            sortTasks()
        }

        sortSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    when (parent.getItemAtPosition(position).toString()) {
                        "nazwy" -> sortType = "name"
                        "daty" -> sortType = "date"
                        "priorytetu" -> sortType = "priority"
                        "typ" -> sortType = "type"
                    }
                    sortTasks()
                }
            }


        }

        taskAdapter = TaskAdapter(taskList, this)
        taskContainer.adapter = taskAdapter
        taskContainer.layoutManager = LinearLayoutManager(this)



        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search()
            }

        })

    }

    fun itemClick(position: Int) {

        val task = taskList[position]
        AlertDialog.Builder(this).setPositiveButton("Tak") { _, _ ->
            Toast.makeText(this, "Usunięto \"${task.name}\"", Toast.LENGTH_SHORT).show()
            AsyncTask.execute {
                db.taskDao().delete(task)
            }
            taskList.removeAt(position)
            taskAdapter.notifyDataSetChanged()
        }.setNegativeButton("Nie", null).setMessage("Czy chcesz usunąć zadanie \n\"${task.name}\" ?")
            .create().show()
    }


    fun search() {
        val pattern = searchInput.text.toString()
        SearchTask(this, pattern, db).execute()
    }


    fun addTask(view: View) {
        val myIntent = Intent(this, AddTaskActivity::class.java)
        startActivityForResult(myIntent, REQUEST_NEW_TASK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_NEW_TASK -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        val name = data.getStringExtra("name")
                        val date = data.getStringExtra("date")
                        val priority = data.getIntExtra("priority", 1)
                        val type = data.getStringExtra("type")

                        val task = Task(name, date, priority, type, false)

                        val CHANNEL_ID = "mojkanal"

                        val data = "$name;$date;$priority;$type"

                        val draw = when (task.type) {
                            "praca" -> R.drawable.ic_work_black_24dp
                            "uczelnia" -> R.drawable.ic_local_library_black_24dp
                            "zakupy" -> R.drawable.ic_local_grocery_store_black_24dp
                            "auto" -> R.drawable.ic_directions_car_black_24dp
                            else -> R.drawable.notification_icon_background
                        }

                        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
                        val notification = Notification.Builder(this, CHANNEL_ID)
                            .setContentTitle("Przypomnienie - ${task.type}")
                            .setContentText("${task.name} - to już dziś, nie zapomnij!")
                            .setAutoCancel(true)
                            .setSmallIcon(draw)
                            .build()




                        val notificationIntent = Intent(this, NotificationPublisher::class.java)
                        notificationIntent.putExtra("notification-id", 1)
                        notificationIntent.putExtra("notification", notification)
                        notificationIntent.putExtra("data", data)
                        val pendingIntent = PendingIntent.getBroadcast(this, 0 ,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.SECOND, 5)
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

                        AsyncTask.execute {
                            db.taskDao().insertAll(task)
                        }

                        taskList.add(task)
                        sortTasks()
                    }
                    taskAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun sortTasks() {
        taskList.sortBy { it.name }
        when (sortType) {
            "date" -> taskList.sortBy { it.date }
            "priority" -> taskList.sortBy { it.priority }
            "type" -> taskList.sortBy { it.priority }
        }
        taskAdapter.notifyDataSetChanged()
    }


}