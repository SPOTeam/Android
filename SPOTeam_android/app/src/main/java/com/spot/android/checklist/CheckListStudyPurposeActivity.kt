    package com.spot.android.checklist

    import android.content.Intent
    import android.os.Bundle
    import android.widget.CompoundButton
    import androidx.appcompat.app.AppCompatActivity
    import com.spot.android.MainActivity
    import com.spot.android.R
    import com.spot.android.databinding.ActivityCheckListStudyPurposeBinding
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
                // 회원가입 마지막 화면 → MainActivity 호출 부분
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)



            }

            binding.activityChecklistStudypurposeChecklistspotNextBt.setOnClickListener {
                val locationIntent = Intent(this, CheckListLocationActivity::class.java).apply {
                    putStringArrayListExtra("selectedThemes", ArrayList(selectedThemes))
                    putIntegerArrayListExtra("selectedPurpose", ArrayList(selectedPurpose))
                }
                startActivity(locationIntent)
            }

            setChipGroup()
        }

        private fun setChipGroup() {
            // 초기 버튼 비활성화
            binding.activityChecklistStudypurposeChecklistspotNextBt.isEnabled = false

            val chipMap = mapOf(
                R.id.activity_checklist_studypurpose_chip_habit to 1,
                R.id.activity_checklist_studypurpose_chip_feedback to 2,
                R.id.activity_checklist_studypurpose_chip_network to 3,
                R.id.activity_checklist_studypurpose_chip_license to 4,
                R.id.activity_checklist_studypurpose_chip_contest to 5,
                R.id.activity_checklist_studypurpose_chip_opinion to 6
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
