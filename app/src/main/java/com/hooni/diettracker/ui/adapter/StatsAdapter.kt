package com.hooni.diettracker.ui.adapter

import android.util.Log
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.util.DateAndTime
import com.hooni.diettracker.util.LAST_TIME_OF_THE_DAY

class StatsAdapter(private val stats: MutableList<Stat>, private val editClickListener: (Stat) -> Unit): RecyclerView.Adapter<StatsViewHolder>(), Filterable {
    var tracker: SelectionTracker<Long>? = null
    var displayedStats: List<Stat>

    init {
        displayedStats = stats
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        return StatsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val item = displayedStats[position]
        holder.itemView.setOnClickListener {editClickListener(item)}
        tracker?.let {
            holder.bind(item, it.isSelected(position.toLong()))
        }

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
                val endDateAndTime = DateAndTime.fromString(endDate,LAST_TIME_OF_THE_DAY)
                val resultList = stats.filter {
                    it.dateAndTime in startDateAndTime..endDateAndTime
                }
                val filterResults =  FilterResults()
                filterResults.values = resultList.sortedBy { stat ->
                    stat.dateAndTime
                }
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                displayedStats = results?.values as List<Stat>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    companion object {
        private const val TAG = "StatsAdapter"
    }
}