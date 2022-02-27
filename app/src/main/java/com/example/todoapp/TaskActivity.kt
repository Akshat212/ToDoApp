package com.example.todoapp

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.room.Room
import com.example.todoapp.AppDatabase.Companion.getInstance
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar : Calendar

    lateinit var dateSetListener : DatePickerDialog.OnDateSetListener

    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finalTime : Long = 0L
    var finalDate : Long = 0L

    val db by lazy {
        getInstance(this)
    }

    var categories = arrayOf("Bank","Alarm","Meeting","Studying")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        setUpSpinner()

        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.dateEdt -> {
                setDateListener()
            }

            R.id.timeEdt -> {
                setTimeListener()
            }

            R.id.saveBtn -> {
                insertDataDb()
            }
        }
    }

    private fun insertDataDb() {
        val category = spinnerCategory.selectedItem.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val todoModel = ToDoModel(etTitle.text.toString(),etDesc.text.toString(),finalDate,
                                        finalTime,category)

            val id = db.todoDao().insertTask(todoModel)
        }

        finish()
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        )

        categories.sort()

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
            myCalendar.set(Calendar.MINUTE,minute)

            updateTimeFormat()
        }

        val timePickerDialog = TimePickerDialog(this,timeSetListener,myCalendar.get(Calendar.HOUR_OF_DAY),
                            myCalendar.get(Calendar.MINUTE),false)

        timePickerDialog.show()
    }

    private fun updateTimeFormat() {
        val time = "h:m a"
        val sdf = SimpleDateFormat(time)

        finalTime = myCalendar.time.time

        timeEdt.setText(sdf.format(myCalendar.time))
    }

    private fun setDateListener() {
        myCalendar = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            updateDateFormat()
        }

        val datePickerDialog = DatePickerDialog(this,dateSetListener,myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDateFormat() {
        val format = "EEE, d MMM yyyy"

        val sdf = SimpleDateFormat(format)

        finalDate = myCalendar.time.time

        dateEdt.setText(sdf.format(myCalendar.time))
        timeInptLay.visibility = View.VISIBLE
    }
}