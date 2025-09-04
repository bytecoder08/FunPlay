package com.bytecoder.funplay

import android.app.Application
import com.bytecoder.funplay.data.repository.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class App : Application() {
    // App-wide scope for DB init if needed
    val appScope = CoroutineScope(SupervisorJob())

    // Singleton DB instance
    val db: AppDatabase by lazy {
        AppDatabase.get(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Optional: do any app-wide initialization here
    }
}
