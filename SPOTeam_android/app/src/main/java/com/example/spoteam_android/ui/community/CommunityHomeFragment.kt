package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCommunityHomeBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommunityHomeFragment : Fragment() {

    private lateinit var binding: FragmentCommunityHomeBinding
    private val viewModel: CommunityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityHomeBinding.inflate(inflater, container, false)

        changeTag()
        fetchBestCommunityContent()
        fetchAnnouncementContent()
        fetchRepresentativeContent()

        binding.communityMoveCommunityIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.communityMoveNotificationIv.setOnClickListener {
            val fragment = CommunityFragment()

            val bundle = Bundle()
            bundle.putBoolean("showSpotNotice", true) // 조건이 충족되면 true로 설정

            fragment.arguments = bundle

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icAlarm.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        binding.icFind.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        binding.communityHomeRealTimeTv.setOnClickListener {
            viewModel.currentSortType = "REAL_TIME"
            fetchBestCommunityContent()
        }

        binding.communityHomeRecommendTv.setOnClickListener {
            viewModel.currentSortType = "RECOMMEND"
            fetchBestCommunityContent()
        }

        binding.communityHomeCommentTv.setOnClickListener {
            viewModel.currentSortType = "COMMENT"
            fetchBestCommunityContent()
        }

        binding.icSpotLogo.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HouseFragment())
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (context as MainActivity).isOnCommunityHome(CommunityHomeFragment())
        changeTag()
        fetchBestCommunityContent()
        fetchAnnouncementContent()
        fetchRepresentativeContent()
    }

    private fun changeTag() {
        if(viewModel.currentSortType == "REAL_TIME") {
            binding.communityHomeRealTimeTv.isChecked = true
        } else if (viewModel.currentSortType == "RECOMMEND") {
            binding.communityHomeRecommendTv.isChecked = true
        } else if (viewModel.currentSortType == "COMMENT") {
            binding.communityHomeCommentTv.isChecked = true
        }
    }

    private fun fetchBestCommunityContent() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getBestCommunityContent(viewModel.currentSortType)
            .enqueue(object : Callback<CommunityResponse> {
                override fun onResponse(
                    call: Call<CommunityResponse>,
                    response: Response<CommunityResponse>
                ) {
                    if (response.isSuccessful) {
                        val communityResponse = response.body()
                        if (communityResponse?.isSuccess == "true") {
                            val contentList = communityResponse.result.postBest5Responses
                            if (!contentList.isNullOrEmpty()) {
                                initBestRecyclerview(contentList)
                            }

                        } else {
                            showError(communityResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                    Log.e("CommunityHomeFragment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun fetchAnnouncementContent() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getAnnouncementContent()
            .enqueue(object : Callback<AnnouncementResponse> {
                override fun onResponse(
                    call: Call<AnnouncementResponse>,
                    response: Response<AnnouncementResponse>
                ) {
                    if (response.isSuccessful) {
                        val announcementResponse = response.body()
                        if (announcementResponse?.isSuccess == "true") {
                            val announcementList = announcementResponse.result?.responses
//                            Log.d("Announcement", "items: $announcementList")
                            if (!announcementList.isNullOrEmpty()) {
                                binding.communityHomeNotificationContentRv.visibility = View.VISIBLE
                                binding.communityMoveNotificationIv.visibility = View.VISIBLE
                                binding.communityHomeNotificationTv.visibility = View.VISIBLE
                                initAnnouncementRecyclerview(announcementList)
                            } else {
                                binding.communityHomeNotificationContentRv.visibility = View.GONE
                                binding.communityMoveNotificationIv.visibility = View.GONE
                                binding.communityHomeNotificationTv.visibility = View.GONE
                            }

                        } else {
                            showError(announcementResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<AnnouncementResponse>, t: Throwable) {
                    Log.e("CommunityHomeFragment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun fetchRepresentativeContent() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getRepresentativeContent()
            .enqueue(object : Callback<RepresentativeResponse> {
                override fun onResponse(
                    call: Call<RepresentativeResponse>,
                    response: Response<RepresentativeResponse>
                ) {
                    if (response.isSuccessful) {
                        val representativeResponse = response.body()
                        if (representativeResponse?.isSuccess == "true") {
                            val representativeList = representativeResponse.result?.responses
//                            Log.d("Representative", "items: $representativeList")
                            if (!representativeList.isNullOrEmpty()) {
                                initRepresentativeRecyclerview(representativeList)
                            }
                        } else {
                            showError(representativeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<RepresentativeResponse>, t: Throwable) {
                    Log.e("CommunityHomeFragment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
//        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initBestRecyclerview(contentList: List<ContentDetailInfo>) {
        binding.communityHomeBestPopularityContentRv.layoutManager =
            object : LinearLayoutManager(context, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        val dataRVAdapter = CommunityHomeRVAdapterWithIndex(contentList)
        binding.communityHomeBestPopularityContentRv.adapter = dataRVAdapter
    }

    private fun initRepresentativeRecyclerview(representativeList: List<RepresentativeDetailInfo>) {
        binding.communityHomeCommunityContentRv.layoutManager =
            object : LinearLayoutManager(context, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        val dataRVAdapter = CommunityHomeRVAdapterWithCategory(representativeList)
        binding.communityHomeCommunityContentRv.adapter = dataRVAdapter
    }

    private fun initAnnouncementRecyclerview(announcementList: List<AnnouncementDetailInfo>) {
        binding.communityHomeNotificationContentRv.layoutManager =
            object : LinearLayoutManager(context, VERTICAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

        val dataRVAdapter = CommunityHomeRVAdapterAnnouncement(announcementList)
        binding.communityHomeNotificationContentRv.adapter = dataRVAdapter
    }
}
