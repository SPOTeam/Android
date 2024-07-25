package com.example.spoteam_android.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.LocationSearchAdapter
import com.example.spoteam_android.databinding.ActivityLocationSearchBinding

class LocationSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationSearchBinding
    private lateinit var adapter: LocationSearchAdapter
    // 예시 데이터
    private val dataList = listOf(
        LocationItem("유원빌딩", "서울특별시 중구 서소문동 서소문로 116"),
        LocationItem("대한항공빌딩", "서울특별시 중구 서소문동 서소문로 117")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        adapter = LocationSearchAdapter(dataList)
        binding.activityLocationRv.layoutManager = LinearLayoutManager(this)
        binding.activityLocationRv.adapter = adapter

        // RecyclerView를 처음에는 숨기기
        binding.activityLocationRv.visibility = View.GONE

        // 드로어블 클릭 리스너 설정
        binding.activityLocationSearchEt.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.activityLocationSearchEt.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.activityLocationSearchEt.right - drawableEnd.bounds.width())) {
                    // 드로어블 클릭 시 검색 수행
                    performSearch()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // 텍스트 변경 감지 및 필터링 설정
        binding.activityLocationSearchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 시 필터링은 하지 않음
            }
        })
    }

    private fun performSearch() {
        // 검색 수행 로직
        val query = binding.activityLocationSearchEt.text.toString()
        adapter.filter(query)

        // 검색 결과가 있을 때 RecyclerView를 보이게 함
        if (adapter.itemCount > 0) {
            binding.activityLocationRv.visibility = View.VISIBLE
        } else {
            binding.activityLocationRv.visibility = View.GONE
        }
    }
}