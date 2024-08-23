package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCommunityHomeBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityHomeFragment : Fragment() {

    private lateinit var binding: FragmentCommunityHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityHomeBinding.inflate(inflater, container, false)

        fetchBestCommunityContent(sortType = "")
        fetchAnnouncementContent()
        fetchRepresentativeContent()

        binding.communityMoveCommunityIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityFragment())
                .commitAllowingStateLoss()
        }

        binding.communityMoveNotificationIv.setOnClickListener {
            val fragment = CommunityFragment()

            val bundle = Bundle()
            bundle.putBoolean("showSpotNotice", true) // 조건이 충족되면 true로 설정

            fragment.arguments = bundle

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .commitAllowingStateLoss()
        }

        binding.communityHomeAlertIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        binding.communityHomeSearchIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        binding.communityHomeRealTimeTv.setOnClickListener {
            fetchBestCommunityContent(sortType = "REAL_TIME")
        }

        binding.communityHomeRecommendTv.setOnClickListener {
            fetchBestCommunityContent(sortType = "RECOMMEND")
        }

        binding.communityHomeCommentTv.setOnClickListener {
            fetchBestCommunityContent(sortType = "COMMENT")
        }

        binding.communityHomeSpotNameLogoIv.setOnClickListener{
            (context as MainActivity).isOnCommunityHome(HomeFragment())
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (context as MainActivity).isOnCommunityHome(CommunityHomeFragment())
    }

    private fun fetchBestCommunityContent(sortType: String) {
        CommunityRetrofitClient.instance.getBestCommunityContent(sortType)
            .enqueue(object : Callback<CommunityResponse> {
                override fun onResponse(
                    call: Call<CommunityResponse>,
                    response: Response<CommunityResponse>
                ) {
                    if (response.isSuccessful) {
                        val communityResponse = response.body()
                        if (communityResponse?.isSuccess == "true") {
                            val contentList = communityResponse.result?.postBest5Responses
//                            Log.d("BestCommunity", "items: $contentList")
                            if (contentList != null) {
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
        CommunityRetrofitClient.instance.getAnnouncementContent()
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
                            if (announcementList != null) {
                                initAnnouncementRecyclerview(announcementList)
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
        CommunityRetrofitClient.instance.getRepresentativeContent()
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
                            if (representativeList != null) {
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
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initBestRecyclerview(contentList: List<ContentDetailInfo>) {
        binding.communityHomeBestPopularityContentRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = CommunityHomeRVAdapterWithIndex(contentList)
        binding.communityHomeBestPopularityContentRv.adapter = dataRVAdapter
    }

    private fun initRepresentativeRecyclerview(representativeList: List<RepresentativeDetailInfo>) {
        binding.communityHomeCommunityContentRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = CommunityHomeRVAdapterWithCategory(representativeList)
        binding.communityHomeCommunityContentRv.adapter = dataRVAdapter
    }

    private fun initAnnouncementRecyclerview(announcementList: List<AnnouncementDetailInfo>) {
        binding.communityHomeNotificationContentRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = CommunityHomeRVAdapterAnnouncement(announcementList)
        binding.communityHomeNotificationContentRv.adapter = dataRVAdapter
    }
}
