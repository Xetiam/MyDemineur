package com.example.mydemineur

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler


class Splash : Activity() {

    private val SPLASH_DISPLAY_LENGTH = 2000
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val mainIntent = Intent(this@Splash, MainActivity::class.java)
            this@Splash.startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}