package com.example.spoteam_android.ui.mypage

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
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MyRecruitingStudiesResponse
import com.example.spoteam_android.ui.community.MyRecruitingStudyDetail
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsiderAttendanceFragment : Fragment() {
    private lateinit var binding: FragmentConsiderAttendanceBinding

    private var memberId : Int = -1
    private var currentPage = 0
    private val size = 5
    private var totalPages = 0
    private var startPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConsiderAttendanceBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        memberId = if (email != null) sharedPreferences.getInt("${email}_memberId", -1) else -1


        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.makeStudyTv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, RegisterStudyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        fetchMyRecruitingStudies()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    fetchMyRecruitingStudies()
                }
            }
        }

        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchMyRecruitingStudies()
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                fetchMyRecruitingStudies()
            }
        }
        fetchMyRecruitingStudies()
    }

    private fun fetchMyRecruitingStudies() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMyPageRecruitingStudy(currentPage, size)
            .enqueue(object : Callback<MyRecruitingStudiesResponse> {
                override fun onResponse(
                    call: Call<MyRecruitingStudiesResponse>,
                    response: Response<MyRecruitingStudiesResponse>
                ) {
                    Log.d("MyRecruitingStudy", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val recruitingStudyResponse = response.body()
                        Log.d("MyRecruitingStudy", "responseBody: ${recruitingStudyResponse?.isSuccess}")
                        if (recruitingStudyResponse?.isSuccess == "true") {
                            val result = recruitingStudyResponse.result
                            totalPages = recruitingStudyResponse.result.totalPages

                            if(result.content.isNotEmpty()) {
                                binding.emptyRecruiting.visibility = View.GONE
                                binding.fragmentConsiderAttendanceRv.visibility = View.VISIBLE

                                initRecyclerView(result.content)
                            } else {
                                binding.emptyRecruiting.visibility = View.VISIBLE
                                binding.fragmentConsiderAttendanceRv.visibility = View.GONE
                            }
                            updatePageUI()
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

    private fun initRecyclerView(content: List<MyRecruitingStudyDetail>) {
        binding.fragmentConsiderAttendanceRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = ConsiderAttendanceContentRVAdapter(content)
        //리스너 객체 생성 및 전달

        binding.fragmentConsiderAttendanceRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object : ConsiderAttendanceContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: MyRecruitingStudyDetail) {
                val fragment = ConsiderAttendanceMemberFragment()

                val bundle = Bundle()
                bundle.putInt("recruitingStudyId", data.studyId)
                fragment.arguments = bundle

                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }

        })
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun updatePageUI() {
        startPage = if (currentPage <= 2) {
            0
        } else {
            maxOf(totalPages - 5, maxOf(0, currentPage - 2))
        }

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum < totalPages) {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(
                    if (pageNum == currentPage) R.drawable.btn_page_bg else 0
                )
                textView.isEnabled = true
                textView.alpha = 1.0f
                textView.visibility = View.VISIBLE
            } else {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(0)
                textView.isEnabled = false // 클릭 안 되게
                textView.alpha = 0.3f
                textView.visibility = View.VISIBLE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
        binding.nextPage.isEnabled = currentPage < totalPages - 1
    }
}