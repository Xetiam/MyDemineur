package com.example.mydemineur.data

import android.app.Application

public class ListScoreGlobal: Application() {
    companion object{
        @JvmField
        var listScore = mutableListOf<ScoreLine>()
    }
}