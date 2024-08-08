package com.example.spoteam_android.checklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityCheckListCategoryBinding
import com.google.android.material.chip.Chip

class CheckListCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheckListCategoryBinding
    private val selectedThemes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCheckListCategoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.activityChecklistTb)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        binding.activityChecklistTb.setNavigationOnClickListener {
            onBackPressed()
        }

        setChipGroup()

        binding.checklistspotNextBt.setOnClickListener {
            // 선택된 테마 리스트를 다음 액티비티로 전달
            val themeIntent = Intent(this, CheckListStudyPurposeActivity::class.java)
            themeIntent.putStringArrayListExtra("selectedThemes", ArrayList(selectedThemes))
            startActivity(themeIntent)
        }
    }

    private fun setChipGroup() {
        // 초기 버튼 비활성화
        binding.checklistspotNextBt.isEnabled = false

        // chip 선택 상태 리스너
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val chip = buttonView as Chip
            if (isChecked) {
                selectedThemes.add(chip.text.toString())
            } else {
                selectedThemes.remove(chip.text.toString())
            }

            val isAnyChipChecked = binding.activityChecklistChipGroup.checkedChipIds.isNotEmpty()
            binding.checklistspotNextBt.isEnabled = isAnyChipChecked
        }

        for (i in 0 until binding.activityChecklistChipGroup.childCount) {
            val chip = binding.activityChecklistChipGroup.getChildAt(i) as? CompoundButton
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }
}
