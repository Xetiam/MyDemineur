package com.example.mydemineur

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mydemineur.data.ListScoreGlobal
import com.example.mydemineur.data.ScoreLine
import kotlinx.android.synthetic.main.activity_score_board.*
import org.json.JSONObject
import java.io.File


class ScoreBoardActivity : AppCompatActivity() {
    private lateinit var exeAdapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_board)
        val path = this.filesDir
        val scoreDir = File(path, "data_score")
        val file = File(scoreDir, "score.json")
        var listScore: ArrayList<ScoreLine> = ArrayList()
        if (file.exists()) {
            file.forEachLine {
                val jsObj = JSONObject(it)
                val pseudo = jsObj.optString("userName")
                val time = jsObj.optString("time")
                val diff = jsObj.optString("difficulty")
                val line = ScoreLine(pseudo, diff, time)
                listScore.add(line)
            }
            listScore = mySort(listScore)
        }
        else {
            println("Il n'y a pas encore de score")
        }
        //tri des scores en fonction de la difficulté
        var listF: ArrayList<ScoreLine> = ArrayList()
        var listM: ArrayList<ScoreLine> = ArrayList()
        var listD: ArrayList<ScoreLine> = ArrayList()
        for(score in listScore){
            when(score.difficulty){
                "Facile" -> listF.add(score)
                "Moyen" -> listM.add(score)
                "Difficile" -> listD.add(score)
            }
        }

        //Boutons
        btn_delete.setOnClickListener{
            val path = this.filesDir
            val scoreDir = File(path, "data_score")
            val file = File(scoreDir, "score.json")
            btn_delete.setBackgroundResource(0)
            val runnable = Runnable {
                btn_delete.setBackgroundResource(R.drawable.ic_baseline_delete_3)
                if(file.exists()){
                    file.delete()
                }
                this.finish()
            }
            val handler = Handler()
            handler.postDelayed(runnable, 100)
        }

        btn_facile.setOnClickListener{
            listScore = listF
            ListScoreGlobal.listScore =  listScore
            initRecycleView()
            exeAdapter.submitList(ListScoreGlobal.listScore)
            btn_moyen.setBackgroundColor(Color.argb(150, 0, 0, 0))
            btn_difficile.setBackgroundColor(Color.argb(150, 0, 0, 0))
            btn_facile.setBackgroundColor(Color.argb(100, 61, 159, 1))

        }
        btn_moyen.setOnClickListener{
            listScore = listM
            ListScoreGlobal.listScore =  listScore
            initRecycleView()
            exeAdapter.submitList(ListScoreGlobal.listScore)
            btn_facile.setBackgroundColor(Color.argb(150, 0, 0, 0))
            btn_difficile.setBackgroundColor(Color.argb(150, 0, 0, 0))
            btn_moyen.setBackgroundColor(Color.argb(100, 61, 159, 1))


        }
        btn_difficile.setOnClickListener{
            listScore = listD
            ListScoreGlobal.listScore =  listScore
            initRecycleView()
            exeAdapter.submitList(ListScoreGlobal.listScore)
            btn_moyen.setBackgroundColor(Color.argb(150, 0, 0, 0))
            btn_facile.setBackgroundColor(Color.argb(150, 0, 0, 0))
            btn_difficile.setBackgroundColor(Color.argb(100, 61, 159, 1))

        }

    }

    //Retourne une liste triée de score
    private fun mySort(listScore: ArrayList<ScoreLine>): ArrayList<ScoreLine> {
        var listScoreSorted: ArrayList<ScoreLine> = ArrayList()

        while(listScore.size-1 >0) {
            var index = bestScore(listScore)
            listScoreSorted.add(listScore[index])
            listScore.removeAt(index)
        }
        listScoreSorted.add(listScore[0])

        return listScoreSorted
    }

    //Convertis le string du temps en seconde pour comparaison
    private fun timeToInt(time: String): Int{
        val minute = time[0].toInt()*10 + time[1].toInt()
        val seconde = time[3].toInt()*10 + time[4].toInt()
        return minute*60+seconde
    }

    //Retourne l'index du meilleur score d'une liste de score
    private fun bestScore(listScore: ArrayList<ScoreLine>): Int{
        var index = 0
        var bestScore = listScore[0]
        var i = 0
        for(score in listScore){
            if(timeToInt(score.time) < timeToInt(bestScore.time)){
                bestScore = score
                index = i
            }
            i++
        }
        return index
    }

    private fun initRecycleView(){
        recycler_view.apply{
            layoutManager = LinearLayoutManager(this@ScoreBoardActivity)
            exeAdapter = Adapter()
            adapter = exeAdapter
        }
    }
}
