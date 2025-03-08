package com.example.spoteam_android.ui.study

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
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentWriteContentBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.WriteContentRequest
import com.example.spoteam_android.ui.community.WriteContentResponse
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call

// 인터페이스 정의
interface BottomSheetDismissListener {
    fun onBottomSheetDismissed()
}

class EditContentFragment() : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentWriteContentBinding
    private var selectedCategory: String = ""
    private var isAnonymous: Boolean = false
    private var postId : String = ""

    private var dismissListener: BottomSheetDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteContentBinding.inflate(inflater, container, false)
        // 전달된 데이터 받아와서 UI에 반영
        arguments?.let {
            val title = it.getString("title", "")
            val content = it.getString("content", "")
            val type = it.getString("type","")
            val postId = it.getString("postId","")
            Log.d("EditContentFragment", postId)

            binding.writeContentTitleEt.setText(title)
            binding.writeContentContentEt.setText(content)
            selectedCategory = type

            this.postId = postId
        }

        binding.writeContentTitleTv.text = "글수정"

        binding.categorySpinner.onItemSelectedListener = this

        binding.writeContentFinishBtn.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val currentEmail = sharedPreferences.getString("currentEmail", null)
            val memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1

            submitContent(memberId)
        }

        // Spinner 어댑터 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter

            // Spinner의 초기값 설정
            val position = getCategoryPosition(selectedCategory)
            if (position != -1) {
                binding.categorySpinner.setSelection(position)
            }
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }

        return binding.root
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        // 다이얼로그 종료를 호출한 쪽에 알림
        dismissListener?.onBottomSheetDismissed()
    }

    // 카테고리의 위치를 반환하는 함수
    private fun getCategoryPosition(category: String): Int {
        val categories = resources.getStringArray(R.array.category_list)
        return categories.indexOfFirst { it == getCategoryDisplayName(category) }
    }

    // 서버에서 사용하는 카테고리 값을 사용자에게 보여줄 텍스트로 변환하는 함수
    private fun getCategoryDisplayName(category: String): String {
        return when (category) {
            "PASS_EXPERIENCE" -> "합격후기"
            "INFORMATION_SHARING" -> "정보공유"
            "COUNSELING" -> "고민상담"
            "JOB_TALK" -> "취준토크"
            "FREE_TALK" -> "자유토크"
            else -> "합격후기"
        }
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

        Log.d("EditContentFragment", "${requestBody} , ${memberId} , ${postId}")

        // 서버로 데이터 전송
        sendEditContentToServer(requestBody, memberId)
    }

    private fun sendEditContentToServer(requestBody: WriteContentRequest, memberId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.editContent(postId, requestBody)
            .enqueue(object : Callback<WriteContentResponse> {
                override fun onResponse(call: Call<WriteContentResponse>, response: Response<WriteContentResponse>) {
                    Log.d("WriteContentFragment", response.body()?.isSuccess.toString())
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        val editContentResponseBody = response.body()
                        Log.d("EditContent", editContentResponseBody!!.code)

                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<WriteContentResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
        })
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
