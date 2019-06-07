package com.example.todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.todo.Task

@Dao
interface TaskDAO {

    @Query("select * from Task")
    fun getAll(): List<Task>

    @Query("select * from Task where name like :pattern")
    fun findByName(pattern : String) : List<Task>

    @Delete
    fun delete(task: Task)

    @Insert
    fun insertAll(vararg task: Task)
}