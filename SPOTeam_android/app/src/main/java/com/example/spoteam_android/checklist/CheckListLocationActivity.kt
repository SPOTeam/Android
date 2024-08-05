package com.example.spoteam_android.checklist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityCheckListLocationBinding
import com.example.spoteam_android.login.LocationSearchActivity

class CheckListLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckListLocationBinding

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val address = data.getStringExtra("SELECTED_ADDRESS")
                val code = data.getStringExtra("SELECTED_CODE")
                displaySelectedLocation(address, code)
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
            val intent = Intent(this, LocationSearchActivity::class.java)
            startForResult.launch(intent)
        }

        binding.locationChip.setOnCloseIconClickListener {
            binding.locationChip.visibility = View.GONE
            binding.activityChecklistLocationCl.visibility = View.VISIBLE
        }

        //추가 버튼 클릭시
        binding.checklistspotLocationPlusBt.setOnClickListener {
            val intent = Intent(this, LocationSearchActivity::class.java)
            startForResult.launch(intent)
        }

        binding.checklistspotLocationFinishBt.setOnClickListener {
            // Complete button click logic here
            // Do something when the complete button is clicked
        }

    }

    private fun displaySelectedLocation(address: String?, code: String?) {
        if (!address.isNullOrEmpty()) {
            binding.locationChip.text = address
            binding.locationChip.visibility = View.VISIBLE
            binding.activityChecklistLocationCl.visibility = View.GONE
        }
    }
}
