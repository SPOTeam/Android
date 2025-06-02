
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.spoteam_android.R
import com.example.spoteam_android.presentation.study.DetailStudyFragment
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ApplyStudyDialog @Inject constructor(
    @ActivityContext private val context: Context,
    private val fragment: DetailStudyFragment
) {

    private val dlg = Dialog(context)

    fun start(onMoveToHouseFragment: () -> Unit) {
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
                val memberCount = fragment.studyViewModel.memberCount.value ?: 0
                val maxPeople = fragment.studyViewModel.maxPeople.value ?: Int.MAX_VALUE

                if (memberCount >= maxPeople) {
                    Toast.makeText(context, "인원이 가득 찼습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    fragment.studyViewModel.applyStudy(studyId, memberId, introduction) { success ->
                        if (success) {
                            dlg.dismiss()
                            showCompleteDialog(onMoveToHouseFragment)
                        } else {
                            Toast.makeText(context, "스터디 신청에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
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

    private fun showCompleteDialog(onMoveToHouseFragment: () -> Unit) {
        val secondDialog = Dialog(context)
        secondDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        secondDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        secondDialog.setContentView(R.layout.dialog_apply_complete_study)

        val secondDialogButton = secondDialog.findViewById<Button>(R.id.dialog_complete_bt)
        val secondCloseIV: ImageView = secondDialog.findViewById(R.id.close_iv)

        secondDialogButton.setOnClickListener {
            secondDialog.dismiss()
            onMoveToHouseFragment()
        }

        secondCloseIV.setOnClickListener {
            secondDialog.dismiss()
        }

        secondDialog.show()
    }
}