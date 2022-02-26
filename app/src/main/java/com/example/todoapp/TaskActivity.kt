package com.example.todoapp

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_task.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val DB_Name = "todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar : Calendar

    lateinit var dateSetListener : DatePickerDialog.OnDateSetListener

    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    val db by lazy {
        Room.databaseBuilder(
                this,
                AppDatabase::class.java,
                DB_Name
        )
    }

    var categories = arrayOf("Bank","Alarm","Meeting","Studying")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)

        setUpSpinner()
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

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.dateEdt -> {
                setDateListener()
            }

            R.id.timeEdt -> {
                setTimeListener()
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

        dateEdt.setText(sdf.format(myCalendar.time))
        timeInptLay.visibility = View.VISIBLE
    }
}