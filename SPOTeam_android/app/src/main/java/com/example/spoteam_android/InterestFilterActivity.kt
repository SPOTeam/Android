package com.example.spoteam_android

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import org.w3c.dom.Text

class InterestFilterActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_filter)


        val spinner: Spinner = findViewById(R.id.gender_spinner)

        ArrayAdapter.createFromResource(
            this,

            // R.array.gender_array 는 2번에서 설정한 string-array 태그의 name 입니다.
            R.array.gender_array,

            // android.R.layout.simple_spinner_dropdown_item 은 android 에서 기본 제공
            // 되는 layout 입니다. 이 부분은 "선택된 item" 부분의 layout을 결정합니다.
            android.R.layout.simple_spinner_dropdown_item

        ).also { adapter ->

            // android.R.layout.simple_spinner_dropdown_item 도 android 에서 기본 제공
            // 되는 layout 입니다. 이 부분은 "선택할 item 목록" 부분의 layout을 결정합니다.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

        }


        val ageRangeSlider = findViewById<RangeSlider>(R.id.ageRangeSlider)
        val minValueText = findViewById<TextView>(R.id.minValueText)
        val maxValueText = findViewById<TextView>(R.id.maxValueText)

        ageRangeSlider.valueFrom = 18f
        ageRangeSlider.valueTo = 60f
        ageRangeSlider.stepSize = 1f
        ageRangeSlider.values = listOf(18f, 25f)

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minValueText.text = values[0].toInt().toString()
            maxValueText.text = values[1].toInt().toString()
        }
//        개별 칩으로 하는 버전
//        val chip1 = findViewById<Chip>(R.id.chip1)
//        val chip2 = findViewById<Chip>(R.id.chip2)
//
//        chip1.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                chip1.setChipBackgroundColorResource(R.color.active_blue)
//                chip1.setTextColor(getResources().getColor(R.color.white, null))
//            } else {
//                chip1.setChipBackgroundColorResource(android.R.color.transparent)
//                chip1.setTextColor(getResources().getColor(R.color.active_blue, null))
//            }
//        }
//
//        chip2.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                chip2.setChipBackgroundColorResource(R.color.active_blue)
//                chip2.setTextColor(getResources().getColor(R.color.white, null))
//            } else {
//                chip2.setChipBackgroundColorResource(android.R.color.transparent)
//                chip2.setTextColor(getResources().getColor(R.color.active_blue, null))
//            }
//
//        }


        val chipGroup1 = findViewById<ChipGroup>(R.id.chipGroup1)
        val editText = findViewById<EditText>(R.id.edittext1)
        val behindet = findViewById<TextView>(R.id.behind_et)

        chipGroup1.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val checkedChip = group.findViewById<Chip>(checkedId)
                if (checkedChip.id == R.id.chip1) {
                    editText.visibility = View.VISIBLE
                    behindet.visibility = View.VISIBLE
                } else {
                    editText.visibility = View.GONE
                    behindet.visibility = View.GONE
                }
            } else {
                editText.visibility = View.GONE
                behindet.visibility = View.GONE
            }
        }

        val chipGroup2 = findViewById<ChipGroup>(R.id.chipGroup2)

        chipGroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val checkedChip = group.findViewById<Chip>(checkedId)
                checkedChip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
//                        checkedChip.setChipBackgroundColorResource(R.color.active_blue)
//                        checkedChip.setTextColor(getResources().getColor(R.color.white, null))
                    } else {
//                        checkedChip.setChipBackgroundColorResource(android.R.color.transparent)
//                        checkedChip.setTextColor(getResources().getColor(R.color.active_blue, null))
                    }
                }
            }
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = HouseFragment()
                }
                R.id.navigation_category -> {
                    selectedFragment = CategoryFragment()
                }
                R.id.navigation_study -> {
                    selectedFragment = StudyFragment()
                }
                R.id.navigation_bookmark -> {
                    selectedFragment = BookmarkFragment()
                }
                R.id.navigation_mypage -> {
                    selectedFragment = MyPageFragment()
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            }
            true
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }


    }
}