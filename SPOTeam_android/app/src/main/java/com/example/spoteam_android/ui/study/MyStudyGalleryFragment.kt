package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.GalleryItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMystudyGalleryBinding

class MyStudyGalleryFragment : Fragment() {

    lateinit var binding: FragmentMystudyGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyGalleryBinding.inflate(inflater, container, false)

        binding.communityCategoryContentRv.layoutManager = GridLayoutManager(context, 3)

        val dataList : ArrayList<GalleryItem> = arrayListOf()

        dataList.apply {
            add(GalleryItem(R.drawable.study_spot_logo))
            add(GalleryItem(R.drawable.activity_main_ic_alaram))
            add(GalleryItem(R.drawable.activity_main_ic_sun))
            add(GalleryItem(R.drawable.ic_view))
            add(GalleryItem(R.drawable.activity_main_mystudy))
            add(GalleryItem(R.drawable.study_spot_logo))
            add(GalleryItem(R.drawable.activity_main_ic_alaram))
            add(GalleryItem(R.drawable.activity_main_ic_sun))
            add(GalleryItem(R.drawable.ic_view))
            add(GalleryItem(R.drawable.activity_main_mystudy))
            add(GalleryItem(R.drawable.study_spot_logo))
            add(GalleryItem(R.drawable.activity_main_ic_alaram))
            add(GalleryItem(R.drawable.activity_main_ic_sun))
            add(GalleryItem(R.drawable.ic_view))
            add(GalleryItem(R.drawable.activity_main_mystudy))
            add(GalleryItem(R.drawable.study_spot_logo))
            add(GalleryItem(R.drawable.activity_main_ic_alaram))
            add(GalleryItem(R.drawable.activity_main_ic_sun))
            add(GalleryItem(R.drawable.ic_view))
            add(GalleryItem(R.drawable.activity_main_mystudy))
            add(GalleryItem(R.drawable.study_spot_logo))
            add(GalleryItem(R.drawable.activity_main_ic_alaram))
            add(GalleryItem(R.drawable.activity_main_ic_sun))
            add(GalleryItem(R.drawable.ic_view))
            add(GalleryItem(R.drawable.activity_main_mystudy))
            add(GalleryItem(R.drawable.study_spot_logo))
            add(GalleryItem(R.drawable.activity_main_ic_alaram))
            add(GalleryItem(R.drawable.activity_main_ic_sun))
            add(GalleryItem(R.drawable.ic_view))
            add(GalleryItem(R.drawable.activity_main_mystudy))
            add(GalleryItem(R.drawable.study_spot_logo))
            add(GalleryItem(R.drawable.activity_main_ic_alaram))
            add(GalleryItem(R.drawable.activity_main_ic_sun))
            add(GalleryItem(R.drawable.ic_view))
            add(GalleryItem(R.drawable.activity_main_mystudy))

        }
        val dataRVAdapter = MyStudyGalleryFragmentRVAdapter(dataList)

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        return binding.root
    }

}
