package com.example.spoteam_android.todolist

import StudyViewModel
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentTodoListBinding
import com.example.spoteam_android.ui.calendar.EventAdapter
import com.example.spoteam_android.ui.calendar.EventViewModel
import java.util.Calendar

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var rvMyTodoList: RecyclerView
    private lateinit var rvDates: RecyclerView
    private lateinit var todoViewModel: TodoViewModel
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var dateAdapter: DateAdapter
    private lateinit var myTodoAdapter: TodoAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val eventViewModel: EventViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter
    private val todoList = mutableListOf<String>()
    private var selectedImageView: ImageView? = null
    private var userEnteredContent: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)

        val studyId = studyViewModel.studyId.value ?: 0

        val apiService = RetrofitInstance.retrofit.create(TodoApiService::class.java)
        val repository = TodoRepository(apiService)
        val factory = TodoViewModelFactory(repository)
        todoViewModel = ViewModelProvider(this, factory).get(TodoViewModel::class.java)

        myTodoAdapter = TodoAdapter(requireContext(), mutableListOf()) { content ->
            val date = "2024-11-08" // 예시 날짜
            todoViewModel.addTodoItem(studyId, content, date)  // 전달된 content로 API 요청
        }


        eventsRecyclerView = binding.eventrecyclerviewto

        // 날짜 생성 코드
        rvDates = binding.rvDates
        rvMyTodoList = binding.rvMyTodoList

        // LinearLayoutManager를 한 번만 생성하여 사용
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvDates.layoutManager = layoutManager
        rvMyTodoList.layoutManager = LinearLayoutManager(requireContext())

        todoViewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            Log.d("TodoListFragment", "Today is $date")

            val dates = (1..31).map {
                val isActive = (it.toString() == date)
                DateItem(it.toString(), isActive)
            }
            dateAdapter = DateAdapter(dates) { selectedDate ->
                onDateSelected(selectedDate)
                Log.d("TodoListFragment", "$selectedDate 를 선택하셨습니다.")
            }
            rvDates.adapter = dateAdapter // 어댑터 설정 추가

            // 날짜 가운데 정렬 코드
            val todayPosition = dates.indexOfFirst { it.date == date }
            if (todayPosition != -1) {
                rvDates.post {
                    Log.d("TodoListFragment", "Scrolling to today's date: $date")

                    // rvDates.getChildAt(0)이 null인지 확인
                    val childView = rvDates.getChildAt(0)
                    if (childView != null) {
                        val offset = rvDates.width / 2 - childView.width / 2
                        layoutManager.scrollToPositionWithOffset(todayPosition, offset)
                    } else {
                        // 적절한 fallback 처리
                        Log.e("TodoListFragment", "Unable to calculate offset: childView is null")
                    }
                }
            }
        })

        val userImageViews = listOf(
            binding.imvStudyowner,
            binding.imvStudyone1,
            binding.imvStudyone2,
            binding.imvStudyone3,
            binding.imvStudyone4,
            binding.imvStudyone5,
            binding.imvStudyone6,
            binding.imvStudyone7,
            binding.imvStudyone8,
            binding.imvStudyone9,
            binding.imvStudyone10
        ).filterNotNull()



        userImageViews.forEach { imageView ->
            imageView.setOnClickListener {
                highlightImageView(imageView)
            }
        }

        eventAdapter = EventAdapter(emptyList(), { /* 클릭 이벤트 처리 (필요시 추가) */ }, true)
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventAdapter

        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            Log.d("CalendarFragment", "Events updated: ${events.size} events")
            eventAdapter.updateEvents(events)

        })

        // 투두 리스트 어댑터 설정
        binding.rvMyTodoList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyTodoList.adapter = myTodoAdapter

        binding.imgbtnPlusTodolist.setOnClickListener {
            myTodoAdapter.addTodo()
            binding.rvMyTodoList.scrollToPosition(myTodoAdapter.itemCount - 1)
        }


        todoViewModel.addTodoResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response?.isSuccess == true) {
                response.result?.let { newTodo ->
                    Log.d("TodoListFragment", "Todo created: ${newTodo.content}")
                }
            } else {
                Log.e("TodoListFragment", "Failed to add todo22")
            }
        })

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

        return binding.root
    }

    private fun onDateSelected(selectedDate: String) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작합니다.

        try {
            val day = selectedDate.toInt() // selectedDate를 정수형으로 변환 (날짜를 나타냄)

            // EventViewModel을 사용하여 선택한 날짜의 일정을 로드
            eventViewModel.loadEvents(year, month, day)

            // 이벤트 로드 후, UI에 업데이트
            eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
                Log.d("TodoListFragment", "Events for $year-$month-$day: ${events.size} events")
                eventAdapter.updateEvents(events)
            })
        } catch (e: NumberFormatException) {
            Log.e("TodoListFragment", "Invalid date format: $selectedDate")
        }
    }

    private fun highlightImageView(imageView: ImageView) {
        selectedImageView?.setBackgroundResource(0)
        imageView.setBackgroundResource(R.drawable.selected_border_blue)  // 선택된 ImageView에 파란 테두리 설정
        selectedImageView = imageView  // 현재 선택된 ImageView 업데이트
    }
}