package com.spot.android.ui.study

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
import com.spot.android.R
import com.spot.android.RetrofitInstance
import com.spot.android.databinding.FragmentMystudyWriteContentBinding
import com.spot.android.ui.community.CommunityAPIService
import com.spot.android.ui.community.StudyPostContentInfo
import com.spot.android.ui.community.StudyPostContentResponse
import com.spot.android.ui.community.StudyPostResponse
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

class MyStudyEditContentFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentMystudyWriteContentBinding
    private var postId : Int = -1
    private var studyId : Int = -1
    private lateinit var imageAdapter: WriteContentImageRVadapter
    private var currentAnnouncement : Boolean = false
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private val imageList = mutableListOf<Any>()

    private var selectedTheme: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyWriteContentBinding.inflate(inflater, container, false)
        isCancelable = false // 외부 클릭으로 닫히지 않도록 설정

        arguments?.let {
            val postId = it.getInt("MyStudyPostId",-1)
            val studyId = it.getInt("MyStudyId",-1)

            this.postId = postId
            this.studyId = studyId
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.thema_list,
            R.layout.write_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.write_spinner_dropdown_item)
            binding.mystudyCategorySpinner.adapter = adapter
        }

            getStudyPostContent()

        binding.writeContentTitleTv.text = "글수정"

        isCancelable = false // 외부 클릭으로 닫히지 않도록 설정

        initTextWatchers()

        binding.mystudyCategorySpinner.onItemSelectedListener = this

        binding.writeContentFinishBtn.setOnClickListener{
            submitContent()
        }

        binding.checkIc.setOnClickListener{
            if(currentAnnouncement) {
                binding.checkIc.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.gray),
                    PorterDuff.Mode.SRC_IN
                )
                currentAnnouncement = false
//                Log.d("MyStudyWriteContentFragment", isAnnouncement.toString())

                binding.themeTv.visibility = View.VISIBLE
                binding.mystudyCategorySpinner.visibility = View.VISIBLE

            } else {
                binding.checkIc.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.b500),
                    PorterDuff.Mode.SRC_IN
                )
                currentAnnouncement = true
