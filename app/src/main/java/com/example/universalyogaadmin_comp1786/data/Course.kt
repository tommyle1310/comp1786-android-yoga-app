package com.example.universalyogaadmin_comp1786.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val day: String,
    val time: String,
    val capacity: String,
    val duration: String,
    val price: String,
    val type: String,
    val description: String
)