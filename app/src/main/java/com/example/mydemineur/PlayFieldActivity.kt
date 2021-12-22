package com.example.mydemineur

import android.content.DialogInterface
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.text.InputFilter
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mydemineur.data.ScoreLine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_play_field.*
import java.io.File


class PlayFieldActivity : AppCompatActivity() {
    lateinit var mediaPlayerM: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_play_field)
        val intentParent = intent
        val difficulty = intentParent.getStringExtra("difficulty")
        selected_difficulty.text = difficulty
        val diff = selected_difficulty.text as String
        val vol = intentParent.getFloatExtra("volume", 1F)
        mediaPlayerM = MediaPlayer.create(this, R.raw.music_game)
        mediaPlayerM.isLooping = true
        mediaPlayerM.setVolume(volume, volume)
        mediaPlayerM.start()

        chronometer.start()
        createGrid(diff, vol)
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.setVolume(0F, 0F)
        mediaPlayerM.setVolume(0F, 0F)
    }

    override fun onRestart() {
        super.onRestart()
        mediaPlayer.setVolume(volume, volume)
        mediaPlayerM.setVolume(volume, volume)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.setVolume(volume, volume)
        mediaPlayerM.setVolume(0F, 0F)
    }
    //Créer la liste de mine
    private fun createMine(size: Int):List<Int>{
        val nmbrMine = (size*size)/5-1
        var nmbrMineCreated = 0
        var loc: Int
        var listMine : MutableList<Int> = MutableList(nmbrMine + 1){0}
        while(nmbrMineCreated<=nmbrMine){
            loc = (1..size*size).random()

            if(loc !in listMine){
                listMine[nmbrMineCreated] = loc
                nmbrMineCreated ++
            }
            else{
                loc = 0
            }
        }
        return listMine
    }

    //Créer la vue de tout les boutons pouvant cacher ou non des mines
    private fun createGrid(diff: String, vol: Float) {
        //Initialisation Sound
        var mediaPlayerE = MediaPlayer.create(this, R.raw.explosion)
        mediaPlayerE.isLooping = false
        mediaPlayerE.setVolume(vol, vol)

        //Initialisation de la taille et des dimmension de la grille
        var size = 0
        var dim = 0

        when (diff) {
            "Facile" -> {
                size = 5
                dim = 200
            }
            "Moyen" -> {
                size = 7
                dim = 150
            }
            "Difficile" -> {
                size = 10
                dim = 100
            }
        }
        val listMine = createMine(size)
        var listClicked = MutableList(size * size - (size * size / 5)){0}
        var nmbrclick = 0
        var nmbFlag = 0
        val layout: GridLayout = gridLayout
        mineflagged.text = "$nmbFlag / ${size * size / 5}"

        //Création des boutons de la grille
        val myButtons = ArrayList<Button>()
        val buttonFlagged = ArrayList<Int>()
        for (i in 1..size * size) {
            val button = Button(this)
            button.minimumHeight = 0
            button.minimumWidth = 0
            button.width = dim
            button.height = dim
            button.id = i
            myButtons.add(button)
            layout.addView(button)
            val mybuttonBack = button.background
            button.setOnClickListener {
                if(!Flag.isChecked && button.id !in buttonFlagged){
                    //Ne fait rien si le bouton est déjà cliqué
                    if (button.id !in listClicked) {
                        listClicked[nmbrclick] = button.id
                        nmbrclick++
                    }
                    //Réaction lors du clic sur une mine
                    if (button.id in listMine) {
                        //Son d'explosion
                        mediaPlayerM.stop()
                        mediaPlayerE.start()
                        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    1000,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
                            vibrator.vibrate(1000)
                        }

                        //Image de mine + fond
                        for(i in buttonFlagged){
                            myButtons[i - 1].setBackgroundResource(0)
                        }
                        for(i in listMine){
                            myButtons[i - 1].setBackgroundResource(R.drawable.icon_action_mine2)
                        }
                        button.setBackgroundResource(R.drawable.icon_action_mine)
                        gridLayout.setBackgroundColor(Color.argb(255, 200, 0, 0))
                        chronometer.stop()
                        val runnable = Runnable {
                            val alertDialog = AlertDialog.Builder(this)
                            alertDialog.setTitle("Vous avez Perdu !")
                            alertDialog.setMessage("Il y avait une mine")
                            alertDialog.setCancelable(false)
                            alertDialog.setPositiveButton(
                                "Retour au Menu",
                                DialogInterface.OnClickListener { _, _ ->
                                    finish()
                                })
                            alertDialog.setNegativeButton(
                                "",
                                DialogInterface.OnClickListener { _, _ ->
                                    finish()
                                })
                            val alert = alertDialog.create()
                            alert.show()
                        }
                        val handler = Handler()

                        handler.postDelayed(runnable, 1000)
                    }

                    //Réaction d'un clic sur un boutton sans mine
                    else {
                        button.setBackgroundColor(0)
                        var mineAdj: Int
                        mineAdj = searchMineAdj(button.id, listMine, size)
                        //Il y a des mines adjacentes
                        if (mineAdj != 0) {
                            button.text = "$mineAdj"
                            when(button.text){
                                "1" -> button.setTextColor(Color.argb(255, 0, 255, 0))
                                "2" -> button.setTextColor(Color.argb(255, 0, 0, 255))
                                "3" -> button.setTextColor(Color.argb(255, 200, 150, 0))
                                "4" -> button.setTextColor(Color.argb(255, 255, 0, 0))
                                "5" -> button.setTextColor(Color.argb(255, 5, 5, 5))
                            }
                        }
                        //Il n'y a pas de mine adjacente
                        else {
                            val listId = clickOnNothing(button.id, size)
                            //clic sur les bouton adjs dont l'id est rangée dans listId
                            for(i in listId){
                                if(i != 0){
                                    if(i !in listClicked){
                                        myButtons[i - 1].performClick()
                                        myButtons[i - 1].isPressed = true
                                        myButtons[i - 1].invalidate()
                                        myButtons[i - 1].isPressed = false
                                        myButtons[i - 1].invalidate()
                                    }
                                }
                            }
                        }
                        //Condition de victoire
                        if (nmbrclick == size * size - (size * size / 5)) {
                            chronometer.stop()
                            val time = chronometer.text.toString()
                            mediaPlayerM.stop()
                            val mediaPlayerV = MediaPlayer.create(this, R.raw.music_victory)
                            mediaPlayerV.setVolume(volume, volume)
                            mediaPlayerV.isLooping = false
                            mediaPlayerV.start()
                            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        2000,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(2000)
                            }
                            //Récupération du pseudo utilisateur
                            val edittext = EditText(this)
                            edittext.filters += InputFilter.LengthFilter(15)
                            edittext.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                            edittext.maxLines = 1
                            val userNameDialog = AlertDialog.Builder(this)
                            userNameDialog.setMessage("Entrez votre pseudo")
                            userNameDialog.setTitle("Félicitation !")
                            userNameDialog.setView(edittext)
                            userNameDialog.setPositiveButton("Entrer"){ dialog, wich->
                                val userName = edittext.text.toString()
                                val score: ScoreLine = ScoreLine(userName, diff, time)
                                val gson = Gson()
                                val scoreJs = gson.toJson(score)
                                //Enregistrement du score
                                addScore(scoreJs)
                                val alertDialog = AlertDialog.Builder(this)
                                alertDialog.setTitle("Vous avez Gagné !")
                                alertDialog.setMessage("Vous avez découvert toutes les mines avec un temps de :\n$time")
                                alertDialog.setCancelable(false)
                                alertDialog.setPositiveButton(
                                    "Retour au Menu",
                                    DialogInterface.OnClickListener { _, _ ->
                                        finish()
                                    })
                                val alert = alertDialog.create()
                                alert.show()
                            }
                            userNameDialog.setCancelable(false)
                            val dialog = userNameDialog.create()
                            dialog.show()

                        }
                    }
                }
                //Réaction à un clic lorsque que l'option pour flagger est activé
                if(Flag.isChecked && button.id !in listClicked){
                    if(button.id !in buttonFlagged){
                        button.setBackgroundResource(R.drawable.icon_action_flag)
                        nmbFlag++
                        mineflagged.text = "$nmbFlag / ${size * size / 5}"
                        buttonFlagged.add(button.id)
                    }
                    else{
                        nmbFlag--
                        mineflagged.text = "$nmbFlag / ${size * size / 5}"
                        button.background = mybuttonBack

                        buttonFlagged.remove(button.id)
                    }
                }
            }
        }
        layout.columnCount = size
    }

    //Ajoute le score actuel au tableau des scores.
    private fun addScore(scoreJs: String) {
        val path = this.filesDir
        val scoreDir = File(path, "data_score")
        scoreDir.mkdirs()
        val file = File(scoreDir, "score.json")
        file.appendText(scoreJs + "\n")
    }

    //Retourne la liste des bouttons adj au boutton sans mine
    private fun clickOnNothing(id: Int, size: Int): List<Int>{
        var buttonList : MutableList<Int> = MutableList(8){0}
        var nmbButton = 0
        if(id> size){
            buttonList[nmbButton] = id-size
            nmbButton++
            if(id%size != 1){
                buttonList[nmbButton] = id-size-1
                nmbButton++
            }
            if (id%size != 0){
                buttonList[nmbButton] = id-size+1
                nmbButton++
            }
        }
        if(id<= size*size-size){
            buttonList[nmbButton] = id+size
            nmbButton++
            if(id%size != 1){
                buttonList[nmbButton] = id+size-1
                nmbButton++
            }
            if (id%size != 0){
                buttonList[nmbButton] = id+size+1
                nmbButton++
            }
        }
        if(id%size != 1){
            buttonList[nmbButton] = id-1
            nmbButton++
        }
        if (id%size != 0){
            buttonList[nmbButton] = id+1
            nmbButton++
        }
        return buttonList
    }

    //Chercher les mines adjacentes
    private fun searchMineAdj(id: Int, listMine: List<Int>, size: Int): Int {
        var mineAdj = 0
        if((id - size) in listMine && id>=size){
            mineAdj++
        }
        if((id - size-1) in listMine && id>=size && id%size != 1){
            mineAdj++
        }
        if((id - size+1) in listMine && id>=size && id%size != 0){
            mineAdj++
        }
        if((id + size) in listMine && id <= (size*size-size)){
            mineAdj++
        }
        if((id +size-1) in listMine && id%size != 1 && id <= (size*size-size)){
            mineAdj++
        }
        if((id + size+1) in listMine && id%size != 0 && id <= (size*size-size)){
            mineAdj++
        }
        if((id+1) in listMine && id%size != 0){
            mineAdj++
        }
        if((id - 1) in listMine && id%size != 1){
            mineAdj++
        }

        return mineAdj
    }
}