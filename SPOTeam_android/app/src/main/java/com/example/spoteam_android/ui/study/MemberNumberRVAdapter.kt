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

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
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
            .inflate(R.layout.item_member_number, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = items[position]
        val context = holder.itemView.context
        val typefaceBold = ResourcesCompat.getFont(context, R.font.suit_bold)
        val typefaceRegular = ResourcesCompat.getFont(context, R.font.suit_variable)


        holder.textView.text = number.toString()

        if (position == selectedPosition) {
            holder.textView.setTextColor(Color.BLACK)
            holder.textView.textSize = 24f
            holder.textView.typeface = typefaceBold
        } else {
            holder.textView.setTextColor(Color.GRAY)
            holder.textView.textSize = 24f
            holder.textView.typeface = typefaceRegular
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
