package com.example.mydemineur

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mydemineur.data.ScoreLine
import kotlinx.android.synthetic.main.layout_list_item.view.*


class Adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<ScoreLine> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ScoreViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ScoreViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun submitList(scoreList: List<ScoreLine>){
        items = scoreList
    }
    class ScoreViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val userName: TextView = itemView.UserName
        val time: TextView = itemView.Time

        fun bind(score: ScoreLine){
            userName.setText(score.userName)
            time.setText(score.time)

        }
    }
}