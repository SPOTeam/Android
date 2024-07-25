package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.LocationSearchAdapter
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentLocationStudyBinding
import com.example.spoteam_android.login.LocationItem

class LocationStudyFragment : Fragment() {

    private lateinit var binding: FragmentLocationStudyBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private val locationItemList = mutableListOf(
        LocationItem("유원빌딩", "서울특별시 중구 서소문동 서소문로 116"),
        LocationItem("대한항공빌딩", "서울특별시 중구 서소문동 서소문로 117")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationStudyBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchFunctionality()

        return binding.root
    }

    private fun setupRecyclerView() {
        locationSearchAdapter = LocationSearchAdapter(locationItemList) { item ->
            // 아이템 클릭 시 처리
            openOnlineStudyFragment(item.address)
        }
        binding.activityLocationRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationSearchAdapter
            visibility = View.GONE // 초기에는 RecyclerView를 숨김
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
            override fun afterTextChanged(s: Editable?) {}
        })
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

    private fun openOnlineStudyFragment(address: String) {
        val fragment = OnlineStudyFragment()
        val bundle = Bundle().apply {
            putString("ADDRESS", address)
            putBoolean("IS_OFFLINE", true) // "오프라인" 버튼 클릭 상태로 설정
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commit()
    }
}
