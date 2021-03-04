package com.hooni.diettracker.ui.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.util.DateAndTime

class StatsAdapter(private val stats: MutableList<Stat>, private val editClickListener: (Stat) -> Unit): RecyclerView.Adapter<StatsViewHolder>(), Filterable {

    var displayedStats = mutableListOf<Stat>()

    init {
        displayedStats = stats
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        return StatsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val item = displayedStats[position]
        holder.itemView.setOnClickListener {editClickListener(item)}
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return displayedStats.size
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val startDate = constraint.toString().substringBefore("///")
                val endDate = constraint.toString().substringAfter("///")
                val startDateAndTime = DateAndTime.fromString(startDate)
                val endDateAndTime = DateAndTime.fromString(endDate)

                val resultList = stats.filter {
                    DateAndTime.fromString(it.date) in startDateAndTime..endDateAndTime
                }
                val filterResults =  FilterResults()
                filterResults.values = resultList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                displayedStats = results?.values as MutableList<Stat>
                notifyDataSetChanged()
            }
        }
    }

    companion object {
        private const val TAG = "StatsAdapter"
    }
}