package com.example.spoteam_android


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentSearchBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import androidx.appcompat.widget.SearchView.SearchAutoComplete

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val recentSearches = mutableListOf<String>()
    private lateinit var boardAdapter: BoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<BoardItem>()
        itemList.add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 600))
        itemList.add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 500))
        itemList.add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 400))

        boardAdapter = BoardAdapter(itemList)
        binding.rvBoard2.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }


        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    addSearchChip(it)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Do nothing for now
                return true
            }
        })
    }

    private fun addSearchChip(query: String) {
        if (query in recentSearches) return

        recentSearches.add(query)
        binding.txRecentlySearchedWord.visibility = View.VISIBLE

        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                binding.chipGroup.removeView(this)
                recentSearches.remove(query)
//                if (recentSearches.isEmpty()) {
//                    binding.txRecentlySearchedWord.visibility = View.GONE
//                }
            }
            setChipDrawable(
                ChipDrawable.createFromAttributes(
                requireContext(),null, 0, R.style.find_ChipStyle))
        }

        binding.chipGroup.addView(chip)

    }
}
