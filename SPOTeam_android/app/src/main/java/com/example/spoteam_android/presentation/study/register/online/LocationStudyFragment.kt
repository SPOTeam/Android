package com.example.spoteam_android.presentation.study.register.online

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentLocationStudyBinding
import com.example.spoteam_android.presentation.login.checklist.LocationItem
import com.example.spoteam_android.presentation.login.checklist.LocationSearchAdapter

class LocationStudyFragment : Fragment() {

    private lateinit var binding: FragmentLocationStudyBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private val locationItemList = mutableListOf<LocationItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationStudyBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchFunctionality()
        loadLocalTsvFile()

        binding.fragmentLocationStudyBackBt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        locationSearchAdapter = LocationSearchAdapter(locationItemList) { item ->
            openOnlineStudyFragment(item.address, item.code)
        }
        binding.activityLocationRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationSearchAdapter
            visibility = View.GONE
        }
    }

    private fun setupSearchFunctionality() {
        binding.activityLocationSearchEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.activityLocationSearchEt.compoundDrawables[2]
                if (drawableEnd != null &&
                    event.rawX >= (binding.activityLocationSearchEt.right - drawableEnd.bounds.width())
                ) {
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
        val inputStream = requireContext().assets.open("region_data_processed.tsv")
        val reader = inputStream.bufferedReader()
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
        binding.activityLocationRv.visibility =
            if (locationSearchAdapter.itemCount > 0) View.VISIBLE else View.GONE
    }

    private fun performSearch() {
        val query = binding.activityLocationSearchEt.text.toString()
        locationSearchAdapter.filter(query)
        binding.activityLocationRv.visibility =
            if (locationSearchAdapter.itemCount > 0) View.VISIBLE else View.GONE
    }

    private fun openOnlineStudyFragment(address: String, code: String) {
        val fragment = OnlineStudyFragment()
        val bundle = Bundle().apply {
            putString("ADDRESS", address)
            putBoolean("IS_OFFLINE", true)
            putString("CODE", code)
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commit()
    }
}
