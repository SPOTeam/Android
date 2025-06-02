package com.example.spoteam_android.presentation.login.checklist

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityCheckListStudyPurposeBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckListStudyPurposeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckListStudyPurposeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListStudyPurposeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedPurpose = mutableListOf<Int>()
        val chipMap = mapOf(
            R.id.activity_checklist_studypurpose_chip_habit to 1,
            R.id.activity_checklist_studypurpose_chip_feedback to 2,
            R.id.activity_checklist_studypurpose_chip_network to 3,
            R.id.activity_checklist_studypurpose_chip_license to 4,
            R.id.activity_checklist_studypurpose_chip_contest to 5,
            R.id.activity_checklist_studypurpose_chip_opinion to 6
        )

        val listener = CompoundButton.OnCheckedChangeListener { btn, isChecked ->
            chipMap[btn.id]?.let {
                if (isChecked) selectedPurpose.add(it) else selectedPurpose.remove(it)
                binding.activityChecklistStudypurposeChecklistspotNextBt.isEnabled = selectedPurpose.isNotEmpty()
            }
        }

        repeat(binding.flexboxLayout.childCount) {
            (binding.flexboxLayout.getChildAt(it) as? Chip)
                ?.setOnCheckedChangeListener(listener)
        }

        val selectedThemes = intent.getStringArrayListExtra("selectedThemes")

        binding.activityChecklistStudypurposeChecklistspotNextBt.setOnClickListener {
            val intent = Intent(this, CheckListLocationActivity::class.java)
            intent.putStringArrayListExtra("selectedThemes", selectedThemes)
            intent.putIntegerArrayListExtra("selectedPurposes", ArrayList(selectedPurpose))
            startActivity(intent)
        }
    }
}