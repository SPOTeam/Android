package com.example.spoteam_android.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCategoryBinding
import com.example.spoteam_android.databinding.FragmentCategoryNavigationviewBinding
import com.google.android.material.tabs.TabLayoutMediator


class CategoryNavViewFragment : Fragment() {

    lateinit var binding: FragmentCategoryNavigationviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryNavigationviewBinding.inflate(inflater, container, false)

        binding.languageBtn.setOnClickListener{
            moveCategoryFragment(1)
        }
        binding.licenseBtn.setOnClickListener{
            moveCategoryFragment(2)
        }
        binding.jobBtn.setOnClickListener{
            moveCategoryFragment(3)
        }
        binding.discussBtn.setOnClickListener{
            moveCategoryFragment(4)
        }
        binding.newsBtn.setOnClickListener{
            moveCategoryFragment(5)
        }
        binding.freeStudyBtn.setOnClickListener{
            moveCategoryFragment(6)
        }
        binding.projectBtn.setOnClickListener{
            moveCategoryFragment(7)
        }
        binding.contestBtn.setOnClickListener{
            moveCategoryFragment(8)
        }
        binding.majorStudyBtn.setOnClickListener{
            moveCategoryFragment(9)
        }
        binding.restBtn.setOnClickListener{
            moveCategoryFragment(10)
        }

        return binding.root
    }

    private fun moveCategoryFragment(type : Int) {
        val bundle = Bundle().apply {
            putInt("categoryType", type) // categoryType에 전달할 값 설정 (예: 1)
        }
        val categoryFragment = CategoryFragment().apply {
            arguments = bundle
        }
        removeBack()
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, categoryFragment)
            .commitAllowingStateLoss()
    }

    private fun removeBack(){
        val mainActivity = activity as? MainActivity
        mainActivity?.removeNavViewFragment()
        mainActivity?.hideBlur()
    }
}
