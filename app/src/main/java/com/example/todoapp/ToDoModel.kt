package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ToDoModel(
    var title : String,
    var description : String,
    var date : Long,
    var time : Long,
    var isFinished : Int = -1,
    var category : String,

    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L
)