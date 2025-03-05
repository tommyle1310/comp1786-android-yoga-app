package com.example.universalyogaadmin_comp1786.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
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

    // Instance queries
    @Insert
    suspend fun insertInstance(instance: Instance)

    @Update
    suspend fun updateInstance(instance: Instance)

    @Delete
    suspend fun deleteInstance(instance: Instance)

    @Query("SELECT * FROM instances WHERE courseId = :courseId")
    fun getInstancesForCourse(courseId: Int): Flow<List<Instance>>

    @Query("SELECT * FROM instances WHERE teacher LIKE :teacherPrefix || '%' OR date = :date OR courseId IN (SELECT id FROM courses WHERE day = :day)")
    fun searchInstances(teacherPrefix: String, date: String, day: String): Flow<List<Instance>>
}