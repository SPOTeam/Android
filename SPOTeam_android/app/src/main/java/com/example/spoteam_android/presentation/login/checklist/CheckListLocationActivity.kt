package com.example.spoteam_android.presentation.login.checklist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityCheckListLocationBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.example.spoteam_android.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckListLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckListLocationBinding
    private val selectedLocations = mutableListOf<String>()

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val code = result.data?.getStringExtra("SELECTED_CODE") ?: return@registerForActivityResult
            val address = result.data?.getStringExtra("SELECTED_ADDRESS") ?: return@registerForActivityResult
            addChip(code, address)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.activityChecklistLocationTb)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        binding.activityChecklistLocationTb.setNavigationOnClickListener {
            finish()
        }

        val selectedThemes = intent.getStringArrayListExtra("selectedThemes")
        val selectedPurposes = intent.getIntegerArrayListExtra("selectedPurposes")

        binding.checklistspotLocationPlusBt.setOnClickListener {
            if (selectedLocations.size < 5) {
                resultLauncher.launch(Intent(this, LocationSearchActivity::class.java))
            }
        }

        binding.checklistspotLocationFinishBt.setOnClickListener {
            val intent = Intent(this, RegisterInformation::class.java).apply {
                putExtra("mode", "FINAL")
                putStringArrayListExtra("selectedThemes", selectedThemes)
                putIntegerArrayListExtra("selectedPurposes", selectedPurposes)
                putStringArrayListExtra("selectedRegions", ArrayList(selectedLocations))
            }
            startActivity(intent)
        }
        updateButtonStates()
    }

    private fun addChip(code: String, address: String) {
        if (!selectedLocations.contains(code)) {
            selectedLocations.add(code)

            val chip = Chip(this).apply {
                text = address
                setTextColor(getColor(R.color.b500))
                setChipDrawable(
                    ChipDrawable.createFromAttributes(
                        this@CheckListLocationActivity,
                        null,
                        0,
                        R.style.CustomChipCloseStyle
                    )
                )
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    binding.chipGroup.removeView(this)
                    selectedLocations.remove(code)
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
        binding.checklistspotLocationPlusBt.isEnabled = selectedLocations.size < 5
        binding.checklistspotLocationFinishBt.isEnabled = selectedLocations.isNotEmpty()
    }
}