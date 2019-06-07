package com.example.todo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.widget.PopupWindowCompat
import kotlinx.android.synthetic.main.activity_add_task.*

import java.time.LocalDate


class AddTaskActivity : AppCompatActivity() {
    private lateinit var selectedYear : String
    private lateinit var selectedMonth  : String
    private lateinit var selectedDay  : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        val currentDate = LocalDate.now()
        selectedYear = currentDate.year.toString()
        selectedMonth = currentDate.monthValue.toString()
        selectedDay = currentDate.dayOfMonth.toString()



        taskType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    when (parent.getItemAtPosition(position).toString()) {
                        "praca" -> typeImage.setImageResource(R.drawable.ic_work_black_24dp)
                        "uczelnia" -> typeImage.setImageResource(R.drawable.ic_local_library_black_24dp)
                        "zakupy" -> typeImage.setImageResource(R.drawable.ic_local_grocery_store_black_24dp)
                        "auto" -> typeImage.setImageResource(R.drawable.ic_directions_car_black_24dp)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedYear = "$year"
            selectedMonth = "${month+1}"
            selectedDay = "$dayOfMonth"
        }

        taskName.requestFocus()
    }

    fun save(view: View) {
        if(taskName.text.isEmpty()) {
            Toast.makeText(this,"Wpisz nazwę zadania!",Toast.LENGTH_SHORT).show()
            return
        }


        val retIntent = Intent()
        retIntent.putExtra("name", taskName.text.toString())
        if(selectedDay.length == 1) {
            selectedDay = "0$selectedDay"
        }
        if(selectedMonth.length == 1){
            selectedMonth = "0$selectedMonth"
        }
        retIntent.putExtra("date", "$selectedYear-$selectedMonth-$selectedDay")
        val pri = if(lowPriority.isChecked) {
            0
        } else {
            if (normalPriority.isChecked) {
                1
            } else {
                2
            }
        }
        retIntent.putExtra("priority", pri)
        retIntent.putExtra("type", taskType.selectedItem.toString())

        setResult(Activity.RESULT_OK,retIntent)
        finish()
    }

}
