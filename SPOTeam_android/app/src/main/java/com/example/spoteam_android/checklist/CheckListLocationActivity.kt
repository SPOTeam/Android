package com.example.spoteam_android.checklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityCheckListLocationBinding
import com.example.spoteam_android.login.LocationSearchActivity
import com.example.spoteam_android.login.RegisterInformation
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

class CheckListLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckListLocationBinding
    private val selectedLocations = mutableListOf<String>() // 코드로 저장

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val address = data.getStringExtra("SELECTED_ADDRESS")
                val code = data.getStringExtra("SELECTED_CODE")
                addLocationChip(address, code)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedThemes = intent.getStringArrayListExtra("selectedThemes")
        val selectedPurpose = intent.getIntegerArrayListExtra("selectedPurpose")


        setSupportActionBar(binding.activityChecklistLocationTb)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        binding.activityChecklistLocationTb.setNavigationOnClickListener {
            val intent = Intent(this, CheckListStudyPurposeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)


        }


        binding.checklistspotLocationPlusBt.setOnClickListener {
            if (selectedLocations.size < 10) {
                val intent = Intent(this, LocationSearchActivity::class.java)
                startForResult.launch(intent)
            } else {
                // 최대 선택 개수를 초과한 경우 처리 로직
            }
        }

        binding.checklistspotLocationFinishBt.setOnClickListener {
            val intent = Intent(this, RegisterInformation::class.java).apply {
                putExtra("mode", "FINAL")
                putStringArrayListExtra("selectedThemes", ArrayList(selectedThemes))
                putIntegerArrayListExtra("selectedPurpose", ArrayList(selectedPurpose))
                putStringArrayListExtra("selectedLocations", ArrayList(selectedLocations)) // 코드 리스트 전달
                Log.d("ChecklistLocation","$selectedLocations")
            }
            startActivity(intent)
        }

        updateButtonStates()
    }

    private fun addLocationChip(address: String?, code: String?) {
        if (!address.isNullOrEmpty() && !selectedLocations.contains(code)) {
            code?.let {
                selectedLocations.add(it) // 코드 저장
            }

            val chip = Chip(this).apply {
                text = address
                setTextColor(getColor(R.color.b500))
                setChipDrawable(ChipDrawable.createFromAttributes(this@CheckListLocationActivity, null, 0, R.style.CustomChipCloseStyle))
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.chipGroup.removeView(this)
                    code?.let { code -> selectedLocations.remove(code) } // 코드 삭제
                    if (selectedLocations.isEmpty()) {
                        binding.activityChecklistLocationCl.visibility = View.VISIBLE
                    }
                    updateButtonStates()
                }
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            binding.chipGroup.addView(chip)
            binding.activityChecklistLocationCl.visibility = View.GONE
            updateButtonStates()
        }
    }

    private fun updateButtonStates() {
        binding.checklistspotLocationPlusBt.isEnabled = selectedLocations.size < 10
        binding.checklistspotLocationFinishBt.isEnabled = selectedLocations.isNotEmpty()
    }
}

