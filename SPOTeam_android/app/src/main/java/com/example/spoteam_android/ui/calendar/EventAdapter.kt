package com.example.spoteam_android.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R

class EventAdapter(
    private var events: List<Event>,
    private val onCheckClick: (Event) -> Unit,
    private val isTodoList: Boolean
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view, onCheckClick, isTodoList)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(
        itemView: View,
        private val onCheckClick: (Event) -> Unit,
        private val isTodoList: Boolean
    ) : RecyclerView.ViewHolder(itemView) {
        private val eventTitleTextView: TextView = itemView.findViewById(R.id.eventTitleTextView)
        private val eventStartdate: TextView = itemView.findViewById(R.id.startDate)
        private val eventEndtdate: TextView = itemView.findViewById(R.id.enddate)
        private val icCheck: ImageView = itemView.findViewById(R.id.ic_check)

        fun bind(event: Event) {
            eventTitleTextView.text = event.title
            eventStartdate.text = event.startDateTime
            eventEndtdate.text = event.endDateTime

            icCheck.visibility = if (isTodoList) View.GONE else View.VISIBLE


            icCheck.setOnClickListener {
                onCheckClick(event)
            }
        }
    }
}
