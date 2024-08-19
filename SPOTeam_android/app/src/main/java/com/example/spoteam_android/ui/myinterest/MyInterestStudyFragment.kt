package com.example.spoteam_android.ui.myinterest

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentInterestBinding
import com.example.spoteam_android.databinding.FragmentMyInterestStudyBinding
import com.example.spoteam_android.ui.interestarea.InterestFilterFragment
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyInterestStudyFragment : Fragment() {

    lateinit var binding: FragmentMyInterestStudyBinding
    lateinit var tabLayout: TabLayout
    lateinit var filterIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInterestStudyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = binding.filterToggle
        val filtericon: ImageView = binding.icFilter

        filtericon.setOnClickListener{
            (activity as MainActivity).switchFragment(MyInterestStudyFilterFragment())
        }


        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_study,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        val myinterest_board = binding.myInterestStudyReyclerview

        val itemList = ArrayList<BoardItem>()

        itemList.add(BoardItem(1,"피아노 스터디", "스터디 목표", "피아노 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(2,"태권도 스터디", "스터디 목표", "태권도 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(3,"프랑스어 스터디", "스터디 목표", "프랑스어 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(4,"토익 스터디", "스터디 목표", "토익 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(5,"C언어 스터디", "스터디 목표", "C언어 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(6,"디자인 스터디", "스터디 목표", "디자인 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(7,"기타 스터디", "스터디 목표", "기타 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))

        val boardAdapter = BoardAdapter(itemList){selectedItem ->}
        boardAdapter.notifyDataSetChanged()

        myinterest_board.adapter = boardAdapter
        myinterest_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
}

//                            val pagesResponse = response.body()
//                            Log.d("InterestingFragment", "$response")
//                            if (pagesResponse?.isSuccess == true) {
//                                val pagesResponseList = pagesResponse?.result?.content
//                                Log.d("InterestingFragment", "items: $pagesResponse")
//                                if (pagesResponseList != null) {
//                                    Log.d("InterestingFragment", "items: $pagesResponseList")
//                                }
//                                //}
//                                else {
//                                    Log.d("InterestingFragment", "이게 찍히나?")
//                                }
//                            } else {
//                                Log.d("InterestingFragment", "{$response}")
//                                // 실패한 경우 서버로부터의 응답 코드를 로그로 출력
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Failed to fetch studies: ${response.code()} - ${response.message()}"
//                                )
//
//                                // 응답 본문을 문자열로 변환하여 로그로 출력
//                                val errorBody = response.errorBody()?.string()
//                                if (errorBody != null) {
//                                    Log.e("InterestingFragment", "Error body: $errorBody")
//                                } else {
//                                    Log.e("InterestingFragment", "Error body is null")
//                                }
//
//                                // 추가 디버깅 정보 출력
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Request URL: ${response.raw().request.url}"
//                                )
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Request Method: ${response.raw().request.method}"
//                                )
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Request Headers: ${response.raw().request.headers}"
//                                )
//                            }
//
//                        }
//                    }
//                }
//
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        })


// memberId를 가져오는 메서드
//    fun getMemberId(context: Context): Int {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        return sharedPreferences.getInt("memberId", -1) // 저장된 memberId 없을 시 기본값 -1 반환
//    }



