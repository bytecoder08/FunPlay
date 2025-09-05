package com.bytecoder.funplay.ui.player

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bytecoder.funplay.databinding.DialogPlayerOptionsBinding
import com.bytecoder.funplay.player.PlaybackConfig
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlayerOptionsDialog : DialogFragment() {
    private val scope = MainScope()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bind = DialogPlayerOptionsBinding.inflate(layoutInflater)
        val player = (requireActivity() as? VideoPlayerActivity)?.let { it }?.let { null }
        val exo = try {
            (requireActivity() as? VideoPlayerActivity)?.let { it } ?: (requireActivity() as? PlaylistPlayerActivity)
        } catch (_: Exception) { null }

        // We’ll fetch the shared player from PlayerManager via Activity’s ViewModel
        val shared: ExoPlayer = when(requireActivity()){
            is VideoPlayerActivity -> (requireActivity() as VideoPlayerActivity).let { (it as? Any) }
            else -> null
        }?.let { null } ?: com.bytecoder.funplay.player.PlayerManager.get(requireContext()).exoPlayer

        bind.speedSlider.value = shared.playbackParameters.speed
        bind.speedSlider.addOnChangeListener { _, v, _ ->
            shared.setPlaybackSpeed(v)
            scope.launch { PlaybackConfig.saveSpeed(requireContext(), v) }
        }

        // (For brevity) Audio/subtitle selection UI can be wired similarly via TrackSelector & Subtitle configurations

        return AlertDialog.Builder(requireContext())
            .setTitle(com.bytecoder.funplay.R.string.player_more_options)
            .setView(bind.root)
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }
}
