package com.example.spoteam_android.login

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.spoteam_android.databinding.ItemSpinnerHomeRegisterBinding

class CategorySpinnerAdapter(
    context: Context,
    private val categoryList: List<String>
) : ArrayAdapter<String>(context, 0, categoryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemSpinnerHomeRegisterBinding
        val view: View

        if (convertView == null) {
            binding = ItemSpinnerHomeRegisterBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemSpinnerHomeRegisterBinding
            view = convertView
        }

        binding.itemSpinnerTv.text = categoryList[position]
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return categoryList.size
    }
}
