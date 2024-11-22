package com.example.spoteam_android.todolist

import StudyApiService
import StudyViewModel
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MemberResponse
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentTodoListBinding
import com.example.spoteam_android.ui.calendar.CalendarApiService
import com.example.spoteam_android.ui.calendar.Event
import com.example.spoteam_android.ui.calendar.EventAdapter
import com.example.spoteam_android.ui.calendar.EventViewModel
import com.example.spoteam_android.ui.calendar.ScheduleResponse
import com.example.spoteam_android.ui.study.DetailStudyHomeProfileAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var todoViewModel: TodoViewModel
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var dateAdapter: DateAdapter
    private lateinit var myTodoAdapter: TodoAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var eventAdapter: EventAdapter
    private var selectedDate: String = "" // 선택된 날짜를 저장할 변수
    private var selectedImageView: ImageView? = null
    private val eventViewModel: EventViewModel by activityViewModels()
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)

        val studyId = studyViewModel.studyId.value ?: 0

        val apiService = RetrofitInstance.retrofit.create(TodoApiService::class.java)
        val repository = TodoRepository(apiService)
        val factory = TodoViewModelFactory(repository, studyId)
        todoViewModel = ViewModelProvider(this, factory).get(TodoViewModel::class.java)

        // 오늘 날짜로 selectedDate 초기화
        val calendar = Calendar.getInstance()
        selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

        // 이벤트 RecyclerView 초기화
        eventAdapter = EventAdapter(emptyList(), { /* 클릭 이벤트 처리 (필요시 추가) */ }, true)
        binding.eventrecyclerviewto.layoutManager = LinearLayoutManager(requireContext())
        binding.eventrecyclerviewto.adapter = eventAdapter


        profileAdapter = DetailStudyHomeProfileAdapter(ArrayList())
        binding.fragmentDetailStudyHomeProfileRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentDetailStudyHomeProfileRv.adapter = profileAdapter


        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        fetchGetSchedule(studyId, year, month) {
            eventViewModel.loadEvents(year, month, today)

            // 어댑터와 데코레이터 갱신 추가
            val selectedDate = String.format("%04d-%02d-%02d", year, month, today)
            eventAdapter.updateSelectedDate(selectedDate)

            // 어댑터 데이터 갱신
            eventAdapter.updateEvents(eventViewModel.events.value ?: emptyList())
        }

        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            eventAdapter.updateEvents(events)
        })


        // 투두 리스트 어댑터 초기화
        myTodoAdapter = TodoAdapter(requireContext(), mutableListOf(), { content ->
            todoViewModel.addTodoItem(studyId, content, selectedDate)
        }, { toDoId ->
            todoViewModel.checkTodoItem(studyId, toDoId) // 체크박스 변경 시 API 호출
        })

        binding.rvMyTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myTodoAdapter
        }

        // 날짜 선택 RecyclerView 설정
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDates.layoutManager = layoutManager

        val dates = (1..31).map { DateItem(it.toString(), it.toString() == calendar.get(Calendar.DAY_OF_MONTH).toString()) }
        dateAdapter = DateAdapter(dates) { date ->
            selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$date"
            todoViewModel.onDateChanged(selectedDate)
        }
        binding.rvDates.adapter = dateAdapter

        fetchTodoList(studyId)

        todoViewModel.todoListResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.result?.content?.let { todos: List<TodoTask> ->
                // 받은 할 일 목록을 역순으로 정렬하여 RecyclerView에 갱신
                val reversedTodos = todos.reversed() // 역순으로 정렬
                myTodoAdapter.updateData(reversedTodos) // TodoTask 리스트를 그대로 전달
            } ?: run {
                // 받은 결과가 없거나 비어 있을 경우 빈 리스트로 RecyclerView 갱신
                myTodoAdapter.updateData(emptyList()) // 빈 리스트 전달하여 RecyclerView 초기화
            }
        })

        setupRecyclerViews(studyId)

        // ViewTreeObserver를 사용하여 날짜 목록 가운데로 스크롤
        binding.rvDates.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rvDates.viewTreeObserver.removeOnGlobalLayoutListener(this)
                scrollToTodayPosition()
            }
        })


        // imgbtnPlusTodolist 버튼 클릭 리스너
        binding.imgbtnPlusTodolist.setOnClickListener {
            myTodoAdapter.addTodo()
            binding.rvMyTodoList.scrollToPosition(myTodoAdapter.itemCount - 1)
        }

        val cbTodo1 = binding.cbTodo1
        val cbTodo2 = binding.cbTodo2
        val cbTodo3 = binding.cbTodo3

        binding.cbTodo1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTodo1.paintFlags = cbTodo3.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                cbTodo1.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            } else {
                cbTodo1.paintFlags = cbTodo3.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                cbTodo1.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }
        binding.cbTodo2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTodo2.paintFlags = cbTodo3.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                cbTodo2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            } else {
                cbTodo2.paintFlags = cbTodo3.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                cbTodo2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }



        binding.cbTodo3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTodo3.paintFlags = cbTodo3.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                cbTodo3.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            } else {
                cbTodo3.paintFlags = cbTodo3.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                cbTodo3.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }

        fetchStudyMembers(studyId)

        return binding.root
    }

    private fun fetchTodoList(studyId: Int) {
        // selectedDate의 형식을 yyyy-MM-dd로 보장
        val formattedDate = formatToDate(selectedDate)
        todoViewModel.fetchTodoList(studyId, page = 0, size = 10, date = formattedDate)
    }


    private fun formatToDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            String.format("%04d-%02d-%02d", parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            date // 이미 포맷이 맞는 경우 그대로 반환
        }
    }

    private fun setupRecyclerViews(studyId: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

        // 날짜 RecyclerView 설정
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDates.layoutManager = layoutManager

        val day = selectedDate.split("-")[2].toInt()

        val dates = (1..31).map { DateItem(it.toString(), it.toString() == calendar.get(Calendar.DAY_OF_MONTH).toString()) }
        dateAdapter = DateAdapter(dates) { date ->
            selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$date"
            Log.d("TodoListFragment", "Date selected: $selectedDate")
            todoViewModel.onDateChanged(selectedDate)

            val day = selectedDate.split("-").last() // "22"

            fetchGetSchedule(studyId, year, month) {
                Log.d("TodoListFragment", "fetchGetSchedule called with selectedDate: $selectedDate")
                eventViewModel.loadEvents(year, month, day.toInt())

                // 어댑터와 데코레이터 갱신 추가
                val selectedDate = String.format("%04d-%02d-%02d", year, month, day.toInt())
                eventAdapter.updateSelectedDate(selectedDate)

                // 어댑터 데이터 갱신
                eventAdapter.updateEvents(eventViewModel.events.value ?: emptyList())
            }

            eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
                eventAdapter.updateEvents(events)
            })
        }
        binding.rvDates.adapter = dateAdapter




    }

    private fun scrollToTodayPosition() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        val todayPosition = dateAdapter.dates.indexOfFirst { it.date == today }

        if (todayPosition != -1) {
            val childView = binding.rvDates.getChildAt(0)
            val offset = binding.rvDates.width / 2 - (childView?.width ?: 0) / 2
            layoutManager.scrollToPositionWithOffset(todayPosition, offset)
        }
    }


    private fun highlightImageView(imageView: ImageView) {
        selectedImageView?.setBackgroundResource(0)
        imageView.setBackgroundResource(R.drawable.selected_border_blue)  // 선택된 ImageView에 파란 테두리 설정
        selectedImageView = imageView  // 현재 선택된 ImageView 업데이트
    }

    private fun loadEventsForSelectedDate() {
        val parts = selectedDate.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        eventViewModel.loadEvents(year, month, day) // EventViewModel에서 이벤트 로드 요청
    }

    private fun fetchGetSchedule(studyId: Int, year: Int, month: Int, onComplete: () -> Unit) {

        val EventItems = arrayListOf<Event>()
        val service = RetrofitInstance.retrofit.create(CalendarApiService::class.java)

        service.GetScheuled(
            studyId = studyId,
            year = year,
            month = month,
        ).enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(
                call: Call<ScheduleResponse>,
                response: Response<ScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("CalendarFragment", "$apiResponse")

                    if (apiResponse?.isSuccess == true) {
                        // EventItems 데이터 로드
                        apiResponse.result.scheduleList.forEach { schedule ->
                            val eventItem = Event(
                                id = schedule.scheduleId,
                                title = schedule.title,
                                startYear = schedule.startedAt.substring(0, 4).toInt(),
                                startMonth = schedule.startedAt.substring(5, 7).toInt(),
                                startDay = schedule.startedAt.substring(8, 10).toInt(),
                                startHour = schedule.startedAt.substring(11, 13).toInt(),
                                startMinute = schedule.startedAt.substring(14, 16).toInt(),
                                endYear = schedule.finishedAt.substring(0, 4).toInt(),
                                endMonth = schedule.finishedAt.substring(5, 7).toInt(),
                                endDay = schedule.finishedAt.substring(8, 10).toInt(),
                                endHour = schedule.finishedAt.substring(11, 13).toInt(),
                                endMinute = schedule.finishedAt.substring(14, 16).toInt(),
                                period = schedule.period,
                                isAllDay = schedule.isAllDay
                            )
                            EventItems.add(eventItem)
                        }

                        Log.d("fetchGetSchedule", "Loaded Events: $EventItems")

                        // ViewModel 및 Adapter에 데이터 업데이트
                        eventViewModel.updateEvents(EventItems)
                        eventAdapter.updateEvents(EventItems)

                        // 콜백 호출
                        onComplete()
                    } else {
                        Log.d("fetchGetSchedule", "No matching events found")
                    }
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun fetchStudyMembers(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val kakaoNickname = sharedPreferences.getString("${email}_nickname", null)

        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { memberResponse ->
                        val members = memberResponse.result?.members ?: emptyList()
                        profileAdapter.updateList(members.map {
                            ProfileItem(profileImage = it.profileImage, nickname = it.nickname)
                        })

                        // 닉네임 리스트에서 현재 사용자의 닉네임과 일치하는지 확인
                        val nicknames = members.map { it.nickname }
                        val isNicknameFound = kakaoNickname?.let { nicknames.contains(it) } ?: false

                        // maxPeople과 memberCount 값을 가져오기 위해 ViewModel을 옵저빙
                        val maxPeople = studyViewModel.maxPeople.value
                        val memberCount = studyViewModel.memberCount.value
                        Log.d("max","${maxPeople},${memberCount}")

                        // 닉네임이 리스트에 있거나, memberCount와 maxPeople이 일치하면 버튼을 숨김
                        val shouldHideButton = isNicknameFound || (maxPeople != null && memberCount != null && memberCount >= maxPeople)


                        // RecyclerView의 제약 조건 설정
                        val layoutParams = binding.fragmentDetailStudyHomeProfileRv.layoutParams as ConstraintLayout.LayoutParams
                        binding.fragmentDetailStudyHomeProfileRv.layoutParams = layoutParams
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch study members", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}