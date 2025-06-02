package com.example.spoteam_android.presentation.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentConsiderAttendanceBinding
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.community.MyRecruitingStudiesResponse
import com.example.spoteam_android.presentation.study.register.theme.ThemeStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsiderAttendanceFragment : Fragment() {
    private lateinit var binding: FragmentConsiderAttendanceBinding

    private var memberId: Int = -1
    private var currentPage = 0
    private val size = 5
    private var totalPages = 0

    private lateinit var adapter: ConsiderAttendanceContentRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConsiderAttendanceBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        memberId = if (email != null) sharedPreferences.getInt("${email}_memberId", -1) else -1

        binding.prevIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.makeStudyTv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, ThemeStudyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        initRecyclerView()
        fetchMyRecruitingStudies()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.fragmentConsiderAttendanceRv.layoutManager = LinearLayoutManager(context)

        adapter = ConsiderAttendanceContentRVAdapter(
            dataList = emptyList(),
            onItemClick = { data ->
                val fragment = ConsiderAttendanceMemberFragment().apply {
                    arguments = Bundle().apply {
                        putInt("recruitingStudyId", data.studyId)
                    }
                }
                (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            },
            onPageClick = { page ->
                currentPage = page
                fetchMyRecruitingStudies()
            },
            currentPageProvider = { currentPage },
            totalPagesProvider = { totalPages }
        )

        binding.fragmentConsiderAttendanceRv.adapter = adapter
    }

    private fun fetchMyRecruitingStudies() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMyPageRecruitingStudy(currentPage, size)
            .enqueue(object : Callback<MyRecruitingStudiesResponse> {
                override fun onResponse(
                    call: Call<MyRecruitingStudiesResponse>,
                    response: Response<MyRecruitingStudiesResponse>
                ) {
                    if (response.isSuccessful) {
                        val recruitingStudyResponse = response.body()
                        if (recruitingStudyResponse?.isSuccess == true) {
                            val result = recruitingStudyResponse.result
                            totalPages = result.totalPages

                            if (result.content.isNotEmpty()) {
                                binding.emptyRecruiting.visibility = View.GONE
                                binding.fragmentConsiderAttendanceRv.visibility = View.VISIBLE
                                adapter.updateList(result.content)
                            } else {
                                binding.emptyRecruiting.visibility = View.VISIBLE
                                binding.fragmentConsiderAttendanceRv.visibility = View.GONE
                            }
                        } else {
                            showError(recruitingStudyResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<MyRecruitingStudiesResponse>, t: Throwable) {
                    Log.e("MyRecruitingStudy", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }
}
