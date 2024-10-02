import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentLocationStudyBinding
import com.example.spoteam_android.login.LocationItem
import com.example.spoteam_android.ui.study.IntroduceStudyFragment
import com.example.spoteam_android.ui.study.OnlineStudyFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import java.io.BufferedReader
import java.io.InputStreamReader

class LocationStudyFragment : Fragment() {

    private lateinit var binding: FragmentLocationStudyBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private val locationItemList = mutableListOf<LocationItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationStudyBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchFunctionality()
        loadLocalTsvFile() // TSV 파일을 읽어 데이터 초기화

        binding.fragmentIntroduceStudyBackBt.setOnClickListener {
            goToPreviusFragment()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        locationSearchAdapter = LocationSearchAdapter(locationItemList) { item ->
            // 아이템 클릭 시 처리
            openOnlineStudyFragment(item.address)
        }
        binding.activityLocationRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationSearchAdapter
            visibility = View.GONE // 초기에는 RecyclerView를 숨김
        }
    }

    private fun setupSearchFunctionality() {
        binding.activityLocationSearchEt.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.activityLocationSearchEt.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.activityLocationSearchEt.right - drawableEnd.bounds.width())) {
                    performSearch()
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.activityLocationSearchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경될 때마다 자동으로 검색 수행
                performSearch()
            }
        })
    }

    private fun loadLocalTsvFile() {
        val inputStream = requireContext().assets.open("region_data_processed.tsv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val tsvData = reader.use { it.readText() }
        parseTsvData(tsvData)
    }

    private fun parseTsvData(tsvData: String) {
        val rows = tsvData.split("\n").drop(1) // 첫 번째 줄은 헤더이므로 제외

        locationItemList.clear()
        for (row in rows) {
            val columns = row.split("\t")
            if (columns.size >= 5) {
                val item = LocationItem(
                    code = columns[0],
                    city = columns[1],
                    districtCity = columns[2],
                    districtGu = columns[3],
                    subdistrict = columns[4]
                )
                locationItemList.add(item)
            }
        }

        locationSearchAdapter.notifyDataSetChanged()

        // 검색 결과가 있을 때 RecyclerView를 보이게 함
        binding.activityLocationRv.visibility = if (locationSearchAdapter.itemCount > 0) View.VISIBLE else View.GONE
    }

    private fun performSearch() {
        val query = binding.activityLocationSearchEt.text.toString()
        locationSearchAdapter.filter(query)
        binding.activityLocationRv.visibility = if (locationSearchAdapter.itemCount > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun openOnlineStudyFragment(address: String) {
        val fragment = OnlineStudyFragment()
        val bundle = Bundle().apply {
            putString("ADDRESS", address)
            putBoolean("IS_OFFLINE", true) // "오프라인" 버튼 클릭 상태로 설정
            // 선택된 아이템의 code를 전달
            val selectedItemCode = locationSearchAdapter.getSelectedItem()?.code
            putString("CODE", selectedItemCode)
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, OnlineStudyFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }
}
