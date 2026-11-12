package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val settings: UserSettings = UserSettings()
)

data class UserSettings(
    val theme: String = "system",
    val newsRegion: String = "US",
    val weatherUnits: String = "metric"
)


@Entity(tableName = "calc_history")
data class CalcHistory(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val id: String = "",
    val expression: String = "",
    val result: String = "",
    val timestampMillis: Long = System.currentTimeMillis()
)
