package com.example.spoteam_android.todolist

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R

data class DateItem(val date: String, val isActive: Boolean)

class DateAdapter(
    private val dates: List<DateItem>,
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todolist_item, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val dateItem = dates[position]
        holder.bind(dateItem)

        // isActive 값을 기준으로 UI 업데이트
        if (dateItem.isActive) {
            holder.itemView.setBackgroundResource(R.drawable.item_calendar_rv)
            holder.dateTextView.setTextColor(Color.parseColor("#186AFF"))
            holder.dateTextView.setTypeface(null, Typeface.BOLD)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_calendar_rv_non_selected)
            holder.dateTextView.setTextColor(Color.parseColor("#588FF2"))
            holder.dateTextView.setTypeface(null, Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener {
            onDateSelected(dateItem.date)
        }
    }

    override fun getItemCount(): Int = dates.size

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.tx_calendar_date)

        fun bind(dateItem: DateItem) {
            dateTextView.text = dateItem.date
        }
    }
}
