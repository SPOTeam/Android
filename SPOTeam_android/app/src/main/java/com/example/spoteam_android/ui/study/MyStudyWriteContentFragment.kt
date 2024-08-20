package com.example.spoteam_android.ui.study

import StudyViewModel
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.community.StudyPostResponse
import com.example.spoteam_android.ui.community.StudyWriteContentRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
                }
            }
        }

        binding.fragmentIntroduceStudyIv.setOnClickListener{
            getImageFromAlbum()
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

    private fun submitContent(studyId : Int) {
        val title = binding.writeContentTitleEt.text.toString().trim()
        val content = binding.writeContentContentEt.text.toString().trim()
        isAnnouncement = binding.mystudyWriteContentInfoLl.findViewById<CheckBox>(R.id.isAnnouncement_cb).isChecked
        val imagePart = profileImageURI?.let { prepareImagePart(it) }
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = StudyWriteContentRequest (
                isAnnouncement = isAnnouncement,
                theme = selectedTheme,
                title = title,
                content = content,
                images = listOf(imagePart)
            )


        Log.d("MYSTUDYWriteContentFragment", "${requestBody} , ${studyId}")

        // 서버로 데이터 전송
        if (requestBody != null) {
            sendContentToServer(requestBody)
        }
    }

    private fun prepareImagePart(uri: Uri): MultipartBody.Part? {
        val file = saveUriAsPng(uri)
        return file?.let {
            val requestFile = it.asRequestBody("image/png".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", it.name, requestFile)
        }
    }

    private fun saveUriAsPng(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // PNG 파일로 저장할 임시 파일 생성
            val tempFile = File(requireContext().cacheDir, "temp_image.png")
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            tempFile // 생성된 파일 반환
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendContentToServer(requestBody: StudyWriteContentRequest) {
        CommunityRetrofitClient.instance.postStudyPost(currentStudyId, requestBody)
            .enqueue(object : Callback<StudyPostResponse> {
                override fun onResponse(call: Call<StudyPostResponse>, response: Response<StudyPostResponse>) {
                    Log.d("WriteContentFragment", response.body()?.isSuccess.toString())
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        val writeContentResponseBody = response.body()!!.result
                        showLog(writeContentResponseBody.toString())
                        dismiss()
                    } else {
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
