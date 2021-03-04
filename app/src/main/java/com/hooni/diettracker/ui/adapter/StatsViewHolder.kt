package com.hooni.diettracker.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.diettracker.R
import com.hooni.diettracker.data.Stat

class StatsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(stat: Stat, isSelected: Boolean = false) {
        val date = view.findViewById<TextView>(R.id.textView_listItemStats_date)
        val time = view.findViewById<TextView>(R.id.textView_listItemStats_time)
        val weight = view.findViewById<TextView>(R.id.textView_listItemStats_weight)
        val waist = view.findViewById<TextView>(R.id.textView_listItemStats_waist)
        val kCal = view.findViewById<TextView>(R.id.textView_listItemStats_kCal)

        date.text = stat.date
        time.text = stat.time
        weight.text = view.context.getString(R.string.textView_listItem_weight, stat.weight.toString())
        waist.text = view.context.getString(R.string.textView_listItem_waist, stat.waist.toString())
        kCal.text = view.context.getString(R.string.textView_listItem_kCal, stat.kCal.toString())
        itemView.isActivated = isSelected
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
        override fun getPosition(): Int = adapterPosition
        override fun getSelectionKey(): Long = itemId
    }

    companion object {
        fun create(parent: ViewGroup): StatsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.list_item_stats, parent, false)
            return StatsViewHolder(view)
        }

        private const val TAG = "StatsViewHolder"
    }
}