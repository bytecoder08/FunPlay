package com.bytecoder.funplay.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bytecoder.funplay.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Could show a splash layout; keep instant for now
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
