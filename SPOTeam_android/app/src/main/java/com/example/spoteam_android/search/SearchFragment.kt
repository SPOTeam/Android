package com.example.spoteam_android.search

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentSearchBinding
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestVPAdapter
import com.example.spoteam_android.ui.interestarea.RecommendStudyApiService
import com.example.spoteam_android.ui.interestarea.RecruitingStudyApiService
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.DetailStudyVPAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val recentSearches = mutableListOf<String>()
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var recommendBoardAdapter: InterestVPAdapter
    private lateinit var studyApiService: StudyApiService
    private lateinit var recruitingStudyAdapter: InterestVPAdapter
    private lateinit var db: AppDatabase
    private lateinit var searchQueryDao: SearchQueryDao



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)


//        val db = Room.databaseBuilder(
//            requireContext(),
//            AppDatabase::class.java, "database-name"
//        ).build()
//
//        val userDao = db.userDao()

//        // 비동기로 데이터베이스 작업을 수행
//        CoroutineScope(Dispatchers.IO).launch {
//            // 더미 데이터 삽입
//            val user1 = User(firstName = "John", lastName = "Doe",uid = 3)
//            val user2 = User(firstName = "Jane", lastName = "Smith",uid = 4)
//            userDao.insertAll(user1, user2)
//
//            // 모든 유저 조회
//            val users = userDao.getAll()
//            users.forEach { user ->
//                Log.d("MainActivity", "User: ${user.firstName} ${user.lastName}, ID: ${user.uid}")
//            }
//
//            // 특정 이름으로 유저 검색
//            val foundUser = userDao.findByName("John", "Doe")
//            Log.d("MainActivity", "Found User: ${foundUser.firstName} ${foundUser.lastName}")
//        }

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "search_database"
        ).build()

        searchQueryDao = db.searchQueryDao()

            // 최근 검색어 로드
        loadRecentSearches()

        val chipGroup = binding.chipGroup2

// ChipGroup의 모든 자식 Chip의 클릭 이벤트를 비활성화
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isClickable = false
            chip.isFocusable = false
            chip.isCheckable = false
        }

        recommendBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        recommendBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                val detailStudyFragment = DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })

        binding.recommendationBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendBoardAdapter
        }

        recruitingStudyAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        recruitingStudyAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                val detailStudyFragment = DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })

        binding.searchBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recruitingStudyAdapter
        }



        val memberId = getMemberId(requireContext())
        fetchRecommendStudy(memberId)
        fetchAllRecruiting("ALL")

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // 검색어를 SharedPreferences에 저장
                    saveSearchQuery(it)

                    // Chip 추가
                    addSearchChip(it)

                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()

                    // 키워드를 번들로 전달
                    val bundle = Bundle().apply {
                        putString("search_keyword", it)
                    }

                    // SearchResultFragment로 이동
                    val fragment = SearchResultFragment().apply {
                        arguments = bundle
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return binding.root
    }

    private fun addSearchChip(query: String) {

        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = false
            isClickable = false
            isFocusable = false
            isChecked = true
            setEnsureMinTouchTargetSize(false)

            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)  // 텍스트 크기 (14sp)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_chip_text))
        }


        chip.setChipDrawable(
            ChipDrawable.createFromAttributes(
                requireContext(), null, 0, R.style.find_ChipStyle
            )
        )

        chip.isCloseIconVisible = false
        chip.isCheckable = false
        binding.chipGroup.addView(chip, 0)
    }

    private fun saveSearchQuery(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val searchQuery = SearchQuery(query = query)
            searchQueryDao.insertSearchQuery(searchQuery)
        }
    }

    private fun loadRecentSearches() {
        CoroutineScope(Dispatchers.IO).launch {
            val recentSearches = searchQueryDao.getAllSearchQueries()
            recentSearches.forEach { searchQuery ->
                Log.d("SearchFragment", "검색어: ${searchQuery.query}, 시간: ${searchQuery.timestamp}")
            }
        }
    }

    private fun fetchRecommendStudy(memberId: Int) {
        val service = RetrofitInstance.retrofit.create(RecommendStudyApiService::class.java)
        service.GetRecommendStudy(memberId = memberId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val boardItems = response.body()?.result?.content?.map { study ->
                        BoardItem(
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
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        recommendBoardAdapter.updateList(boardItems)
                        binding.recommendationBoard.visibility = View.VISIBLE
                    } else {
                        binding.recommendationBoard.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) {
            sharedPreferences.getInt("${currentEmail}_memberId", -1)
        } else {
            -1
        }
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
                                val adapter = binding.recommendationBoard.adapter as InterestVPAdapter
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
            page = 0,
            size = 1,
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
                    } else {
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }
    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        recruitingStudyAdapter.updateList(boardItems)
    }

}