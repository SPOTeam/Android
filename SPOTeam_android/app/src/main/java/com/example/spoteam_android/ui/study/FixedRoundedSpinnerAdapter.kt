package com.example.spoteam_android.ui.study

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.SpinnerDropdownItemBinding

class FixedRoundedSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, R.layout.spinner_item, items) {

    override fun getDropDownView(position: Int, convertView: android.view.View?, parent: ViewGroup): android.view.View {
        val binding = SpinnerDropdownItemBinding.inflate(LayoutInflater.from(context), parent, false)

        binding.spinnerText.text = items[position]

            val background = when (position) {
            0 -> R.drawable.spinner_item_top
            items.size - 1 -> R.drawable.spinner_item_bottom
            else -> R.drawable.spinner_item_middle
        }

        binding.itemContainer.setBackgroundResource(background)
        return binding.root
    }
}