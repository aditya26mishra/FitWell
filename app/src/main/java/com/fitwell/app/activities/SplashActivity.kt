package com.fitwell.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fitwell.app.utils.Session

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val next = if (Session(this).loggedIn) MainActivity::class.java else AuthActivity::class.java
        startActivity(Intent(this, next))
        finish()
    }
}
