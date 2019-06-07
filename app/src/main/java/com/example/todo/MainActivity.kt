package com.example.todo

import android.app.Activity
import android.app.AlertDialog
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

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_NEW_TASK = 1
        val taskList = ArrayList<Task>()


    }

    private lateinit var taskAdapter: TaskAdapter
    private var sortType = "name"
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(this, AppDatabase::class.java, "task.db").build()

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

                        val newTask = Task(name, date, priority, type)

                        AsyncTask.execute {
                            db.taskDao().insertAll(newTask)
                        }

                        taskList.add(newTask)
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