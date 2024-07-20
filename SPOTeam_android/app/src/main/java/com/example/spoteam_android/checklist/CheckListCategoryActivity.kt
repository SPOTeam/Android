package com.example.spoteam_android.checklist

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityCheckListCategoryBinding

class CheckListCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheckListCategoryBinding
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

        val categoryintent = Intent(this, CheckListStudyPurposeActivity::class.java)
        binding.checklistspotNextBt.setOnClickListener { startActivity(categoryintent) }


    }

    private fun setChipGroup() {
        //초기 버튼 비활성화
        binding.checklistspotNextBt.isEnabled = false

        //chip 선택 상태 리스너
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            val isAnyChipChecked = binding.activityChecklistChipGroup.checkedChipIds.isNotEmpty()
            binding.checklistspotNextBt.isEnabled = isAnyChipChecked
        }

        for (i in 0 until binding.activityChecklistChipGroup.childCount) {
            val chip = binding.activityChecklistChipGroup.getChildAt(i) as? CompoundButton
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }


    }
}