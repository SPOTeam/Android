    package com.example.spoteam_android.checklist

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.widget.CompoundButton
    import androidx.appcompat.app.AppCompatActivity
    import com.example.spoteam_android.R
    import com.example.spoteam_android.databinding.ActivityCheckListStudyPurposeBinding
    import com.google.android.material.chip.Chip

    class CheckListStudyPurposeActivity : AppCompatActivity() {
        lateinit var binding: ActivityCheckListStudyPurposeBinding
        private val selectedPurpose = mutableListOf<Int>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityCheckListStudyPurposeBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val selectedThemes = intent.getStringArrayListExtra("selectedThemes")


            setSupportActionBar(binding.activityChecklistStudypurposeTb)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            title = ""
            binding.activityChecklistStudypurposeTb.setNavigationOnClickListener {
                onBackPressed()
            }

            binding.activityChecklistStudypurposeChecklistspotNextBt.setOnClickListener {
                val locationIntent = Intent(this, CheckListLocationActivity::class.java).apply {
                    putStringArrayListExtra("selectedThemes", ArrayList(selectedThemes))
                    putIntegerArrayListExtra("selectedPurpose", ArrayList(selectedPurpose))
                }
                startActivity(locationIntent)
                finish()
            }

            setChipGroup()
        }

        private fun setChipGroup() {
            // 초기 버튼 비활성화
            binding.activityChecklistStudypurposeChecklistspotNextBt.isEnabled = false

            val chipMap = mapOf(
                R.id.activity_checklist_studypurpose_chip_language to 1,
                R.id.activity_checklist_studypurpose_chip_license to 2,
                R.id.activity_checklist_studypurpose_chip_job to 3,
                R.id.activity_checklist_studypurpose_chip_discussion to 4,
                R.id.activity_checklist_studypurpose_chip_news to 5
            )

            // chip 선택 상태 리스너
            val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                val chip = buttonView as Chip
                val chipId= chip.id
                val associatedNumber = chipMap[chipId]

                if (isChecked) {
                    associatedNumber?.let { selectedPurpose.add(it) }
                } else {
                    associatedNumber?.let { selectedPurpose.remove(it) }
                }

                // 버튼 활성화 상태 업데이트
                val isAnyChipChecked = selectedPurpose.isNotEmpty()
                binding.activityChecklistStudypurposeChecklistspotNextBt.isEnabled = isAnyChipChecked
            }

            // FlexboxLayout의 각 칩에 리스너 설정
            for (i in 0 until binding.flexboxLayout.childCount) {
                val chip = binding.flexboxLayout.getChildAt(i) as? Chip
                chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
            }
        }
    }
