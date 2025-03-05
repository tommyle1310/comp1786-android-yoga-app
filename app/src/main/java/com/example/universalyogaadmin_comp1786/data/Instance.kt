package com.example.universalyogaadmin_comp1786.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "instances",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["id"],
        childColumns = ["courseId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Instance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int, // Liên kết với Course
    val date: String, // VD: "17/10/2023"
    val teacher: String,
    val comments: String?
)