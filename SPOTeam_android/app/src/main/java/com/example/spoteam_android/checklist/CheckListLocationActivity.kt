package com.example.spoteam_android.checklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private val selectedLocations = mutableListOf<String>()

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

        setSupportActionBar(binding.activityChecklistLocationTb)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        binding.activityChecklistLocationTb.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.checklistspotLocationPlusBt.setOnClickListener {
            if (selectedLocations.size < 5) {
                val intent = Intent(this, LocationSearchActivity::class.java)
                startForResult.launch(intent)
            } else {

            }
        }

        binding.checklistspotLocationFinishBt.setOnClickListener {
            val intent = Intent(this, RegisterInformation::class.java)
            startActivity(intent)
        }

        updateButtonStates()
    }

    private fun addLocationChip(address: String?, code: String?) {
        if (!address.isNullOrEmpty() && !selectedLocations.contains(address)) {
            selectedLocations.add(address)

            val chip = Chip(this).apply {
                text = address
                setTextColor(getColor(R.color.active_blue))
                setChipDrawable(ChipDrawable.createFromAttributes(this@CheckListLocationActivity, null, 0, R.style.CustomChipCloseStyle))
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.chipGroup.removeView(this)
                    selectedLocations.remove(address)
                    if (selectedLocations.isEmpty()) {
                        binding.activityChecklistLocationCl.visibility = View.VISIBLE
                    }

                    updateButtonStates()
                }
            }
            binding.chipGroup.addView(chip)
            binding.activityChecklistLocationCl.visibility = View.GONE
            updateButtonStates()
        }
    }

    private fun updateButtonStates() {
        // `selectedLocations`의 크기에 따라 버튼 상태 업데이트
        binding.checklistspotLocationPlusBt.isEnabled = selectedLocations.size < 5
        binding.checklistspotLocationFinishBt.isEnabled = selectedLocations.isNotEmpty()
    }

}
