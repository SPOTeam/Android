package com.example.spoteam_android.todolist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentTodoListBinding
import com.example.spoteam_android.ui.calendar.EventAdapter
import com.example.spoteam_android.ui.calendar.EventViewModel

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var rvMyTodoList: RecyclerView
    private lateinit var rvDates: RecyclerView
    private val todoViewModel: TodoViewModel by activityViewModels() // TodoViewModel을 공유
    private lateinit var dateAdapter: DateAdapter
    private lateinit var myTodoAdapter: TodoAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val eventViewModel: EventViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_todo_list, container, false)



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




        eventAdapter = EventAdapter(emptyList(), { /* 클릭 이벤트 처리 (필요시 추가) */ }, true)
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventAdapter

        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            Log.d("CalendarFragment", "Events updated: ${events.size} events")
            eventAdapter.updateEvents(events)

        })

        // 투두 리스트 어댑터 설정
        myTodoAdapter = TodoAdapter(emptyList())
        rvMyTodoList.adapter = myTodoAdapter

        // TodoViewModel의 투두 리스트를 관찰하여 RecyclerView 업데이트
        todoViewModel.myTodos.observe(viewLifecycleOwner, Observer { todos ->
            myTodoAdapter.updateTodos(todos)
        })

        return binding.root
    }

    private fun onDateSelected(selectedDate: String) {
        // 날짜 선택 시 호출될 동작 정의
    }
}
