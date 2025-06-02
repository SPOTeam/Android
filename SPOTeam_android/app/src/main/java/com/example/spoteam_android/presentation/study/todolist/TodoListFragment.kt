package com.example.spoteam_android.presentation.study.todolist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentTodoListBinding
import com.example.spoteam_android.presentation.study.DetailStudyHomeProfileAdapter
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.example.spoteam_android.presentation.study.calendar.CalendarApiService
import com.example.spoteam_android.presentation.study.calendar.Event
import com.example.spoteam_android.presentation.study.calendar.EventViewModel
import com.example.spoteam_android.presentation.study.calendar.ScheduleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var todoViewModel: TodoViewModel
    private val studyViewModel: StudyViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by activityViewModels()

    private lateinit var myTodoAdapter: TodoAdapter
    private lateinit var otherTodoAdapter: OtherTodoAdapter
    private lateinit var todoEventAdapter: TodoEventAdapter
    private lateinit var todoDateAdapter: TodoDateAdapter
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter
    private lateinit var repository: TodoRepository

    private lateinit var memberIdMap: Map<ProfileItem, Int>
    private var selectedMemberId: Int? = null
    private lateinit var selectedDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)

        studyViewModel.loadNickname()

        val studyId = studyViewModel.studyId.value ?: 0
        val apiService = RetrofitInstance.retrofit.create(TodoApiService::class.java)
        repository = TodoRepository(apiService)
        val factory = TodoViewModelFactory(repository, studyId)
        todoViewModel = ViewModelProvider(this, factory)[TodoViewModel::class.java]

        // 날짜 초기화
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        selectedDate = String.format("%04d-%02d-%02d", year, month, today)

        // 달력 어댑터
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().monthValue
        val daysOfCurrentMonth = getDaysOfMonthWithPadding(currentYear, currentMonth)

        todoDateAdapter = TodoDateAdapter(daysOfCurrentMonth, today) { newSelectedDate ->
            selectedDate = formatToDate("$currentYear-$currentMonth-$newSelectedDate")
            todoDateAdapter.updateDates(daysOfCurrentMonth, newSelectedDate)
            todoViewModel.onDateChanged(selectedDate)
            fetchTodoList(studyId, newSelectedDate.toString().padStart(2, '0'))
            fetchGetSchedule(studyId, currentYear, currentMonth) {
                eventViewModel.loadEvents(currentYear, currentMonth, newSelectedDate)
                todoEventAdapter.updateSelectedDate(selectedDate)
                todoEventAdapter.updateEvents(eventViewModel.events.value ?: emptyList())
                todoDateAdapter.updateEventDays(getEventDaysForMonth(currentYear, currentMonth))
                binding.txScheduledEvent.visibility =
                    if (todoEventAdapter.hasEventOnDay(newSelectedDate)) View.VISIBLE else View.GONE
            }
            profileAdapter.resetBorder()
            todoViewModel.clearOtherTodoList()
            otherTodoAdapter.clearData()
        }

        binding.rvCalendar.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = todoDateAdapter
        }

        scrollToTodayPosition()

        // 내 투두 어댑터
        myTodoAdapter = TodoAdapter(requireContext(), mutableListOf(),
            onAddTodo = { content -> todoViewModel.addTodoItem(studyId, content, selectedDate) },
            onCheckTodo = { id -> todoViewModel.checkTodo(studyId, id) },
            repository = repository,
            studyId = studyId
        )
        binding.rvMyTodoList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyTodoList.adapter = myTodoAdapter

        // 스터디원 투두 어댑터
        otherTodoAdapter = OtherTodoAdapter(requireContext(), mutableListOf())
        binding.rvOtherTodo.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOtherTodo.adapter = otherTodoAdapter

        // 이벤트 어댑터
        todoEventAdapter = TodoEventAdapter(emptyList(), {}, true)
        binding.eventrecyclerviewto.layoutManager = LinearLayoutManager(requireContext())
        binding.eventrecyclerviewto.adapter = todoEventAdapter

        // 스터디원 프로필 어댑터
        profileAdapter = DetailStudyHomeProfileAdapter(ArrayList(), { profileItem ->
            memberIdMap[profileItem]?.let {
                selectedMemberId = it
                fetchOtherTodoList(studyId, it, selectedDate)
            }
        }, isTodo = true)
        binding.fragmentDetailStudyHomeProfileRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentDetailStudyHomeProfileRv.adapter = profileAdapter

        // 키보드 숨김 처리
        binding.root.setOnTouchListener { view, _ ->
            view.performClick()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            activity?.currentFocus?.let {
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                it.clearFocus()
            }
            myTodoAdapter.cancelIfEditing()
            false
        }

        // 데이터 초기 조회
        todoViewModel.onDateChanged(selectedDate)
        fetchTodoList(studyId, today.toString().padStart(2, '0'))
        fetchGetSchedule(studyId, year, month) {
            eventViewModel.loadEvents(year, month, today)
            todoEventAdapter.updateSelectedDate(selectedDate)
            todoEventAdapter.updateEvents(eventViewModel.events.value ?: emptyList())
            todoDateAdapter.updateEventDays(getEventDaysForMonth(currentYear, currentMonth))
            binding.txScheduledEvent.visibility =
                if (todoEventAdapter.hasEventOnDay(today)) View.VISIBLE else View.GONE
        }

        // 내 투두 관찰
        todoViewModel.myTodoListResponse.observe(viewLifecycleOwner) {
            val todos = it?.result?.content?.reversed() ?: emptyList()
            myTodoAdapter.updateData(todos.toMutableList())
        }

        // 다른 스터디원 투두 관찰
        todoViewModel.otherTodoListResponse.observe(viewLifecycleOwner) {
            val todos = it?.result?.content?.reversed() ?: emptyList()
            otherTodoAdapter.updateData(todos.toMutableList())
        }

        // + 버튼 클릭
        binding.imgbtnPlusTodolist.setOnClickListener {
            myTodoAdapter.addTodo(selectedDate)
            binding.rvMyTodoList.scrollToPosition(myTodoAdapter.itemCount - 1)
        }

        // 스터디 멤버 조회
        fetchStudyMembers(studyId)

        return binding.root
    }

    private fun fetchTodoList(studyId: Int, day: String) {
        val date = formatToDate("${Calendar.getInstance().get(Calendar.YEAR)}-" +
                "${(Calendar.getInstance().get(Calendar.MONTH) + 1).toString().padStart(2, '0')}-$day")
        todoViewModel.fetchTodoList(studyId, 0, 10, date)
    }

    private fun fetchOtherTodoList(studyId: Int, memberId: Int, date: String) {
        todoViewModel.fetchOtherToDoList(studyId, memberId, 0, 10, date)
    }

    private fun fetchGetSchedule(studyId: Int, year: Int, month: Int, onComplete: () -> Unit) {
        val api = RetrofitInstance.retrofit.create(CalendarApiService::class.java)
        api.GetScheuled(studyId, year, month).enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                if (response.isSuccessful) {
                    val eventList = response.body()?.result?.scheduleList?.map {
                        Event(
                            id = it.scheduleId,
                            title = it.title,
                            startYear = it.startedAt.substring(0, 4).toInt(),
                            startMonth = it.startedAt.substring(5, 7).toInt(),
                            startDay = it.startedAt.substring(8, 10).toInt(),
                            startHour = it.startedAt.substring(11, 13).toInt(),
                            startMinute = it.startedAt.substring(14, 16).toInt(),
                            endYear = it.finishedAt.substring(0, 4).toInt(),
                            endMonth = it.finishedAt.substring(5, 7).toInt(),
                            endDay = it.finishedAt.substring(8, 10).toInt(),
                            endHour = it.finishedAt.substring(11, 13).toInt(),
                            endMinute = it.finishedAt.substring(14, 16).toInt(),
                            period = it.period,
                            isAllDay = it.isAllDay
                        )
                    } ?: emptyList()
                    eventViewModel.updateEvents(eventList)
                    todoEventAdapter.updateEvents(eventList)
                    onComplete()
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "일정 조회 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchStudyMembers(studyId: Int) {
        studyViewModel.fetchStudyMembers(studyId) { response ->
            response?.let {
                val members = it.members
                val nickname = studyViewModel.nickname.value
                memberIdMap = members.associateBy(
                    { member -> ProfileItem(member.profileImage, member.nickname) },
                    { member -> member.memberId }
                )
                profileAdapter.updateList(memberIdMap.keys.toList())

                val isMember = nickname != null && members.any { it.nickname == nickname }
                binding.imgbtnPlusTodolist.visibility = if (isMember) View.VISIBLE else View.GONE
            } ?: run {
                Toast.makeText(requireContext(), "스터디 멤버 조회 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun formatToDate(date: String): String {
        val parts = date.split("-")
        return String.format("%04d-%02d-%02d", parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
    }

    private fun getDaysOfMonthWithPadding(year: Int, month: Int): List<Triple<Int?, Boolean, Boolean>> {
        val yearMonth = YearMonth.of(year, month)
        return (1..yearMonth.lengthOfMonth()).map { Triple(it, true, true) }
    }

    private fun getEventDaysForMonth(year: Int, month: Int): Set<Int> {
        return eventViewModel.getEventDates()
            .filter { it.year == year && it.month == month }
            .map { it.day }
            .toSet()
    }

    private fun scrollToTodayPosition() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val position = todoDateAdapter.getPositionForDate(today)
        if (position != -1) {
            binding.rvCalendar.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.rvCalendar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val offset = binding.rvCalendar.width / 2 -
                            (binding.rvCalendar.getChildAt(0)?.width ?: 0) / 2
                    (binding.rvCalendar.layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(position, offset)
                }
            })
        }
    }
}
