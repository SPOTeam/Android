package com.example.spoteam_android.presentation.community

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyWriteContentBinding
import com.example.spoteam_android.presentation.study.WriteContentImageRVadapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
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

class EditContentFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

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

        isCancelable = false // 외부 클릭으로 닫히지 않도록 설정

        // 전달된 데이터 받아와서 UI에 반영
        arguments?.let {
            val postId = it.getInt("postId")
//            Log.d("EditContentFragment", postId)

            this.postId = postId
        }



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
            R.layout.write_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.write_spinner_dropdown_item)
            binding.mystudyCategorySpinner.adapter = adapter
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

        getPostContent()
        initTextWatchers()

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
                        if (contentResponse?.isSuccess == true) {
                            val contentInfo = contentResponse.result
                            isAnonymous = contentInfo.anonymous
                            selectedCategory = contentInfo.type
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
                    ContextCompat.getColor(requireContext(), R.color.b500),
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

        if(contentInfo.imageUrl != null ) imageList.add(serverImageUrls)

        binding.addedImagesRv.apply {
            adapter = imageAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        imageAdapter.notifyDataSetChanged() // RecyclerView 갱신

        if (isAnonymous) {
            binding.checkIc.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.b500),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            binding.checkIc.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.gray),
                PorterDuff.Mode.SRC_IN
            )
        }

        changeContent()

        val displayName = getCategoryDisplayName(selectedCategory)
        val position = resources.getStringArray(R.array.category_list).indexOf(displayName)
        if (position != -1) {
            binding.mystudyCategorySpinner.setSelection(position)
        }
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

        lifecycleScope.launch {
            var imagePart: MultipartBody.Part? = null
            var existingImage: RequestBody? = null

            if (imageList.isNotEmpty()) {
                val item = imageList[0]

                when (item) {
                    is Uri -> {
                        val file = getFileFromUri(item)
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                        existingImage = "".toRequestBody("text/plain".toMediaTypeOrNull()) // 새 이미지 업로드니까 기존 이미지 제거
                    }
                    is String -> {
                        // 서버 이미지 URL (기존 이미지 유지)
                        imagePart = null
                        existingImage = item.toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                }
            }

            val isAnonymousPart = isAnonymous.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val themePart = selectedCategory.toRequestBody("text/plain".toMediaTypeOrNull())
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

            sendContentToServer(isAnonymousPart, themePart, titlePart, contentPart, imagePart, existingImage)
        }
    }

    private fun sendContentToServer(
        isAnonymous: RequestBody,
        themePart: RequestBody,
        titlePart: RequestBody,
        contentPart: RequestBody,
        imagePart: MultipartBody.Part?,
        existingImage : RequestBody?
    ) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.editContent(postId, titlePart, contentPart, themePart, imagePart, existingImage, isAnonymous)
            .enqueue(object : Callback<WriteContentResponse> {
                override fun onResponse(call: Call<WriteContentResponse>, response: Response<WriteContentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val writeContentResponseBody = response.body()!!.result
                        Log.d("isExitEdit", "1234567890")
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "게시글 편집에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<WriteContentResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("sendContentToServer", "onFailure: ${t.localizedMessage}", t)
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
            "자유토크" -> "FREE_TALK"
            else -> "PASS_EXPERIENCE" // 기본값
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedCategory = "PASS_EXPERIENCE"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? CommunityContentActivity)?.fetchContentInfo() // Fragment 닫히면 새로고침
    }
}
