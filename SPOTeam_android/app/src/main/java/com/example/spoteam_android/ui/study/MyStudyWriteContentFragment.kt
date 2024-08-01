package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMypageWriteContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyStudyWriteContentFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentMypageWriteContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageWriteContentBinding.inflate(inflater, container, false)

        binding.writeContentFinishBtn.setOnClickListener{
            dismiss()
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.thema_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}