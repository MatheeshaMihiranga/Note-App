package com.example.thetaskapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val taskTitle:String,
    val taskDesc:String,


):Parcelable
