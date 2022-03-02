package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ToDoDao {

    @Insert()
    suspend fun insertTask(todoModel: ToDoModel):Long

    @Query("Select * from ToDoModel where isFinished = 0")
    fun getTask():LiveData<List<ToDoModel>>

    @Query("Select * from ToDoModel where isFinished = 1")
    fun getFinishedTask():LiveData<List<ToDoModel>>

    @Query("Update ToDoModel Set isFinished = 1 where id=:uid")
    fun finishTask(uid:Long)

    @Query("Delete from ToDoModel where id=:uid")
    fun deleteTask(uid:Long)
}