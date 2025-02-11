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
            onBackPressedDispatcher.onBackPressed()

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

        // Chip 선택 상태 리스너
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val chip = buttonView as Chip
            val chipText = chip.text.toString()
            val processedText = when {
                chipText.contains("전공") -> chipText.replace("/", "및") //
                else -> chipText.replace("/", "").replace(" ", "") // 공백 및 슬래시를 제거
            }

            if (isChecked) {
                if (chipText !in selectedThemes) {
                    selectedThemes.add(processedText)
                }
            } else {
                selectedThemes.remove(processedText)
            }

            // 선택된 칩이 하나라도 있으면 버튼 활성화
            val isAnyChipChecked = selectedThemes.isNotEmpty()
            binding.checklistspotNextBt.isEnabled = isAnyChipChecked

            Log.d("CheckListCategoryActivity", "Selected Themes: $selectedThemes")
        }

        // Chip 그룹의 모든 칩에 리스너 설정
        for (i in 0 until binding.activityChecklistChipGroup.childCount) {
            val chip = binding.activityChecklistChipGroup.getChildAt(i) as? Chip
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }

}
