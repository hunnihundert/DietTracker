package com.hooni.diettracker.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.diettracker.data.Stat

class StatsAdapter(private val stats: List<Stat>): RecyclerView.Adapter<StatsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        return StatsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.bind(stats[position])
    }

    override fun getItemCount(): Int {
        return stats.size
    }
}