package com.bytecoder.funplay.ui.video

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bytecoder.funplay.databinding.FragmentVideoListBinding
import com.bytecoder.funplay.viewmodel.VideoListViewModel
import com.bytecoder.funplay.ui.player.VideoPlayerActivity

class VideoListFragment : Fragment() {

    private var _bind: FragmentVideoListBinding? = null
    private val bind get() = _bind!!
    private val vm: VideoListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentVideoListBinding.inflate(inflater, container, false).also { _bind = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = VideoAdapter { video ->
            startActivity(
                Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                    // ✅ pass Uri as String
                    putExtra(VideoPlayerActivity.EXTRA_VIDEO_URI, video.uri.toString())
                    putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, video.name)
                    putExtra(VideoPlayerActivity.EXTRA_VIDEO_PATH, video.path)
                }
            )
        }
        bind.recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        bind.recycler.adapter = adapter

        vm.videos.observe(viewLifecycleOwner) { adapter.submitList(it) }
        vm.load()
    }

    // ✅ called from MainActivity after permissions are granted
    fun reloadVideos() {
        vm.load()
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }
}
