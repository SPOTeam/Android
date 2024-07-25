package com.example.spoteam_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class StudyRegisterPopupFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_study_register_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.popup_title)
        val message = view.findViewById<TextView>(R.id.popup_message)
        val editText = view.findViewById<EditText>(R.id.popup_edit_text)
        val applyButton = view.findViewById<ImageView>(R.id.popup_apply_button)

        applyButton.setOnClickListener {
            val inputText = editText.text.toString()
            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "신청 완료", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }
}