package com.example.spoteam_android.ui.study.todolist

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.MemberResponse
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentTodoListBinding
import com.example.spoteam_android.ui.study.calendar.CalendarApiService
import com.example.spoteam_android.ui.study.calendar.Event
import com.example.spoteam_android.ui.study.calendar.EventAdapter
import com.example.spoteam_android.ui.study.calendar.EventViewModel
import com.example.spoteam_android.ui.study.calendar.ScheduleResponse
import com.example.spoteam_android.ui.study.DetailStudyHomeProfileAdapter
import com.prolificinteractive.materialcalendarview.CalendarDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar
import java.time.YearMonth

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var todoViewModel: TodoViewModel
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var myTodoAdapter: TodoAdapter
    private lateinit var otherTodoAdapter: OtherTodoAdapter
    private lateinit var todoEventAdapter: TodoEventAdapter
    private lateinit var todoDateAdapter: TodoDateAdapter
    private lateinit var selectedDate: String // 멤버 변수로 선언
    private val eventViewModel: EventViewModel by activityViewModels()
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter
    private lateinit var memberIdMap: Map<ProfileItem, Int>
    private var selectedMemberId: Int? = null
    private lateinit var repository: TodoRepository // repository 선언

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)


        // 투두리스트 EdixText 다른 곳 클릭 시 포커싱 해제
        binding.root.setOnTouchListener { view, _ ->
            view.performClick()
            val focusedView = activity?.currentFocus
            if (focusedView != null) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                focusedView.clearFocus()
            }

            myTodoAdapter.cancelIfEditing()

            false
        }

        val studyId = studyViewModel.studyId.value ?: 0

        val apiService = RetrofitInstance.retrofit.create(TodoApiService::class.java)
        repository = TodoRepository(apiService) // 기존 로직 유지
        val factory = TodoViewModelFactory(repository, studyId)
        todoViewModel = ViewModelProvider(this, factory).get(TodoViewModel::class.java)

        // 오늘 날짜로 selectedDate 초기화
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val today = calendar.get(Calendar.DAY_OF_MONTH)

        val currentYear = LocalDate.now().year // 현재 연도
        val currentMonth = LocalDate.now().monthValue // 현재 월

        Log.d("Todo","1111")

        Log.d("Todo","${getTotalWeeksInMonth(currentYear,currentMonth)}")

        val daysOfCurrentMonth = getDaysOfMonthWithPadding(currentYear, currentMonth)


        var selectedDate2 = 1


        todoDateAdapter = TodoDateAdapter(daysOfCurrentMonth, selectedDate2) { newSelectedDate ->
            selectedDate2 = newSelectedDate
            todoDateAdapter.updateDates(daysOfCurrentMonth, selectedDate2)
        }

        binding.rvCalendar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCalendar.adapter = todoDateAdapter



        selectedDate  = "${calendar.get(Calendar.YEAR)}-" +
                "${(calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}-" +
                "${calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}"

        // 리사이클러뷰 초기화, 변수 선언등 UI 표시 이전에 선행되어야 할 작업 수행

        todoEventAdapter = TodoEventAdapter(emptyList(), { /* 클릭 이벤트 처리 (필요시 추가) */ }, true)
        binding.eventrecyclerviewto.layoutManager = LinearLayoutManager(requireContext())
        binding.eventrecyclerviewto.adapter = todoEventAdapter

        profileAdapter = DetailStudyHomeProfileAdapter(
            ArrayList(),
            { profileItem ->
                val memberId = memberIdMap[profileItem]
                if (memberId != null) {
                    selectedMemberId = memberId
                    fetchOtherTodoList(studyId, memberId, selectedDate)
                }
            },
            isTodo = true // ← 여기 true로 넘김
        )

        binding.fragmentDetailStudyHomeProfileRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentDetailStudyHomeProfileRv.adapter = profileAdapter

        //내 투두리스트
        myTodoAdapter = TodoAdapter(
            context = requireContext(),
            todoList = mutableListOf(),
            onAddTodo = { content ->
                todoViewModel.addTodoItem(studyId, content, selectedDate)
            },
            onCheckTodo = { toDoId ->
                todoViewModel.checkTodo(studyId, toDoId)
            },
            repository = repository, // TodoRepository 전달
            studyId = studyId // 스터디 ID 전달
        )

        binding.rvMyTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myTodoAdapter
        }

        //다른 스터디원 투두리스트
        otherTodoAdapter = OtherTodoAdapter(requireContext(), mutableListOf())

        binding.rvOtherTodo.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = otherTodoAdapter
        }


        // 최초 투두리스트, 스케쥴 조회
        todoViewModel.onDateChanged(selectedDate)
        fetchTodoList(studyId, today.toString().padStart(2, '0'))

        //캘린더 일정 조회 API 최초 호출
        fetchGetSchedule(studyId, year, month) {
            eventViewModel.loadEvents(year, month, today)

            // 어댑터와 데코레이터 갱신 추가
            todoEventAdapter.updateSelectedDate(selectedDate)

            // 어댑터 데이터 갱신
            todoEventAdapter.updateEvents(eventViewModel.events.value ?: emptyList())

            todoEventAdapter.updateSelectedDate(selectedDate)
            eventViewModel.events.value?.let { todoEventAdapter.updateEvents(it) }

            val updatedEventDays = getEventDaysForMonth(currentYear, currentMonth)
            todoDateAdapter.updateEventDays(updatedEventDays)


            // ✅ 선택한 날짜에 이벤트가 있는지 확인
            val hasEvent = todoEventAdapter.hasEventOnDay(today)
            binding.txScheduledEvent.visibility = if (hasEvent) View.VISIBLE else View.GONE
        }




        // ✅ 기존 selectedDate 변수를 그대로 사용
        todoDateAdapter = TodoDateAdapter(daysOfCurrentMonth, today) { newSelectedDate ->
            selectedDate = formatToDate("$currentYear-${currentMonth.toString().padStart(2, '0')}-${newSelectedDate.toString().padStart(2, '0')}")

            // ✅ 날짜 선택 시 UI 반영
            todoDateAdapter.updateDates(daysOfCurrentMonth, newSelectedDate)

            // ✅ 선택된 날짜에 대한 API 요청 실행
            todoViewModel.onDateChanged(selectedDate)
            fetchTodoList(studyId, newSelectedDate.toString().padStart(2, '0'))

            fetchGetSchedule(studyId, currentYear, currentMonth) {
                eventViewModel.loadEvents(currentYear, currentMonth, newSelectedDate)
                todoEventAdapter.updateSelectedDate(selectedDate)
                eventViewModel.events.value?.let { todoEventAdapter.updateEvents(it) }

                Log.d("checkList", "📦 이벤트 리스트: ${eventViewModel.events.value}")

                val updatedEventDays = getEventDaysForMonth(currentYear, currentMonth)
                todoDateAdapter.updateEventDays(updatedEventDays)

                val hasEvent = todoEventAdapter.hasEventOnDay(today)
                binding.txScheduledEvent.visibility = if (hasEvent) View.VISIBLE else View.GONE
            }

            // ✅ 기존의 otherTodoList 초기화
            profileAdapter.resetBorder()
            todoViewModel.clearOtherTodoList()
            otherTodoAdapter.clearData()
        }


        binding.rvCalendar.adapter = todoDateAdapter

        scrollToTodayPosition()



        // 내 투두리스트 조회 API
        todoViewModel.myTodoListResponse.observe(viewLifecycleOwner) { response ->
            response?.result?.content?.let { todos ->
                // 받은 데이터가 이전과 동일한 경우 RecyclerView를 갱신하지 않음
                if (todos != myTodoAdapter.getCurrentData()) {
                    val reversedTodos = todos.reversed() // 역순으로 정렬
                    myTodoAdapter.updateData(reversedTodos.toMutableList())
                }
            } ?: run {
                myTodoAdapter.updateData(emptyList<TodoTask>().toMutableList())
            }

        }

        // 스터디원 투두리스트 조회 API
        todoViewModel.otherTodoListResponse.observe(viewLifecycleOwner) { response ->
            response?.result?.content?.let { todos ->
                Log.d("TodoFramgment_other","${todos}")

                if (todos.isNotEmpty()) {
                    otherTodoAdapter.updateData(todos.toMutableList())
                } else {
                    otherTodoAdapter.updateData(emptyList())
                }
            } ?: run {
                otherTodoAdapter.updateData(emptyList())
            }
        }



        // + 버튼을 통해 내 투두리스트를 추가할 수 있음
        binding.imgbtnPlusTodolist.setOnClickListener {
            myTodoAdapter.addTodo(selectedDate)
            binding.rvMyTodoList.scrollToPosition(myTodoAdapter.itemCount - 1)
        }

        //스터디원 프로필 조회 API 호출
        fetchStudyMembers(studyId)

        val eventDays = getEventDaysForMonth(currentYear, currentMonth)
        todoDateAdapter.updateEventDays(eventDays)



        return binding.root
    }


    // 내 투두리스트 조회
    private fun fetchTodoList(studyId: Int, date:String) {

        val calendar = Calendar.getInstance()
        val formattedDate = formatToDate("${calendar.get(Calendar.YEAR)}-" +
                "${(calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}-" +
                date)
        todoViewModel.fetchTodoList(studyId, page = 0, size = 10, date = formattedDate)

        todoViewModel.myTodoListResponse.observe(viewLifecycleOwner) { response ->
            response?.result?.content?.let { todos ->
                // 데이터를 역순으로 정렬
                val reversedTodos = todos.reversed()
                myTodoAdapter.updateData(reversedTodos.toMutableList())
            } ?: run {
                myTodoAdapter.updateData(emptyList<TodoTask>().toMutableList())
            }
        }
    }


    // 스터디원 투두 리스트 조회
    private fun fetchOtherTodoList(studyId: Int, memberId: Int, date: String) {

        todoViewModel.fetchOtherToDoList(studyId, memberId, page = 0, size = 10, date = date)
        todoViewModel.otherTodoListResponse.observe(viewLifecycleOwner) { response ->
            response?.result?.content?.let { todos ->
                // 데이터를 역순으로 정렬
                val reversedTodos = todos.reversed()
                otherTodoAdapter.updateData(reversedTodos.toMutableList())
            } ?: run {
                otherTodoAdapter.updateData(emptyList())
            }
        }
    }



    //날짜 형식 조정
    private fun formatToDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            String.format("%04d-%02d-%02d", parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            date // 이미 포맷이 맞는 경우 그대로 반환
        }
    }


    // 캘린더 일정 조회
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
                        // ViewModel 및 Adapter에 데이터 업데이트
                        eventViewModel.updateEvents(EventItems)

                        todoEventAdapter.updateEvents(EventItems)

                        // 콜백 호출
                        onComplete()
                    }
                }
            }



            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    // 스터디 멤버 조회
    private fun fetchStudyMembers(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val kakaoNickname = sharedPreferences.getString("${email}_nickname", null)

        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { memberResponse ->

                        val members = memberResponse.result.members

                        // ProfileItem 생성
                        val profileItems = members.map {
                            ProfileItem(
                                profileImage = it.profileImage,
                                nickname = it.nickname
                            )
                        }

                        // ProfileItem과 memberId 매핑
                        memberIdMap = members.associateBy(
                            keySelector = { ProfileItem(profileImage = it.profileImage, nickname = it.nickname) },
                            valueTransform = { it.memberId }
                        )

                        // RecyclerView 업데이트
                        profileAdapter.updateList(profileItems)

                        // 닉네임 리스트에서 현재 사용자의 닉네임과 일치하는지 확인
                        val nicknames = members.map { it.nickname }
                        val isNicknameFound = kakaoNickname?.let { nicknames.contains(it) } ?: false

                        // maxPeople과 memberCount 값을 가져오기 위해 ViewModel을 옵저빙
                        val maxPeople = studyViewModel.maxPeople.value
                        val memberCount = studyViewModel.memberCount.value

                        // 닉네임이 리스트에 있거나, memberCount와 maxPeople이 일치하면 버튼을 숨김
                        val isMember = isNicknameFound
                        if (isMember){
                            binding.imgbtnPlusTodolist.visibility = View.VISIBLE
                        }else{
                            binding.imgbtnPlusTodolist.visibility = View.GONE
                        }



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


    fun getDaysOfMonthWithPadding(year: Int, month: Int): List<Triple<Int?, Boolean, Boolean>> {
        val yearMonth = YearMonth.of(year, month)
        val daysInMonth = yearMonth.lengthOfMonth()

        // 🔹 이번 달 1일의 요일 가져오기 (월요일=1, 일요일=7)
        val firstDayOfMonth = LocalDate.of(year, month, 1).dayOfWeek.value

        // 🔹 월요일을 기준으로 정렬하기 위해 필요한 빈 칸 개수 계산
        val startPadding = if (firstDayOfMonth == 1) 0 else firstDayOfMonth - 1

        // 🔹 이전 달 정보 가져오기
        val previousMonth = if (month == 1) 12 else month - 1
        val previousYear = if (month == 1) year - 1 else year
        val previousMonthDays = YearMonth.of(previousYear, previousMonth).lengthOfMonth()

        val daysList = mutableListOf<Triple<Int?, Boolean, Boolean>>() // (날짜, 현재 달 여부, 클릭 가능 여부)


        // ✅ 현재 월의 날짜 추가 (정상 처리)
        for (i in 1..daysInMonth) {
            daysList.add(Triple(i, true, true)) // 현재 달, 클릭 가능
        }

        return daysList
    }



    fun getTotalWeeksInMonth(year: Int, month: Int): Int {
        val daysList = getDaysOfMonthWithPadding(year, month)
        return (daysList.size + 6) / 7 // 7일씩 나누고 올림 처리
    }

    // ✅ 오늘 날짜를 RecyclerView 중앙에 정렬하는 함수
    private fun scrollToTodayPosition() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) // 오늘 날짜 가져오기
        val todayPosition = todoDateAdapter.getPositionForDate(today) // 오늘 날짜의 위치 찾기

        if (todayPosition != -1) {
            binding.rvCalendar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.rvCalendar.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val childView = binding.rvCalendar.getChildAt(0)
                    val offset = binding.rvCalendar.width / 2 - (childView?.width ?: 0) / 2
                    (binding.rvCalendar.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(todayPosition, offset)
                }
            })
        }
    }

    fun getEventDaysForMonth(year: Int, month: Int): Set<Int> {
        val days = mutableSetOf<Int>()

        // ✅ ViewModel의 이벤트 전체 날짜에서 현재 월에 해당하는 것만 필터링
        val allEventDays = eventViewModel.getEventDates()

        allEventDays.forEach { calendarDay ->
            if (calendarDay.year == year && calendarDay.month == month) {
                days.add(calendarDay.day)
            }
        }

        Log.d("eventDaySet", "📅 ${year}년 ${month}월 기준 파란 점 찍힐 날짜들: $days")
        return days
    }



}