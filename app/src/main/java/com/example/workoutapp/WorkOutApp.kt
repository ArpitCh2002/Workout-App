package com.example.workoutapp

import android.app.Application

class WorkOutApp : Application() {
    val db : HistoryDatabase by lazy {
        HistoryDatabase.getInstance(this)
    }
}