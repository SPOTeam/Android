package com.example.spoteam_android.ui.recruiting

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.spoteam_android.ui.interestarea.RecruitingStudyApiService
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitingStudyFragment : Fragment() {

    lateinit var binding: FragmentRecruitingStudyBinding
    private lateinit var recruitingStudyAdapter: InterestVPAdapter
    private lateinit var studyApiService: StudyApiService
    private val studyViewModel: StudyViewModel by activityViewModels()
    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var gender: String? = null
    private var minAge: String? = null
    private var maxAge: String? = null
    private var isOnline: String? = null
    private var isFee: String? = null
    private var selectedStudyTheme: String? = null
    private var activityFeeAmount: String? = null
    private var source: String? = null
    private var selectedItem: String = "ALL"

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
            },
            studyViewModel = studyViewModel
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

        gender = arguments?.getString("gender2")
        minAge = arguments?.getString("minAge2")
        maxAge = arguments?.getString("maxAge2")
        isOnline = arguments?.getString("activityFee_01") //  온 오프라인
        isFee = arguments?.getString("activityFee_02") // 활동비 유무 변수
        activityFeeAmount = arguments?.getString("activityFeeAmount2")// 구체적인 활동비
        selectedStudyTheme = arguments?.getString("selectedStudyTheme2")
        source = arguments?.getString("source")

        setupPageNavigationButtons()

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

        when (source) {
            "RecruitingStudyFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE
                fetchSpecificRecruiting(
                    selectedItem,
                    gender = gender,
                    minAge = minAge,
                    maxAge = maxAge,
                    activityFee = isFee,
                    activityFeeAmount = activityFeeAmount,
                    selectedStudyTheme = selectedStudyTheme,
                    isOnline = isOnline,
                    currentPage = currentPage
                    )
            }

            else -> {
                fetchAllRecruiting(selectedItem)
                binding.icFilterActive.visibility = View.GONE
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (source) {
                    "HouseFragment" -> {
                        selectedItem = when (position) {
                            1 -> "RECRUITING"
                            2 -> "COMPLETED"
                            3 -> "HIT"
                            4 -> "LIKED"
                            else -> "ALL"
                        }
                        fetchAllRecruiting(selectedItem)
                    }
                    "RecruitingStudyFilterFragment" -> {
                        selectedItem = when (position) {
                            1 -> "RECRUITING"
                            2 -> "COMPLETED"
                            3 -> "HIT"
                            4 -> "LIKED"
                            else -> "ALL"
                        }
                        fetchSpecificRecruiting(
                            selectedItem,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            activityFee = isFee,
                            activityFeeAmount = activityFeeAmount,
                            selectedStudyTheme = selectedStudyTheme,
                            isOnline = isOnline,
                            currentPage = currentPage
                        )
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

    private fun setupPageNavigationButtons() {
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                requestPageUpdate()
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                requestPageUpdate()
            }
        }
    }

    private fun requestPageUpdate() {
        when (source) {
            "RecruitingStudyFilterFragment" -> {
                fetchSpecificRecruiting(
                    selectedItem,
                    gender = gender,
                    minAge = minAge,
                    maxAge = maxAge,
                    activityFee = isFee,
                    activityFeeAmount = activityFeeAmount,
                    selectedStudyTheme = selectedStudyTheme,
                    isOnline = isOnline,
                    currentPage = currentPage  // currentPage 유지
                )
            }
            else -> {
                fetchAllRecruiting(selectedItem)
            }
        }
    }

    private fun updatePageNumberUI() {
        binding.currentPage.text = (currentPage + 1).toString()

        binding.previousPage.isEnabled = currentPage > 0
        binding.previousPage.setTextColor(resources.getColor(
            if (currentPage > 0) R.color.active_color else R.color.disabled_color,
            null
        ))

        binding.nextPage.isEnabled = currentPage < totalPages - 1
        binding.nextPage.setTextColor(resources.getColor(
            if (currentPage < totalPages - 1) R.color.active_color else R.color.disabled_color,
            null
        ))
    }

    private fun fetchAllRecruiting(selectedItem: String) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(RecruitingStudyApiService::class.java)
        service.GetRecruitingStudy(
            gender = "MALE",
            minAge = 18,
            maxAge = 60,
            isOnline = false,
            hasFee = false,
            fee = null,
            page = currentPage,
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
                                liked = study.liked,
                                isHost = false
                            )
                            boardItems.add(boardItem)
                        }
                        totalPages = apiResponse.result.totalPages
                        Log.d("RecruitingStudyFragment", "$totalPages")
                        updatePageNumberUI()

                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                        binding.recruitingStudyReyclerview.visibility = View.VISIBLE

                        updateRecyclerView(boardItems)

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
        gender: String? = null,
        minAge: String? = null,
        maxAge: String? = null,
        activityFee: String? = null,
        activityFeeAmount: String? = null,
        selectedStudyTheme: String? = null,
        isOnline: String? = null,
        currentPage: Int?= null,
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(RecruitingStudyApiService::class.java)
        service.GetRecruitingStudy(
            gender = gender,
            minAge = minAge?.toInt(),
            maxAge = maxAge?.toInt(),
            isOnline = isOnline?.toBoolean(),
            hasFee = activityFee?.toBoolean(),
            fee = activityFeeAmount?.toIntOrNull(),
            page = currentPage ?: 0,
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
                                liked = study.liked,
                                isHost = false
                            )
                            boardItems.add(boardItem)
                        }
                        totalPages = apiResponse.result.totalPages
                        updatePageNumberUI()

                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                        binding.recruitingStudyReyclerview.visibility = View.VISIBLE

                        updateRecyclerView(boardItems)
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
            studyApiService.toggleStudyLike(studyItem.studyId)
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
