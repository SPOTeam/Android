import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyApplyResponse
import com.example.spoteam_android.ui.study.DetailStudyHomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplyStudyDialog(private val context: Context, private val fragment: DetailStudyHomeFragment) {

    private val dlg = Dialog(context)

    fun start(onMoveToHouseFragment: () -> Unit) {
        // 타이틀바 제거 및 커스텀 다이얼로그 설정
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_apply_study)

        // EditText와 Button 참조
        val editText = dlg.findViewById<EditText>(R.id.dialog_apply_study_et)
        val btnMove = dlg.findViewById<Button>(R.id.dialog_apply_study_bt)

        // 처음에는 버튼을 비활성화
        btnMove.isEnabled = false

        // EditText의 내용이 변경될 때 버튼 활성화/비활성화
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnMove.isEnabled = s?.isNotEmpty() == true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnMove.setOnClickListener {
            // 서버에 데이터 전송
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("currentEmail", null)
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            val studyId = fragment.studyViewModel.studyId.value
            val introduction = editText.text.toString()


            if (memberId != -1 && studyId != null) {
                applyToStudy(memberId, studyId, introduction) { success ->
                    if (success) {
                        dlg.dismiss()

                        // 두 번째 다이얼로그 생성 및 표시
                        val secondDialog = Dialog(context)
                        secondDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        secondDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        secondDialog.setContentView(R.layout.dialog_apply_complete_study)

                        val secondDialogButton = secondDialog.findViewById<Button>(R.id.dialog_complete_bt)
                        secondDialogButton.setOnClickListener {
                            secondDialog.dismiss()
                            onMoveToHouseFragment()
                        }

                        secondDialog.show()
                    } else {
                        Toast.makeText(context, "스터디 신청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "멤버 ID 또는 스터디 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        dlg.show()
    }

    private fun applyToStudy(memberId: Int, studyId: Int, introduction: String, callback: (Boolean) -> Unit) {
        val apiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val call = apiService.applyStudy(memberId, studyId, introduction)

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
