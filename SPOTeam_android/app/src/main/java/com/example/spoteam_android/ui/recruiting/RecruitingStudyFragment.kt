package com.example.spoteam_android.ui.recruiting

import RetrofitClient.getAuthToken
import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentRecruitingStudyBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestVPAdapter
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitingStudyFragment : Fragment() {

    lateinit var binding: FragmentRecruitingStudyBinding
    private lateinit var recruitingStudyAdapter: InterestVPAdapter
    private lateinit var studyApiService: StudyApiService
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecruitingStudyBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        recruitingStudyAdapter = InterestVPAdapter(
            ArrayList(),
            onLikeClick = { selectedItem, likeButton ->
                toggleLikeStatus(selectedItem, likeButton)
            }
        )

        recruitingStudyAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                // Fragment 전환
                val fragment = DetailStudyFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        binding.recruitingStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recruitingStudyAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()

        val gender = arguments?.getString("gender2")
        val minAge = arguments?.getString("minAge2")
        val maxAge = arguments?.getString("maxAge2")
        val activityFee01 = arguments?.getString("activityFee_01")
        val activityFee02 = arguments?.getString("activityFee_02")
        val selectedStudyTheme = arguments?.getString("selectedStudyTheme2")
        val activityFeeinput = arguments?.getString("activityFeeAmount2")
        val source = arguments?.getString("source")
        val mysource = arguments?.getString("mysource")

        val spinner: Spinner = binding.filterToggle

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_study,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        binding.icFindRecruiting.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.spotLogo.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HouseFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icAlarmRecruiting.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        when (mysource) {
            "HouseFragment" -> {
                fetchAllRecruiting("LIKED")
                spinner.setSelection(4)
            }
        }

        when (source) {
            "RecruitingStudyFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE

                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && selectedStudyTheme != null && activityFee01 != null) {
                    fetchSpecificRecruiting(
                        "ALL",
                        gender,
                        minAge,
                        maxAge,
                        activityFee02,
                        activityFeeinput ?: "",
                        selectedStudyTheme,
                        activityFee01
                    )
                }
            }

            else -> {
                fetchAllRecruiting("ALL")
                binding.icFilterActive.visibility = View.GONE
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val source = arguments?.getString("source")
                when (source) {
                    "HouseFragment" -> {
                        // HouseFragment에서 접근했을 때의 API 처리
                        when (position) {
                            0 -> fetchAllRecruiting("ALL")
                            1 -> fetchAllRecruiting("RECRUITING")
                            2 -> fetchAllRecruiting("COMPLETED")
                            3 -> fetchAllRecruiting("HIT")
                            4 -> fetchAllRecruiting("LIKED")
                        }
                    }
                    "RecruitingStudyFilterFragment" -> {
                        if (gender != null && minAge != null && maxAge != null && activityFee02 != null && selectedStudyTheme != null && activityFee01 != null) {
                            fetchSpecificRecruiting(
                                when (position) {
                                    1 -> "RECRUITING"
                                    2 -> "COMPLETED"
                                    3 -> "HIT"
                                    4 -> "LIKED"
                                    else -> "ALL"
                                },
                                gender,
                                minAge,
                                maxAge,
                                activityFee02,
                                activityFeeinput ?: "",
                                selectedStudyTheme,
                                activityFee01
                            )
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val filter: ImageView = binding.icFilter
        filter.setOnClickListener {
            bundle.putString("source", "RecruitingStudyFragment")
            val recruitingStudyFilterFragment = RecruitingStudyFilterFragment().apply {
                arguments = bundle // Bundle 전달
            }

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, recruitingStudyFilterFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchAllRecruiting(selectedItem: String) {
        val boardItems = arrayListOf<BoardItem>()

        RetrofitClient.RSService.GetRecruitingStudy(
            authToken = getAuthToken(),
            gender = "MALE",
            minAge = 18,
            maxAge = 60,
            isOnline = false,
            hasFee = false,
            fee = null,
            page = 0,
            size = 5,
            sortBy = selectedItem
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result?.content?.forEach { study ->
                            val boardItem = BoardItem(
                                studyId = study.studyId,
                                title = study.title,
                                goal = study.goal,
                                introduction = study.introduction,
                                memberCount = study.memberCount,
                                heartCount = study.heartCount,
                                hitNum = study.hitNum,
                                maxPeople = study.maxPeople,
                                studyState = study.studyState,
                                themeTypes = study.themeTypes,
                                regions = study.regions,
                                imageUrl = study.imageUrl,
                                liked = study.liked
                            )
                            boardItems.add(boardItem)
                        }
                        updateRecyclerView(boardItems)
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                        binding.recruitingStudyReyclerview.visibility = View.VISIBLE
                    } else {
                        binding.checkAmount.text = "0건"
                        binding.recruitingStudyReyclerview.visibility = View.GONE
                        showNoResults()

                    }
                } else {
                    showError(response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showError(t.message)
            }
        })
    }

    private fun fetchSpecificRecruiting(
        selectedItem: String,
        gender: String,
        minAge: String,
        maxAge: String,
        activityFee: String,
        activityFeeAmount: String,
        selectedStudyTheme: String,
        isOnline: String
    ) {
        val boardItems = arrayListOf<BoardItem>()

        RetrofitClient.RSService.GetRecruitingStudy(
            authToken = getAuthToken(),
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = isOnline.toBoolean(),
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount.toIntOrNull(),
            page = 0,
            size = 5,
            sortBy = selectedItem
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result?.content?.forEach { study ->
                            val boardItem = BoardItem(
                                studyId = study.studyId,
                                title = study.title,
                                goal = study.goal,
                                introduction = study.introduction,
                                memberCount = study.memberCount,
                                heartCount = study.heartCount,
                                hitNum = study.hitNum,
                                maxPeople = study.maxPeople,
                                studyState = study.studyState,
                                themeTypes = study.themeTypes,
                                regions = study.regions,
                                imageUrl = study.imageUrl,
                                liked = study.liked
                            )
                            boardItems.add(boardItem)
                        }
                        updateRecyclerView(boardItems)
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                    } else {
                        binding.checkAmount.text = "0건"
                        showNoResults()
                    }
                } else {
                    showError(response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showError(t.message)
            }
        })
    }

    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        recruitingStudyAdapter.updateList(boardItems)
    }

    private fun showNoResults() {
        binding.checkAmount.text = "0 건"
        binding.recruitingStudyReyclerview.visibility = View.GONE
        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
    }


    private fun showError(error: String?) {
        Toast.makeText(requireContext(), "API 호출 실패: $error", Toast.LENGTH_SHORT).show()
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId, memberId)
                .enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { likeResponse ->
                                // 서버에서 반환된 상태에 따라 하트 아이콘 및 BoardItem의 liked 상태 업데이트
                                val newStatus = likeResponse.result.status
                                studyItem.liked = newStatus == "LIKE"
                                val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                                likeButton.setImageResource(newIcon)

                                // heartCount 즉시 증가 또는 감소
                                studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                                // 변경된 항목을 어댑터에 알림
                                val adapter = binding.recruitingStudyReyclerview.adapter as InterestVPAdapter
                                val position = adapter.dataList.indexOf(studyItem)
                                if (position != -1) {
                                    adapter.notifyItemChanged(position)
                                }
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

    private fun updateLikeButtonUI(likeButton: ImageView, isLiked: Boolean) {
        val newIcon = if (isLiked) R.drawable.ic_heart_filled else R.drawable.study_like
        likeButton.setImageResource(newIcon)
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
    }
}
