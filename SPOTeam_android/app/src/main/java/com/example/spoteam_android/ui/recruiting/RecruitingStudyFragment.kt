package com.example.spoteam_android.ui.recruiting

import RetrofitClient.getAuthToken
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
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentRecruitingStudyBinding
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitingStudyFragment : Fragment() {

    lateinit var binding: FragmentRecruitingStudyBinding
    private lateinit var recruitingStudyAdapter: BoardAdapter
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecruitingStudyBinding.inflate(inflater, container, false)

        recruitingStudyAdapter = BoardAdapter(
            ArrayList(),
            onItemClick = { selectedItem ->
                studyViewModel.setStudyData(
                    selectedItem.studyId,
                    selectedItem.imageUrl,
                    selectedItem.introduction
                )

                // Fragment 전환
                val fragment = DetailStudyFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onLikeClick = { selectedItem, likeButton ->
            }
        )
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
//        val activityFeeinput2 = activityFee02 =="있음" // activityFeeinput이 있으면 true, 없으면 false
//        val activityFeeAmount: String? = if (activityFeeinput2) activityFeeinput else null

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


        when (mysource) {
            "HouseFragment" -> {
                fetchAllRecruiting("LIKED")
                spinner.setSelection(4)
            }
        }




        when (source) {
            "RecruitingStudyFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE

                if (gender != null && minAge!= null && maxAge!= null && activityFee02!= null && selectedStudyTheme!= null && activityFee01!= null) {
                    if (activityFeeinput != null) {
                        fetchSpecificRecruiting(
                            "ALL",
                            gender,
                            minAge,
                            maxAge,
                            activityFee02,
                            activityFeeinput,
                            selectedStudyTheme,
                            activityFee01
                        )
                    }
                    else{
                        fetchSpecificRecruiting(
                            "ALL",
                            gender,
                            minAge,
                            maxAge,
                            activityFee02,
                            "",
                            selectedStudyTheme,
                            activityFee01
                        )
                    }
                }
            }

            else -> {
                fetchAllRecruiting("ALL")
                binding.icFilterActive.visibility = View.GONE
            }

        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
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
                        when (position) {
                            0 ->
                                if (gender != null && minAge!= null && maxAge!= null && activityFee02!= null && selectedStudyTheme!= null && activityFee01!= null) {
                                    if (activityFeeinput != null) {
                                        fetchSpecificRecruiting(
                                            "ALL",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                    else{
                                        fetchSpecificRecruiting(
                                            "ALL",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                }
                            1 ->
                                if (gender != null && minAge!= null && maxAge!= null && activityFee02!= null && selectedStudyTheme!= null && activityFee01!= null) {
                                    if (activityFeeinput != null) {
                                        fetchSpecificRecruiting(
                                            "RECRUITING",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                    else{
                                        fetchSpecificRecruiting(
                                            "RECRUITING",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                }
                            2 ->
                                if (gender != null && minAge!= null && maxAge!= null && activityFee02!= null && selectedStudyTheme!= null && activityFee01!= null) {
                                    if (activityFeeinput != null) {
                                        fetchSpecificRecruiting(
                                            "COMPLETED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                    else{
                                        fetchSpecificRecruiting(
                                            "COMPLETED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                }

                            3 ->
                                if (gender != null && minAge!= null && maxAge!= null && activityFee02!= null && selectedStudyTheme!= null && activityFee01!= null) {
                                    if (activityFeeinput != null) {
                                        fetchSpecificRecruiting(
                                            "HIT",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                    else{
                                        fetchSpecificRecruiting(
                                            "HIT",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                }
                            4 ->
                                if (gender != null && minAge!= null && maxAge!= null && activityFee02!= null && selectedStudyTheme!= null && activityFee01!= null) {
                                    if (activityFeeinput != null) {
                                        fetchSpecificRecruiting(
                                            "LIKED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                    else{
                                        fetchSpecificRecruiting(
                                            "LIKED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            selectedStudyTheme,
                                            activityFee01
                                        )
                                    }
                                }
                        }
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }



        val filter: ImageView = binding.icFilter
        filter.setOnClickListener{
            bundle.putString("source", "RecruitingStudyFragment")
            val recruitingStudyFilterFragment = RecruitingStudyFilterFragment()
            recruitingStudyFilterFragment.arguments = bundle // Bundle 전달

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, recruitingStudyFilterFragment)
                .addToBackStack(null)
                .commit()
        }
    }



    private fun fetchAllRecruiting(SelectedItem:String) {

        val recruitingboard = binding.recruitingStudyReyclerview
        val checkcount: TextView = binding.checkAmount
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
            sortBy = SelectedItem,
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {

                    if (response.isSuccessful) {
                        boardItems.clear()
                        val apiResponse = response.body()
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
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
                                val boardAdapter = BoardAdapter(boardItems, onItemClick = { /* No-op */ }, onLikeClick = { _, _ -> /* No-op */ })

                                boardAdapter.notifyDataSetChanged()
                                recruitingboard.visibility = View.VISIBLE
                                recruitingboard.adapter = boardAdapter
                                recruitingboard.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)
                            }
                        }
                        else{
                            checkcount.text = "00 건"
                            recruitingboard.visibility = View.GONE
                        }
                    }
                    else{
                        val errorBody = response.errorBody()?.string()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                }
            })
    }

    private fun fetchSpecificRecruiting(selectedItem: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, selectedStudyTheme:String,isOnline: String) {

        val recruitingboard = binding.recruitingStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        val activityFeeAmount: String ? = if (activityFeeAmount=="") null else activityFeeAmount


        RetrofitClient.RSService.GetRecruitingStudy(
            authToken = getAuthToken(),
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = isOnline.toBoolean(),
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount?.toInt(),
            page = 0,
            size = 5,
            sortBy = selectedItem,
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {

                        boardItems.clear()
                        val apiResponse = response.body()

                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
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
                                val boardAdapter = BoardAdapter(boardItems, onItemClick = { /* No-op */ }, onLikeClick = { _, _ -> /* No-op */ })

                                boardAdapter.notifyDataSetChanged()
                                recruitingboard.visibility = View.VISIBLE
                                recruitingboard.adapter = boardAdapter
                                recruitingboard.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)
                            }
                        }
                        else{
                            checkcount.text = "00 건"
                            recruitingboard.visibility = View.GONE
                            Toast.makeText(requireContext() ,"조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        val errorBody = response.errorBody()?.string()

                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {

                }
            })

    }

    fun getMemberId(context: Context): Int {

        var memberId: Int = -1

        val sharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt(
            "${currentEmail}_memberId",
            -1
        ) else -1


        return memberId // 저장된 memberId 없을 시 기본값 -1 반환
    }
}

