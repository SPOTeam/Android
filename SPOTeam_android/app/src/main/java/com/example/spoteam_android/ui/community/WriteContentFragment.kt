package com.example.spoteam_android.ui.community

import android.app.Activity
import android.content.Context
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
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyWriteContentBinding
import com.example.spoteam_android.ui.study.WriteContentImageRVadapter
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.reflect.typeOf

class WriteContentFragment() : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentMystudyWriteContentBinding
    private var selectedCategory: String = ""
    private var isAnonymous: Boolean = false
    private val imageList = mutableListOf<Any>()
    private lateinit var imageAdapter: WriteContentImageRVadapter
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyWriteContentBinding.inflate(inflater, container, false)

        isCancelable = false // 외부 클릭으로 닫히지 않도록 설정

        changeContent()
        initTextWatchers()
        initImageButtonAction()

        binding.mystudyCategorySpinner.onItemSelectedListener = this

        binding.writeContentFinishBtn.setOnClickListener{
            submitContent()
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.mystudyCategorySpinner.adapter = adapter
        }

        binding.addImageIv.setOnClickListener{
            getImageFromAlbum()
//            Log.d("imageFormat", "$profileImageURI")
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }

        return binding.root
    }

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        getImageLauncher.launch(intent)
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

    private fun sendContentToServer(
        isAnonymous: RequestBody,
        themePart: RequestBody,
        titlePart: RequestBody,
        contentPart: RequestBody,
        imageParts: MultipartBody.Part?
    ) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContent(titlePart, contentPart, themePart, isAnonymous, imageParts)
            .enqueue(object : Callback<WriteContentResponse> {
                override fun onResponse(call: Call<WriteContentResponse>, response: Response<WriteContentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        val writeContentResponseBody = response.body()!!.result
                        Log.d("WriteContent", "created postId = ${writeContentResponseBody.id}")

                        val intent = Intent(requireContext(), CommunityContentActivity::class.java)
                        intent.putExtra("postInfo", writeContentResponseBody.id) // postId 등 필요한 데이터 전달
                        startActivity(intent)
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
