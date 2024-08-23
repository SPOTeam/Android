package com.example.spoteam_android.ui.study

import StudyViewModel
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMystudyWriteContentBinding
import com.example.spoteam_android.ui.community.CommunityContentActivity
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.community.StudyPostResponse
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

class MyStudyWriteContentFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentMystudyWriteContentBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private var currentStudyId : Int = -1
    private var selectedTheme: String = ""
    private var isAnnouncement: Boolean = false
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private var profileImageURI: Uri? = null // 이미지 URI를 저장할 변수 추가

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyWriteContentBinding.inflate(inflater, container, false)

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
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.mystudyCategorySpinner.adapter = adapter
        }


        // 이미지 선택을 위한 Launcher 설정
        getImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageURI : Uri? = result.data?.data
                if(selectedImageURI != null) {
                    binding.fragmentIntroduceStudyIv.setImageURI(selectedImageURI)
                    profileImageURI = selectedImageURI
                    Log.d("imageFormat", "$profileImageURI")
                }
            }
        }

        binding.fragmentIntroduceStudyIv.setOnClickListener{
            getImageFromAlbum()
            Log.d("imageFormat", "$profileImageURI")
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }

        return binding.root
    }

    // 갤러리 열기
    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        getImageLauncher.launch(intent)
    }

    private fun submitContent(studyId: Int) {
        val title = binding.writeContentTitleEt.text.toString().trim()
        val content = binding.writeContentContentEt.text.toString().trim()
        isAnnouncement = binding.mystudyWriteContentInfoLl.findViewById<CheckBox>(R.id.isAnnouncement_cb).isChecked

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 여러 이미지 파일을 담을 리스트 생성
        val imageParts = mutableListOf<MultipartBody.Part>()
        if (profileImageURI != null) {
            val uris = listOf(profileImageURI!!) // 여기에 여러 URI를 추가할 수 있음
            uris.forEach { uri ->
                val file = getFileFromUri(uri)
                if (file != null) {
                    val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
                    val imagePart =
                        MultipartBody.Part.createFormData("images", file.name, requestFile)
                    imageParts.add(imagePart)
                }
            }
        }

        // 나머지 데이터를 RequestBody로 변환
        val isAnnouncementPart = (if (isAnnouncement) "true" else "false").toRequestBody("text/plain".toMediaTypeOrNull())
        val themePart = selectedTheme.toRequestBody("text/plain".toMediaTypeOrNull())
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

        // 서버로 데이터 전송
        sendContentToServer(studyId, isAnnouncementPart, themePart, titlePart, contentPart, imageParts)
    }


    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "selected_image.png")
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
        imageParts: List<MultipartBody.Part>
    ) {
        CommunityRetrofitClient.instance.postStudyPost(
            studyId,
            isAnnouncementPart,
            themePart,
            titlePart,
            contentPart,
            imageParts
        ).enqueue(object : Callback<StudyPostResponse> {
            override fun onResponse(call: Call<StudyPostResponse>, response: Response<StudyPostResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == "true") {
                    val writeContentResponseBody = response.body()!!.result
                    showLog(writeContentResponseBody.toString())
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
