package com.example.spoteam_android.presentation.mypage

import com.example.spoteam_android.presentation.login.checklist.LocationSearchAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMyPageLocationBinding
import com.example.spoteam_android.presentation.login.checklist.LocationItem
import java.io.BufferedReader
import java.io.InputStreamReader

class MyPageLocationFragment : Fragment() {
    private lateinit var binding: FragmentMyPageLocationBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private val locationItemList = mutableListOf<LocationItem>()
    private val selectedRegions = mutableListOf<String>() // 선택된 지역 리스트
    private val selectedCodes = mutableListOf<String>()   // 선택된 지역 코드 리스트
    private var isEditMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageLocationBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchFunctionality()
        loadLocalTsvFile() // TSV 파일을 읽어 데이터 초기화

        // 이전 프래그먼트에서 전달된 데이터를 처리
        isEditMode = arguments?.getBoolean("IS_EDIT_MODE", false) ?: false
        val receivedRegions = arguments?.getStringArrayList("SELECTED_REGIONS")
        val receivedCodes = arguments?.getStringArrayList("SELECTED_CODES") // 코드도 받음

        if (receivedRegions != null) {
            selectedRegions.addAll(receivedRegions)
        }
        if (receivedCodes != null) {
            selectedCodes.addAll(receivedCodes) // 받은 코드 추가
        }

        binding.fragmentIntroduceStudyBackBt.setOnClickListener {
            goToPreviusFragment()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        locationSearchAdapter = LocationSearchAdapter(locationItemList) { item ->
            onLocationItemSelected(item.address, item.code)
        }
        binding.activityLocationRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationSearchAdapter
            visibility = View.GONE // 초기에는 RecyclerView를 숨김
        }
    }

    private fun onLocationItemSelected(address: String, code: String) {
        // 선택된 코드가 이미 리스트에 있는지 확인
        if (selectedCodes.contains(code)) {
            // 중복된 코드가 있을 경우 Toast 메시지 표시
            Toast.makeText(requireContext(), "이미 선택된 지역입니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 선택된 지역 정보와 코드 리스트에 새로운 값을 추가
            selectedRegions.add(address)
            selectedCodes.add(code)

            // TemporaryRegionFragment로 전달할 데이터 생성
            val fragment = com.example.spoteam_android.presentation.mypage.TemporaryRegionFragment()
            val bundle = Bundle().apply {
                putStringArrayList("SELECTED_REGIONS", ArrayList(selectedRegions))
                putStringArrayList("SELECTED_CODES", ArrayList(selectedCodes))
            }
            fragment.arguments = bundle

            // 프래그먼트 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .addToBackStack(null) // 백스택에 추가하여 뒤로가기 시 원래 Fragment로 복귀 가능
                .commit()
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
        val inputStream = requireContext().assets.open("region_data_processed.tsv")
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

    private fun goToPreviusFragment() {
        parentFragmentManager.popBackStack()
    }
}
