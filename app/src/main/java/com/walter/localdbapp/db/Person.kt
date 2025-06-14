package com.walter.localdbapp.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "person", indices = [Index(value = ["email"], unique = true)])
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val weight: Int
)