//                Log.d("MyStudyWriteContentFragment", isAnnouncement.toString())

                binding.themeTv.visibility = View.GONE
                binding.mystudyCategorySpinner.visibility = View.GONE
            }
        }

        binding.addImageIv.setOnClickListener{
            getImageFromAlbum()
//            Log.d("imageFormat", "$profileImageURI")
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }

        // 갤러리에서 새로운 이미지 선택 처리
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

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MyStudyPostContentActivity)?.fetchContentInfo() // Fragment 닫히면 새로고침
    }

    private fun getStudyPostContent() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyPostContent(studyId, postId,true)
            .enqueue(object : Callback<StudyPostContentResponse> {
                override fun onResponse(
                    call: Call<StudyPostContentResponse>,
                    response: Response<StudyPostContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val contentResponse = response.body()
                        if (contentResponse?.isSuccess == "true") {
                            val contentInfo = contentResponse.result
                            currentAnnouncement = contentInfo.isAnnouncement

                            initContentInfo(contentInfo)

                        } else {
                            showError(contentResponse?.message)
                        }
                    } else {
//                        showError(response.code().toString())
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Error 400: ${response.code()}, Message: $errorBody")

                        showError("서버 오류 발생: $errorBody")
                    }

                }

                override fun onFailure(call: Call<StudyPostContentResponse>, t: Throwable) {
                    Log.e("CommunityContentActivity", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initContentInfo(contentInfo: StudyPostContentInfo) {
        binding.writeContentTitleEt.setText(contentInfo.title)
        binding.writeContentContentEt.setText(contentInfo.content)

        // 서버에서 가져온 이미지 리스트를 Uri 리스트로 변환
        val serverImageUrls = contentInfo.studyPostImages.map { it.imageUrl }

        // 서버 이미지 리스트와 기존의 갤러리에서 선택한 이미지 리스트 통합
        imageList.clear()
        imageList.addAll(serverImageUrls)

        // RecyclerView 어댑터 설정
        imageAdapter = WriteContentImageRVadapter(imageList)
        binding.addedImagesRv.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        imageAdapter.notifyDataSetChanged() // RecyclerView 갱신

        if (!currentAnnouncement) {
            binding.themeTv.visibility = View.VISIBLE
            binding.mystudyCategorySpinner.visibility = View.VISIBLE
            binding.checkIc.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.gray),
                PorterDuff.Mode.SRC_IN
            )

        } else {
            binding.themeTv.visibility = View.GONE
            binding.mystudyCategorySpinner.visibility = View.GONE
            binding.checkIc.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.b500),
                PorterDuff.Mode.SRC_IN
            )
        }

        val position = getCategoryPosition(contentInfo.theme)
        if (position != -1) {
            binding.mystudyCategorySpinner.setSelection(position)
        }
    }


    // 카테고리의 위치를 반환하는 함수
    private fun getCategoryPosition(theme: String): Int {
        val categories = resources.getStringArray(R.array.thema_list)
        return categories.indexOfFirst { it == getCategoryDisplayName(theme) }
    }

    // 서버에서 사용하는 카테고리 값을 사용자에게 보여줄 텍스트로 변환하는 함수
    private fun getCategoryDisplayName(category: String): String {
        return when (category) {
            "WELCOME" -> "가입인사"
            "INFO_SHARING" -> "정보공유"
            "STUDY_REVIEW" -> "스터디후기"
            "FREE_TALK" -> "자유"
            "QNA" -> "Q&A"
            else -> "WELCOME"
        }
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

    // 갤러리 열기 (여러 개 선택 가능)
    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 🔥 여러 개 선택 가능하도록 설정
        }
        getImageLauncher.launch(intent)
    }


    private fun submitContent() {
        val title = binding.writeContentTitleEt.text.toString().trim()
        val content = binding.writeContentContentEt.text.toString().trim()

        lifecycleScope.launch {
            var imagePart: MultipartBody.Part? = null
            var existingImage: RequestBody? = null

            if (imageList.isNotEmpty()) {
                val item = imageList[0] // 첫 번째 하나만 사용

                when (item) {
                    is Uri -> {
                        val file = getFileFromUri(item)
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                        existingImage = "".toRequestBody("text/plain".toMediaTypeOrNull())

                    }

                    is String -> {
                        existingImage = item.toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                }
            }

            val isAnnouncementPart = currentAnnouncement.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val themePart = selectedTheme.toRequestBody("text/plain".toMediaTypeOrNull())
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

            sendContentToServer(
                isAnnouncementPart,
                themePart,
                titlePart,
                contentPart,
                imagePart,
                existingImage
            )
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


    private fun sendContentToServer(
        isAnnouncementPart: RequestBody,
        themePart: RequestBody,
        titlePart: RequestBody,
        contentPart: RequestBody,
        imagePart: MultipartBody.Part?,
        existingImage: RequestBody?
    ) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.patchStudyPost(
            studyId,
            postId,
            isAnnouncementPart,
            themePart,
            titlePart,
            contentPart,
            imagePart,
            existingImage
        ).enqueue(object : Callback<StudyPostResponse> {
            override fun onResponse(call: Call<StudyPostResponse>, response: Response<StudyPostResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == "true") {
                    val writeContentResponseBody = response.body()!!.result
//                    showLog(writeContentResponseBody.toString())
                    dismiss()
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}, Message: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StudyPostResponse>, t: Throwable) {
                Log.e("API ERROR", "네트워크 오류 : ${t.message}", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedCategory = parent?.getItemAtPosition(position).toString()

        selectedTheme = when(selectedCategory) {
            "가입인사" -> "WELCOME"
            "정보공유" -> "INFO_SHARING"
            "스터디후기" -> "STUDY_REVIEW"
            "자유" -> "FREE_TALK"
            "Q&A" -> "QNA"
            else -> "WELCOME"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedTheme = "WELCOME"
    }

}