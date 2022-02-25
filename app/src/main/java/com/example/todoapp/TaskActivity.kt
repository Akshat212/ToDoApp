package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

const val DB_Name = "todo.db"
class TaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
    }
}