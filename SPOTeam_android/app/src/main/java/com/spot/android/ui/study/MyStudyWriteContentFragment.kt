package com.spot.android.ui.study

import StudyViewModel
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spot.android.R
import com.spot.android.RetrofitInstance
import com.spot.android.databinding.FragmentMystudyWriteContentBinding
import com.spot.android.ui.community.CommunityAPIService
import com.spot.android.ui.community.StudyPostResponse
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

class MyStudyWriteContentFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentMystudyWriteContentBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private var currentStudyId : Int = -1
    private var selectedTheme: String = ""
    private var isAnnouncement: Boolean = false
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private val imageList = mutableListOf<Any>()
    private lateinit var imageAdapter: WriteContentImageRVadapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyWriteContentBinding.inflate(inflater, container, false)

        isCancelable = false // 외부 클릭으로 닫히지 않도록 설정

        initTextWatchers()

        // ViewModel에서 studyId를 관찰하고 변경될 때마다 fetchStudyMembers 호출
        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            Log.d("DetailStudyHomeFragment", "Received studyId from ViewModel: $studyId")
            if (studyId != null) {
                currentStudyId = studyId
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding.mystudyCategorySpinner.onItemSelectedListener = this

        binding.writeContentFinishBtn.setOnClickListener{
            submitContent(currentStudyId)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.thema_list,
            R.layout.write_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.write_spinner_dropdown_item)
            binding.mystudyCategorySpinner.adapter = adapter
        }

        binding.checkIc.setOnClickListener{
            if(isAnnouncement) {
                binding.checkIc.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.gray),
                    PorterDuff.Mode.SRC_IN
                )
                isAnnouncement = false
//                Log.d("MyStudyWriteContentFragment", isAnnouncement.toString())

                binding.themeTv.visibility = View.VISIBLE
                binding.mystudyCategorySpinner.visibility = View.VISIBLE

            } else {
                binding.checkIc.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.selector_blue),
                    PorterDuff.Mode.SRC_IN
                )
                isAnnouncement = true
//                Log.d("MyStudyWriteContentFragment", isAnnouncement.toString())

                binding.themeTv.visibility = View.GONE
                binding.mystudyCategorySpinner.visibility = View.GONE
            }
        }

        initImageButtonAction()

        binding.addImageIv.setOnClickListener{
            getImageFromAlbum()
//            Log.d("imageFormat", "$profileImageURI")
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
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


    private fun initImageButtonAction() {
        // RecyclerView 초기화
        imageAdapter = WriteContentImageRVadapter(imageList)
        binding.addedImagesRv.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // 이미지 선택을 위한 Launcher 설정
        getImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                imageList.clear()

                if (data?.data != null) {
                    // 🔥 단일 이미지 선택
                    val uri = data.data!!
                    if (!imageList.contains(uri)) {
                        imageList.add(uri)
                    }
                }

                imageAdapter.notifyDataSetChanged()
            }
        }
    }


    // 갤러리 열기 (여러 개 선택 가능)
    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        getImageLauncher.launch(intent)
    }


    private fun submitContent(studyId: Int) {
        val title = binding.writeContentTitleEt.text.toString().trim()
        val content = binding.writeContentContentEt.text.toString().trim()

        var imagePart: MultipartBody.Part? = null

        if (imageList.isNotEmpty()) {
            val item = imageList[0] // 하나만 사용
            if (item is Uri) {
                val file = getFileFromUri(item)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            }
        }

        // 나머지 데이터를 RequestBody로 변환
        val isAnnouncementPart = isAnnouncement.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val themePart = selectedTheme.toRequestBody("text/plain".toMediaTypeOrNull())
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())
        val existingImagePart = "".toRequestBody("text/plain".toMediaTypeOrNull())

        // 서버 전송
        sendContentToServer(
            studyId,
            isAnnouncementPart,
            themePart,
            titlePart,
            contentPart,
            imagePart,
            existingImagePart
        )
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
        studyId: Int,
        isAnnouncementPart: RequestBody,
        themePart: RequestBody,
        titlePart: RequestBody,
        contentPart: RequestBody,
        imagePart: MultipartBody.Part?,
        existingImage: RequestBody?
    ) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postStudyPost(
            studyId,
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
                    dismiss()
                    val intent = Intent(requireContext(), MyStudyPostContentActivity::class.java)
                    intent.putExtra("myStudyId", currentStudyId.toString())
                    intent.putExtra("myStudyPostId", writeContentResponseBody.postId.toString())
                    startActivity(intent)

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


    private fun showLog(message: String?) {
        Toast.makeText(requireContext(), "WriteContentFragment: $message", Toast.LENGTH_SHORT).show()
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