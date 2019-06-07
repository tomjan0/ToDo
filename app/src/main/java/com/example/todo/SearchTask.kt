package com.example.todo

import android.content.Context
import android.os.AsyncTask
import com.example.todo.MainActivity.Companion.taskList

class SearchTask(val context: Context, val pattern: String, val db: AppDatabase) : AsyncTask<Void, Void, Void>() {


    override fun doInBackground(vararg params: Void?): Void? {
        if (pattern.isBlank()) {
            taskList.clear()

            for (task in db.taskDao().getAll()) {
                taskList.add(task)
            }

        } else {

            taskList.clear()
            for (task in db.taskDao().findByName("%$pattern%")) {
                taskList.add(task)
            }

        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        (context as MainActivity).sortTasks()
    }

}