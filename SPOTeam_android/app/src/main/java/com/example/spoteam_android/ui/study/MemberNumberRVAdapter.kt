package com.example.spoteam_android.ui.study

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R

class MemberNumberRVAdapter(
    private val items: List<Int>
) : RecyclerView.Adapter<MemberNumberRVAdapter.ViewHolder>() {

    private var selectedPosition: Int = 0
    var onItemClick: ((position: Int) -> Unit)? = null

    inner class ViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_member_number)

        init {
            textView.setOnClickListener {
                onItemClick?.invoke(bindingAdapterPosition)
            }
        }
    }


    fun getSelectedNumber(): Int {
        return items.getOrNull(selectedPosition) ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_number, parent, false)

        val itemWidth = parent.context.resources.displayMetrics.widthPixels / 7
        view.layoutParams = RecyclerView.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        return ViewHolder(view as ViewGroup)
    }





    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = items[position]

        holder.textView.text = number.toString()

        if (position == selectedPosition) {
            holder.textView.setBackgroundResource(R.drawable.page_bg)
        } else {
            holder.textView.setBackgroundColor(Color.TRANSPARENT)
        }
    }



    override fun getItemCount(): Int = items.size

    fun setSelectedPosition(position: Int) {
        val previous = selectedPosition
        selectedPosition = position
        notifyItemChanged(previous)
        notifyItemChanged(selectedPosition)
    }
}
