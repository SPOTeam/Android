package com.example.spoteam_android.ui.study

import StudyApiService
import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MemberResponse
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentDetailStudyHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStudyHomeFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyHomeBinding
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailStudyHomeBinding.inflate(inflater, container, false)

        setupViews()

        // ViewModel에서 studyId를 관찰하고 변경될 때마다 fetchStudyMembers 호출
        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            Log.d("DetailStudyHomeFragment", "Received studyId from ViewModel: $studyId")
            if (studyId != null) {
                fetchStudyMembers(studyId)
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun setupViews() {
        profileAdapter = DetailStudyHomeProfileAdapter(ArrayList())
        binding.fragmentDetailStudyHomeProfileRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentDetailStudyHomeProfileRv.adapter = profileAdapter

        studyViewModel.studyIntroduction.observe(viewLifecycleOwner) { introduction ->
            binding.fragmentDetailStudyHomeIntroduceTv.text = introduction
        }
    }

    private fun fetchStudyMembers(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { memberResponse ->
                        profileAdapter.updateList(memberResponse.result.members.map {
                            ProfileItem(profileImage = it.profileImage, nickname = it.nickname)
                        })
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch study members", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
