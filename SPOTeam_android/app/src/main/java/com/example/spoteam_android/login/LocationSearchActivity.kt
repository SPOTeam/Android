package com.example.spoteam_android.login

import LocationSearchAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.ActivityLocationSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Timer
import java.util.TimerTask

class LocationSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationSearchBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private val locationItemList = mutableListOf<LocationItem>()
    private var searchTimer: Timer? = null // 디바운스 타이머

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.activityChecklistLocationSearchTb)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        binding.activityChecklistLocationSearchTb.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        setupSearchFunctionality()

        // TSV 데이터를 백그라운드에서 파싱
        lifecycleScope.launch(Dispatchers.IO) {
            val data = parseTsvDataFromAssets()
            withContext(Dispatchers.Main) {
                locationItemList.clear()
                locationItemList.addAll(data)
                locationSearchAdapter.notifyDataSetChanged()
                binding.activityLocationRv.visibility = if (locationSearchAdapter.itemCount > 0) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        locationSearchAdapter = LocationSearchAdapter(locationItemList) { item ->
            val resultIntent = Intent().apply {
                putExtra("SELECTED_ADDRESS", item.address)
                putExtra("SELECTED_CODE", item.code)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.activityLocationRv.apply {
            layoutManager = LinearLayoutManager(this@LocationSearchActivity)
            adapter = locationSearchAdapter
            visibility = View.GONE
        }
    }

    private fun setupSearchFunctionality() {
        binding.activityLocationSearchEt.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.activityLocationSearchEt.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.activityLocationSearchEt.right - drawableEnd.bounds.width())) {
                    performSearch()
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.activityLocationSearchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 디바운스: 일정 시간 지난 후 검색 실행
                searchTimer?.cancel()
                searchTimer = Timer().apply {
                    schedule(object : TimerTask() {
                        override fun run() {
                            runOnUiThread { performSearch() }
                        }
                    }, 300) // 300ms 후에 검색
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * Assets에서 TSV 데이터를 읽어 리스트로 반환
     */
    private suspend fun parseTsvDataFromAssets(): List<LocationItem> {
        return withContext(Dispatchers.IO) {
            val inputStream = assets.open("region_data_processed.tsv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val tsvData = reader.use { it.readText() }

            val rows = tsvData.split("\n").drop(1)
            val list = mutableListOf<LocationItem>()
            for (row in rows) {
                val columns = row.split("\t")
                if (columns.size >= 5) {
                    list.add(
                        LocationItem(
                            code = columns[0],
                            city = columns[1],
                            districtCity = columns[2],
                            districtGu = columns[3],
                            subdistrict = columns[4]
                        )
                    )
                }
            }
            list
        }
    }

    /**
     * 현재 쿼리를 기준으로 데이터를 필터링
     */
    private fun performSearch() {
        val query = binding.activityLocationSearchEt.text.toString().trim()

        lifecycleScope.launch(Dispatchers.Default) {
            val filteredList = if (query.isEmpty()) {
                locationItemList
            } else {
                locationItemList.filter {
                    it.city.contains(query, ignoreCase = true) ||
                            it.districtCity.contains(query, ignoreCase = true) ||
                            it.districtGu.contains(query, ignoreCase = true) ||
                            it.subdistrict.contains(query, ignoreCase = true)
                }
            }

            withContext(Dispatchers.Main) {
                locationSearchAdapter.updateList(filteredList)
                binding.activityLocationRv.visibility = if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}
