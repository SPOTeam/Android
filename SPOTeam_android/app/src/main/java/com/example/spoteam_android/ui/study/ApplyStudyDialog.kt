import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyApplyResponse
import com.example.spoteam_android.StudyDetailsResponse
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.DetailStudyHomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplyStudyDialog(private val context: Context, private val fragment: DetailStudyFragment) {

    private val dlg = Dialog(context)

    fun start(onMoveToHouseFragment: () -> Unit) {
        // 타이틀바 제거 및 커스텀 다이얼로그 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setContentView(R.layout.dialog_apply_study)

        val editText = dlg.findViewById<EditText>(R.id.fragment_introduce_studyname_et)
        val btnMove = dlg.findViewById<Button>(R.id.dialog_complete_bt)
        val closeIv: ImageView = dlg.findViewById(R.id.close_iv)

        btnMove.isEnabled = false

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnMove.isEnabled = s?.isNotEmpty() == true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnMove.setOnClickListener {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("currentEmail", null)
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            val studyId = fragment.studyViewModel.studyId.value
            val introduction = editText.text.toString()

            if (memberId != -1 && studyId != null) {
                fetchStudyDetails(studyId) { success, memberCount, maxPeople ->
                    if (success) {
                        if (memberCount >= maxPeople) {
                            Toast.makeText(context, "인원이 가득 찼습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            applyToStudy(studyId, memberId, introduction) { applySuccess ->
                                if (applySuccess) {
                                    dlg.dismiss()

                                    val secondDialog = Dialog(context)
                                    secondDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                    secondDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    secondDialog.setContentView(R.layout.dialog_apply_complete_study)

                                    val secondDialogButton = secondDialog.findViewById<Button>(R.id.dialog_complete_bt)
                                    val secondCloseIV : ImageView = secondDialog.findViewById(R.id.close_iv)
                                    secondDialogButton.setOnClickListener {
                                        secondDialog.dismiss()
                                        onMoveToHouseFragment()
                                    }
                                    secondCloseIV.setOnClickListener {
                                        dlg.dismiss()
                                    }

                                    secondDialog.show()
                                } else {
                                    Toast.makeText(context, "스터디 신청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "스터디 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "멤버 ID 또는 스터디 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        closeIv.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()

    }

    private fun fetchStudyDetails(studyId: Int, callback: (Boolean, Int, Int) -> Unit) {
        Log.d("DetailStudyFragment", "fetchStudyDetails() 호출")
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getStudyDetails(studyId).enqueue(object : Callback<StudyDetailsResponse> {
            override fun onResponse(call: Call<StudyDetailsResponse>, response: Response<StudyDetailsResponse>) {
                if (response.isSuccessful) {
                    val studyDetailsResponse = response.body()
                    val studyDetails = studyDetailsResponse?.result
                    if (studyDetails != null) {
                        val memberCount = studyDetails.memberCount
                        val maxPeople = studyDetails.maxPeople
                        callback(true, memberCount, maxPeople)
                    } else {
                        callback(false, 0, 0)
                        Toast.makeText(context, "스터디 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    callback(false, 0, 0)
                    Toast.makeText(context, "스터디 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StudyDetailsResponse>, t: Throwable) {
                callback(false, 0, 0)
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun applyToStudy(studyId: Int, memberId: Int, introduction: String, callback: (Boolean) -> Unit) {
        val apiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val call = apiService.applyStudy(studyId, memberId, introduction)

        call.enqueue(object : Callback<StudyApplyResponse> {
            override fun onResponse(call: Call<StudyApplyResponse>, response: Response<StudyApplyResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    // 서버 전송 성공
                    callback(true)
                } else {
                    // 서버 전송 실패
                    callback(false)
                }
            }
            override fun onFailure(call: Call<StudyApplyResponse>, t: Throwable) {
                // 네트워크 오류 등으로 인한 실패
                callback(false)
            }
        })
    }
}
