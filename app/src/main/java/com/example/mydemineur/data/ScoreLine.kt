package com.example.mydemineur.data

data class ScoreLine(
    val userName: String,
    val difficulty: String,
    val time: String,
){
    override fun toString(): String{
        return "Score [User: ${this.userName}, Difficulty: ${this.difficulty}, Time: ${this.time}]"
    }
}
