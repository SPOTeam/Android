package com.example.spoteam_android.ui.bookMark

import StudyApiService
import StudyViewModel
import android.content.Context.MODE_PRIVATE
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.BookmarkItem
import com.example.spoteam_android.BookmarkResponse
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.databinding.FragmentBookmarkBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookMarkRVAdapter: BookMarkRVAdapter
    private var bookitemList = ArrayList<BookmarkItem>()
    private val studyViewModel: StudyViewModel by activityViewModels()

    // Retrofit API Service
    private lateinit var studyApiService: StudyApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        bookMarkRVAdapter = BookMarkRVAdapter(bookitemList, onItemClick = { selectedItem ->
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

        binding.fragmentBookmarkRv.apply {
            adapter = bookMarkRVAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        fetchBookmarkedStudies()
    }


    private fun fetchBookmarkedStudies() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.getBookmark(memberId).enqueue(object : Callback<BookmarkResponse> {
                override fun onResponse(call: Call<BookmarkResponse>, response: Response<BookmarkResponse>) {
                    if (response.isSuccessful) {
                        val bookmarkResponse = response.body()
                        if (bookmarkResponse != null && bookmarkResponse.result != null) {
                            val content = bookmarkResponse.result.content
                            if (!content.isNullOrEmpty()) {
                                bookitemList.clear()
                                bookitemList.addAll(content.map { studyData ->
                                    BookmarkItem(
                                        studyId = studyData.studyId,
                                        title = studyData.title,
                                        goal = studyData.goal,
                                        introduction = studyData.introduction,
                                        memberCount = studyData.memberCount,
                                        heartCount = studyData.heartCount,
                                        hitNum = studyData.hitNum,
                                        maxPeople = studyData.maxPeople,
                                        studyState = studyData.studyState,
                                        themeTypes = studyData.themeTypes,
                                        regions = studyData.regions,
                                        imageUrl = studyData.imageUrl,
                                        liked = studyData.liked
                                    )
                                })
                                Log.d("Bookmark", "Fetched bookmarked studies: $bookitemList")
                                bookMarkRVAdapter.updateList(bookitemList)
                                binding.bookmarkEmpty.visibility = View.GONE
                                binding.fragmentBookmarkRv.visibility = View.VISIBLE
                            } else {
                                // 데이터가 없을 때 처리
                                binding.bookmarkEmpty.visibility = View.VISIBLE
                                binding.fragmentBookmarkRv.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(requireContext(), "서버 응답이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "북마크 데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun toggleLikeStatus(studyItem: BookmarkItem, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId, memberId).enqueue(object : Callback<LikeResponse> {
                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { likeResponse ->
                            // 서버에서 반환된 상태에 따라 하트 아이콘 및 BookmarkItem의 liked 상태 업데이트
                            val newStatus = likeResponse.result.status
                            Log.d("studyfrag", newStatus)
                            studyItem.liked = newStatus == "LIKE" // "LIKE"이면 true, "DISLIKE"이면 false로 저장
                            val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                            likeButton.setImageResource(newIcon)

                            // heartCount 즉시 증가 또는 감소
                            studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1
                            bookMarkRVAdapter.notifyItemChanged(bookitemList.indexOf(studyItem))
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
}