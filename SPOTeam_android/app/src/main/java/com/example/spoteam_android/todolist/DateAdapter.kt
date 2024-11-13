package com.example.spoteam_android.todolist

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import java.util.Calendar

data class DateItem(val date: String, val isActive: Boolean)

class DateAdapter(
    val dates: List<DateItem>,
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition: Int

    init {
        // 오늘 날짜를 계산하여 selectedPosition을 초기화
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH).toString()
        selectedPosition = dates.indexOfFirst { it.date == today }

        if (selectedPosition == -1) {
            selectedPosition = 0 // 오늘 날짜가 리스트에 없으면 첫 번째 날짜를 선택
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_todolist, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val dateItem = dates[position]
        holder.bind(dateItem)

        // 선택된 날짜와 비교하여 UI 업데이트
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.item_calendar_rv) // 선택된 날짜의 배경
            holder.dateTextView.setTextColor(Color.parseColor("#186AFF"))
            holder.dateTextView.setTypeface(null, Typeface.BOLD)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_calendar_rv_non_selected) // 선택되지 않은 날짜의 배경
            holder.dateTextView.setTextColor(Color.parseColor("#588FF2"))
            holder.dateTextView.setTypeface(null, Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition) // 이전에 선택된 항목을 갱신
            notifyItemChanged(position) // 현재 선택된 항목을 갱신

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