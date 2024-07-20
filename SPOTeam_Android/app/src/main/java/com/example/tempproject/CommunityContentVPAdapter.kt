package com.example.tempproject

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CommunityContentVPAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 7
    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }

//    override fun createFragment(position: Int): Fragment {
//        return
////        return when(position){
////            //SavedMusicFragment
////            0 -> SavedMusicFragment()
////            //MusicFileFragment
////            1 -> MusicFileFragment()
////            2 -> MusicFileFragment()
////            3 -> MusicFileFragment()
////            4 -> MusicFileFragment()
////            5 -> MusicFileFragment()
////            6 -> MusicFileFragment()
////            else -> SavedAlbumFragment()
////        }
//    }
}