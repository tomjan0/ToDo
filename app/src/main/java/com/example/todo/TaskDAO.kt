package com.example.todo

import androidx.room.*
import com.example.todo.Task

@Dao
interface TaskDAO {

    @Query("select * from Task")
    fun getAll(): List<Task>

    @Query("select * from Task where name like :pattern")
    fun findByName(pattern : String) : List<Task>

    @Update
    fun updateTasks(vararg tasks: Task)

    @Delete
    fun delete(task: Task)

    @Insert
    fun insertAll(vararg task: Task)
}