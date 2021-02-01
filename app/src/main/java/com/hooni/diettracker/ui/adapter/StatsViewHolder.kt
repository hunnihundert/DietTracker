package com.hooni.diettracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hooni.diettracker.R
import com.hooni.diettracker.data.Stat

class StatsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(stat: Stat) {
        val date = view.findViewById<TextView>(R.id.textView_listItemStats_date)
        val weight = view.findViewById<TextView>(R.id.textView_listItemStats_weight)
        val waist = view.findViewById<TextView>(R.id.textView_listItemStats_waist)
        val kCal = view.findViewById<TextView>(R.id.textView_listItemStats_kCal)

        date.text = stat.date
        weight.text = view.context.getString(R.string.textView_listItem_weight)
        waist.text = view.context.getString(R.string.textView_listItem_waist)
        kCal.text = view.context.getString(R.string.textView_listItem_kCal)
    }

    companion object {
        fun create(parent: ViewGroup): StatsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.fragment_stats,parent,false)
            return StatsViewHolder(view)
        }
    }
}