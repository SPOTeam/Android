package com.example.spoteam_android.ui.community

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentWriteContentBinding
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call

class WriteContentFragment() : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentWriteContentBinding
    private var selectedCategory: String = ""
    private var isAnonymous: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteContentBinding.inflate(inflater, container, false)

        // SharedPreferences 사용
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        val memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
//        Log.d("SharedPreferences", "MemberId: $memberId")

        binding.categorySpinner.onItemSelectedListener = this

        binding.writeContentFinishBtn.setOnClickListener{
            submitContent(memberId)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }

        return binding.root
    }

    private fun submitContent(memberId : Int) {
        val title = binding.writeContentTitleEt.text.toString().trim()
        val content = binding.writeContentContentEt.text.toString().trim()
        isAnonymous = binding.writeContentInfoLl.findViewById<CheckBox>(R.id.anonymous_cb).isChecked

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = WriteContentRequest(
            title = title,
            content = content,
            type = selectedCategory,
            anonymous = isAnonymous
        )

        Log.d("WriteContentFragment", "${requestBody} , ${memberId}")

        // 서버로 데이터 전송
        sendContentToServer(requestBody, memberId)
        resetWriting()
    }

    private fun resetWriting() {
        binding.writeContentTitleEt.text = null
        binding.writeContentContentEt.text = null
    }

    private fun sendContentToServer(requestBody: WriteContentRequest, memberId : Int) {
        CommunityRetrofitClient.instance.postContent(memberId, requestBody)
            .enqueue(object : Callback<WriteContentResponse> {
                override fun onResponse(call: Call<WriteContentResponse>, response: Response<WriteContentResponse>) {
                    Log.d("WriteContentFragment", response.body()?.isSuccess.toString())
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        val writeContentResponseBody = response.body()!!.result
//                        showLog(writeContentResponseBody.toString())
                        setFragmentResult("requestKey", bundleOf("resultKey" to "SUCCESS"))
                        dismiss()
                        val intent = Intent(requireContext(), CommunityContentActivity::class.java)
                        intent.putExtra("postInfo", writeContentResponseBody.id)
                        startActivity(intent)

                    } else {
                        Toast.makeText(requireContext(), "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<WriteContentResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
        })
    }

    private fun showLog(message: String?) {
        Toast.makeText(requireContext(), "WriteContentFragment: $message", Toast.LENGTH_SHORT).show()
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Spinner에서 선택된 항목의 텍스트를 가져옴
        val selectedItem = parent?.getItemAtPosition(position).toString()

        // 선택된 항목에 따라 카테고리 설정
        selectedCategory = when (selectedItem) {
            "합격후기" -> "PASS_EXPERIENCE"
            "정보공유" -> "INFORMATION_SHARING"
            "고민상담" -> "COUNSELING"
            "취준토크" -> "JOB_TALK"
            "자유토크" -> "FREE_TALK" // 기본값
            else -> "PASS_EXPERIENCE"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedCategory = "PASS_EXPERIENCE"
    }
}
