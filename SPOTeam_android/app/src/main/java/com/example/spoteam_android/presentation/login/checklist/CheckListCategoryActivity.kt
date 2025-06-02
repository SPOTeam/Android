package com.example.spoteam_android.presentation.login.checklist

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityCheckListCategoryBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckListCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckListCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedThemes = mutableListOf<String>()
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val chip = buttonView as Chip
            val chipText = chip.text.toString()
            val processedText = if (chipText.contains("전공")) chipText.replace("/", "및") else chipText.replace("/", "").replace(" ", "")
            if (isChecked) {
                if (processedText !in selectedThemes) {
                    selectedThemes.add(processedText)
                }
            } else {
                selectedThemes.remove(processedText)
            }
            binding.checklistspotNextBt.isEnabled = selectedThemes.isNotEmpty()
        }

        repeat(binding.activityChecklistChipGroup.childCount) {
            (binding.activityChecklistChipGroup.getChildAt(it) as? Chip)
                ?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }

        binding.checklistspotNextBt.setOnClickListener {
            val intent = Intent(this, CheckListStudyPurposeActivity::class.java)
            intent.putStringArrayListExtra("selectedThemes", ArrayList(selectedThemes))
            startActivity(intent)
        }
    }
}