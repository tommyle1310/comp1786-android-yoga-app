package com.example.universalyogaadmin_comp1786.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert
    suspend fun insert(course: Course)

    @Update
    suspend fun update(course: Course)

    @Delete
    suspend fun delete(course: Course)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Query("DELETE FROM courses")
    suspend fun resetDatabase()
}
