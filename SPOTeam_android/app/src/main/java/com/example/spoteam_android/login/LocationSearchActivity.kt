package com.example.spoteam_android.login

import LocationSearchAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.ActivityLocationSearchBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class LocationSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationSearchBinding
    private lateinit var adapter: LocationSearchAdapter
    private val locationItemList = mutableListOf<LocationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadLocalTsvFile() // TSV 파일을 읽어 데이터 초기화

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
                // 텍스트 변경 시 자동으로 검색 수행
                performSearch()
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = LocationSearchAdapter(locationItemList) { item ->
            // 아이템 클릭 시 처리
            // 예를 들어, 아이템 클릭 시 어떤 액션을 수행할 수 있습니다.
        }
        binding.activityLocationRv.layoutManager = LinearLayoutManager(this)
        binding.activityLocationRv.adapter = adapter

        // RecyclerView를 처음에는 숨기기
        binding.activityLocationRv.visibility = View.GONE
    }

    private fun loadLocalTsvFile() {
        val inputStream = assets.open("region_data_processed.tsv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val tsvData = reader.use { it.readText() }
        parseTsvData(tsvData)
    }

    private fun parseTsvData(tsvData: String) {
        val rows = tsvData.split("\n").drop(1) // 첫 번째 줄은 헤더이므로 제외

        locationItemList.clear()
        for (row in rows) {
            val columns = row.split("\t")
            if (columns.size >= 5) {
                val item = LocationItem(
                    code = columns[0],
                    city = columns[1],
                    districtCity = columns[2],
                    districtGu = columns[3],
                    subdistrict = columns[4]
                )
                locationItemList.add(item)
            }
        }

        adapter.notifyDataSetChanged()

        // 검색 결과가 있을 때 RecyclerView를 보이게 함
        binding.activityLocationRv.visibility = if (adapter.itemCount > 0) View.VISIBLE else View.GONE
    }

    private fun performSearch() {
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
