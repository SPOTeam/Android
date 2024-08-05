package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.CommentInfo
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.databinding.FragmentCommunityContentBinding
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.ENROLL
import com.example.spoteam_android.ui.community.contentComment.ContentCommentMultiViewRVAdapter
import com.example.spoteam_android.ui.community.contentComment.ContentCommentMultiViewRVAdapter.Companion.COMMENT
import com.example.spoteam_android.ui.community.contentComment.ContentCommentMultiViewRVAdapter.Companion.COMMENTREPLY
import org.w3c.dom.Comment

class CommunityContentInfoFragment : Fragment() {

    lateinit var binding: FragmentCommunityContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityContentBinding.inflate(inflater, container, false)

        binding.communityPrevIv.setOnClickListener{
            requireActivity().finish()
        }
        initMultiViewRecyclerView()

        return binding.root
    }

    private fun initMultiViewRecyclerView() {
        binding.contentCommentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val dataList :  ArrayList<CommentInfo> = arrayListOf()

        dataList.apply {
            add(CommentInfo("First", "FirstComment", COMMENT))
            add(CommentInfo("Second", "SecondComment", COMMENTREPLY))
            add(CommentInfo("Third", "ThirdComment", COMMENT))
            add(CommentInfo("Fourth", "FourthComment", COMMENTREPLY))
            add(CommentInfo("Fifth", "FifthComment", COMMENTREPLY))
            add(CommentInfo("Sixth", "SixthComment", COMMENT))
            add(CommentInfo("Seventh", "SeventhComment", COMMENTREPLY))
            add(CommentInfo("Eight", "EighthComment", COMMENTREPLY))
            add(CommentInfo("Ninth", "NinthComment", COMMENT))
            add(CommentInfo("Tenth", "TenthComment",  COMMENTREPLY))
        }

        val dataRVAdapter = ContentCommentMultiViewRVAdapter(dataList)
        binding.contentCommentRv.adapter = dataRVAdapter

    }
}
