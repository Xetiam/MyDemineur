package com.example.mydemineur

import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*


var volume = 1F
lateinit var mediaPlayer :MediaPlayer
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mediaPlayer = MediaPlayer.create(this, R.raw.music_fond)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(volume , volume)
        mediaPlayer.start()

        btn_easy.setOnClickListener {
            selection.text = "Facile"
        }
        btn_medium.setOnClickListener {
            selection.text = "Moyen"
        }
        btn_difficult.setOnClickListener {
            selection.text = "Difficile"
        }
        btn_score.setOnClickListener{
            val intentScore = Intent(this, ScoreBoardActivity::class.java)
            startActivity(intentScore)
        }
        btn_play.setOnClickListener{
            val difficulty = selection.text as String
            if(difficulty != "Pas de sélection") {
                val intentPlayField = Intent(this, PlayFieldActivity::class.java)
                intentPlayField.putExtra("difficulty", difficulty)
                intentPlayField.putExtra("volume", volume)
                startActivity(intentPlayField)
            }
        }
    }
    override fun onStop() {
        super.onStop()
        mediaPlayer.setVolume(0F,0F)
    }

    override fun onRestart() {
        super.onRestart()
        mediaPlayer.setVolume(volume, volume)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.son_settings->{
                if(volume == 1F){
                    volume = 0F
                    mediaPlayer.setVolume(volume , volume)
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Le son est coupé!")
                    alertDialog.setCancelable(false)
                    alertDialog.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { _, _ ->
                        })
                    val alert = alertDialog.create()
                    alert.show()
                }
                else{
                    volume = 1F
                    mediaPlayer.setVolume(volume , volume)
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Le son est activé!")
                    alertDialog.setCancelable(false)
                    alertDialog.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { _, _ ->
                        })
                    val alert = alertDialog.create()
                    alert.show()
                }
                true
            }
            R.id.linkdinButton->{
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = (Uri.parse("https://www.linkedin.com/in/charly-lepiller/"))
                startActivity(intent)
                true
            }
            R.id.info->{
                val intentInfo = Intent(this, InfoActivity::class.java)
                startActivity(intentInfo)
                true
            }
            R.id.CV->{
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = (Uri.parse("https://drive.google.com/file/d/12yzl0IFETWbfGJtrVht5j_yLwYYa_S4N/view?usp=sharing"))
                startActivity(intent)
                true
            }
            R.id.quit->{
                finish()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}