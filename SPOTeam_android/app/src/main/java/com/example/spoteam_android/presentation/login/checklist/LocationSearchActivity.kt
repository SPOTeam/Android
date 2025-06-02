package com.example.spoteam_android.presentation.login.checklist

import android.content.Intent
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
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private val locationItemList = mutableListOf<LocationItem>()

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
        loadLocalTsvFile()
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                performSearch()
            }
        })
    }

    private fun loadLocalTsvFile() {
        val inputStream = assets.open("region_data_processed.tsv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val tsvData = reader.use { it.readText() }
        parseTsvData(tsvData)
    }

    private fun parseTsvData(tsvData: String) {
        val rows = tsvData.split("\n").drop(1)

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

        locationSearchAdapter.notifyDataSetChanged()
        binding.activityLocationRv.visibility = if (locationSearchAdapter.itemCount > 0) View.VISIBLE else View.GONE
    }

    private fun performSearch() {
        val query = binding.activityLocationSearchEt.text.toString()
        locationSearchAdapter.filter(query)
        binding.activityLocationRv.visibility = if (locationSearchAdapter.itemCount > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}