package com.example.spoteam_android.ui.study

import StudyAdapter
import StudyApiService
import StudyViewModel
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.spoteam_android.LikeResponse

import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.StudyResponse
import com.example.spoteam_android.databinding.FragmentStudyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private lateinit var studyAdapter: StudyAdapter
    private var itemList = ArrayList<StudyItem>()
    private var currentPage = 0
    private val size = 5 // 페이지당 항목 수
    private var totalPages = 0
    private val studyViewModel: StudyViewModel by activityViewModels()

    // Retrofit API Service
    private lateinit var studyApiService: StudyApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)

        binding.fragmentStudyTv.setOnClickListener {
            // 현재 Fragment를 백스택에서 제거하고 이전 Fragment로 돌아갑니다.
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        // 어댑터 설정
        studyAdapter = StudyAdapter(ArrayList(), onItemClick = { selectedItem ->
            // StudyViewModel에 studyId 설정
            studyViewModel.setStudyData(selectedItem.studyId, selectedItem.imageUrl, selectedItem.introduction)

            val detailStudyFragment = DetailStudyFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, detailStudyFragment)
                .addToBackStack(null)
                .commit()
        }, onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        // RecyclerView 설정
        binding.fragmentStudyRv.apply {
            adapter = studyAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // 페이지 전환 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchStudyData() // 이전 페이지 데이터를 가져옴
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < getTotalPages() - 1) {
                currentPage++
                fetchStudyData() // 다음 페이지 데이터를 가져옴
            }
        }

        // 초기 데이터 로드
        fetchStudyData()
    }

    private fun fetchStudyData() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (memberId != -1) {
                Log.d("StudyFragment", "Fetching data for memberId: $memberId, page: $currentPage, size: $size")

                studyApiService.getStudies(memberId.toString(), currentPage, size).enqueue(object : Callback<StudyResponse> {
                    override fun onResponse(call: Call<StudyResponse>, response: Response<StudyResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { studyResponse ->
                                val content = studyResponse.result.content
                                if (!content.isNullOrEmpty()) {
                                    Log.d("StudyFragment", "Received data: ${content.size} items")

                                    itemList.clear()
                                    itemList.addAll(content.map {
                                        StudyItem(
                                            studyId = it.studyId,
                                            title = it.title,
                                            goal = it.goal,
                                            introduction = it.introduction,
                                            memberCount = it.memberCount,
                                            heartCount = it.heartCount,
                                            hitNum = it.hitNum,
                                            maxPeople = it.maxPeople,
                                            studyState = it.studyState,
                                            themeTypes = it.themeTypes,
                                            regions = it.regions,
                                            imageUrl = it.imageUrl,
                                            liked = it.liked // Boolean 값으로 받음
                                        )
                                    })

                                    totalPages = studyResponse.result.totalPages

                                    // RecyclerView 업데이트
                                    studyAdapter.updateList(itemList)
                                    Log.d("StudyFragment", "List updated with new data.")

                                    binding.fragmentStudyRv.visibility = View.VISIBLE
                                    binding.emptyMessage.visibility = View.GONE

                                    // 페이지 UI 업데이트
                                    updatePageNumberUI(studyResponse.result.totalPages)
                                } else {
                                    // 데이터가 없을 때 처리
                                    Log.d("StudyFragment", "No studies found.")
                                    binding.fragmentStudyRv.visibility = View.GONE
                                    binding.emptyMessage.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            Log.d("StudyFragment", "Response failed: ${response.code()}")
                            Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<StudyResponse>, t: Throwable) {
                        Log.d("StudyFragment", "Error: ${t.message}")
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleLikeStatus(studyItem: StudyItem, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId, memberId).enqueue(object : Callback<LikeResponse> {
                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { likeResponse ->
                            // 서버에서 반환된 상태에 따라 하트 아이콘 및 StudyItem의 liked 상태 업데이트
                            val newStatus = likeResponse.result.status
                            Log.d("studyfrag", newStatus)
                            studyItem.liked = newStatus == "LIKE" // Boolean 값으로 업데이트
                            val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                            likeButton.setImageResource(newIcon)

                            // heartCount 즉시 증가 또는 감소
                            studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1
                            studyAdapter.notifyItemChanged(itemList.indexOf(studyItem))
                        }
                    } else {
                        Toast.makeText(requireContext(), "찜 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePageNumberUI(totalPages: Int) {
        binding.currentPage.text = "${currentPage + 1}"

        binding.previousPage.isEnabled = currentPage > 0
        binding.previousPage.setTextColor(resources.getColor(
            if (currentPage > 0) R.color.active_color else R.color.disabled_color,
            null
        ))

        // 다음 페이지 버튼 활성화/비활성화
        binding.nextPage.isEnabled = currentPage < totalPages - 1
        binding.nextPage.setTextColor(resources.getColor(
            if (currentPage < totalPages - 1) R.color.active_color else R.color.disabled_color,
            null
        ))
    }

    private fun getTotalPages(): Int {
        return totalPages // 올바른 페이지 수 계산
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
