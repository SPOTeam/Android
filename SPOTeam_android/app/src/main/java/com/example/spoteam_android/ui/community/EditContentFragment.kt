package com.example.spoteam_android.ui.community

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.ReportCompleteListener
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyWriteContentBinding
import com.example.spoteam_android.ui.study.WriteContentImageRVadapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// 인터페이스 정의
interface BottomSheetDismissListener {
    fun onBottomSheetDismissed()
}

class EditContentFragment(
    private val listener: BottomSheetDismissListener // ✅ 콜백 추가
) : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentMystudyWriteContentBinding
    private var selectedCategory: String = ""
    private var isAnonymous: Boolean = false
    private var postId : Int = -1

    private val imageList = mutableListOf<Any>()
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageAdapter: WriteContentImageRVadapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyWriteContentBinding.inflate(inflater, container, false)
        // 전달된 데이터 받아와서 UI에 반영
        arguments?.let {
            val postId = it.getInt("postId")
//            Log.d("EditContentFragment", postId)

            this.postId = postId
        }
        getPostContent()
        initTextWatchers()


        binding.writeContentTitleTv.text = "글수정"

        binding.mystudyCategorySpinner.onItemSelectedListener = this

        binding.addImageIv.setOnClickListener{
            getImageFromAlbum()
        }

        binding.writeContentFinishBtn.setOnClickListener {
            submitContent()
        }

        // Spinner 어댑터 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.mystudyCategorySpinner.adapter = adapter

            // Spinner의 초기값 설정
            val position = getCategoryPosition(selectedCategory)
            if (position != -1) {
                binding.mystudyCategorySpinner.setSelection(position)
            }
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }

        getImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                // 🔁 이전 이미지 모두 제거
                imageList.clear()
                if (data?.data != null) {
                    // ✅ 한 장 선택된 경우
                    val uri = data.data!!
                    imageList.add(uri)
                }

                imageAdapter.notifyDataSetChanged() // RecyclerView 갱신
            }
        }

        return binding.root
    }

    private fun initTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkFieldsForEmptyValues()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.writeContentTitleEt.addTextChangedListener(textWatcher)
        binding.writeContentContentEt.addTextChangedListener(textWatcher)
    }

    private fun checkFieldsForEmptyValues() {
        val title = binding.writeContentTitleEt.text.toString().trim()
        val content = binding.writeContentContentEt.text.toString().trim()

        binding.writeContentFinishBtn.isEnabled = title.isNotEmpty() && content.isNotEmpty()

    }

    private fun getPostContent() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getContentInfo(postId, false)
            .enqueue(object : Callback<ContentResponse> {
                override fun onResponse(
                    call: Call<ContentResponse>,
                    response: Response<ContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val contentResponse = response.body()
                        if (contentResponse?.isSuccess == "true") {
                            val contentInfo = contentResponse.result
                            isAnonymous = contentInfo.anonymous
                            initContentInfo(contentInfo)
                        } else {
                            showError(contentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ContentResponse>, t: Throwable) {
                    Log.e("CommunityContentActivity", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun changeContent() {
        binding.isAnnountTv.text = "익명"

        binding.checkIc.setOnClickListener{
            if(isAnonymous) {
                binding.checkIc.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.gray),
                    PorterDuff.Mode.SRC_IN
                )
                isAnonymous = false

            } else {
                binding.checkIc.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.selector_blue),
                    PorterDuff.Mode.SRC_IN
                )
                isAnonymous = true
            }
        }
    }
    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 🔥 여러 개 선택 가능하도록 설정
        }
        getImageLauncher.launch(intent)
    }

    private fun initContentInfo(contentInfo: ContentInfo) {
        binding.writeContentTitleEt.setText(contentInfo.title)
        binding.writeContentContentEt.setText(contentInfo.content)

        // 서버에서 가져온 이미지 리스트를 Uri 리스트로 변환
        val serverImageUrls = contentInfo.imageUrl

        imageAdapter = WriteContentImageRVadapter(imageList)

        // 서버 이미지 리스트와 기존의 갤러리에서 선택한 이미지 리스트 통합
        imageList.clear()
        imageList.addAll(listOf(serverImageUrls))
        binding.addedImagesRv.apply {
            adapter = imageAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        imageAdapter.notifyDataSetChanged() // RecyclerView 갱신

        if (isAnonymous) {
            binding.checkIc.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.selector_blue),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            binding.checkIc.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.gray),
                PorterDuff.Mode.SRC_IN
            )
        }

        changeContent()

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.thema_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.mystudyCategorySpinner.adapter = adapter

            // Spinner의 초기값 설정
            val position = getCategoryPosition(contentInfo.type)
            if (position != -1) {
                binding.mystudyCategorySpinner.setSelection(position)
            }
        }
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

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val fileName = "image_${UUID.randomUUID()}.png" // 🔥 파일명을 고유하게 생성
        val file = File(requireContext().cacheDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun submitContent() {
        val title = binding.writeContentTitleEt.text.toString().trim()
        val content = binding.writeContentContentEt.text.toString().trim()

        val imagePart: MultipartBody.Part? =
            if (imageList.isNotEmpty() && imageList[0] is Uri) {
                val file = getFileFromUri(imageList[0] as Uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            } else null

        // 나머지 데이터를 RequestBody로 변환
        val isAnnoymous = isAnonymous.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val themePart = selectedCategory.toRequestBody("text/plain".toMediaTypeOrNull())
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

        // 서버로 데이터 전송
        sendContentToServer(isAnnoymous, themePart, titlePart, contentPart, imagePart)
    }

    private fun sendContentToServer(
        isAnonymous: RequestBody,
        themePart: RequestBody,
        titlePart: RequestBody,
        contentPart: RequestBody,
        imageParts: MultipartBody.Part?
    ) {
        val requestBody = WriteContentRequest(
            title = titlePart.toString(),
            content = contentPart.toString(),
            type = "ALL",
            anonymous = false // 임시
        )

        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
//        service.editContent(titlePart, contentPart, themePart, isAnonymous, imageParts!!)
            service.editContent(postId, requestBody)
            .enqueue(object : Callback<WriteContentResponse> {
                override fun onResponse(call: Call<WriteContentResponse>, response: Response<WriteContentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        val writeContentResponseBody = response.body()!!.result
                        setFragmentResult("requestKey", bundleOf("resultKey" to "SUCCESS"))
                        dismiss()
                        listener.onBottomSheetDismissed()
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
