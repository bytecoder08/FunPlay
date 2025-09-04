package com.bytecoder.funplay.ui.player

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bytecoder.funplay.R
import com.bytecoder.funplay.databinding.DialogPlayerOptionsBinding
import com.bytecoder.funplay.player.PlaybackConfig
import com.bytecoder.funplay.player.PlayerManager
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlayerOptionsDialog : DialogFragment() {

    private val scope = MainScope()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bind = DialogPlayerOptionsBinding.inflate(layoutInflater)

        val player: ExoPlayer = PlayerManager.get(requireContext()).exoPlayer
        val selector = player.trackSelector as? DefaultTrackSelector

        // --- Playback speed ---
        bind.speedSlider.value = player.playbackParameters.speed
        bind.speedSlider.addOnChangeListener { _, v, _ ->
            player.setPlaybackSpeed(v)
            scope.launch { PlaybackConfig.saveSpeed(requireContext(), v) }
        }

        val mappedTracks = selector?.currentMappedTrackInfo

        // --- Audio Tracks ---
        val audioLabels = mutableListOf<String>()
        val audioOverrides = mutableListOf<Pair<Int, Int>>() // rendererIndex, trackIndex

        mappedTracks?.let { info ->
            for (rendererIndex in 0 until info.rendererCount) {
                if (info.getRendererType(rendererIndex) == C.TRACK_TYPE_AUDIO) {
                    val trackGroups = info.getTrackGroups(rendererIndex)
                    for (groupIndex in 0 until trackGroups.length) {
                        val group = trackGroups.get(groupIndex)
                        for (trackIndex in 0 until group.length) {
                            val format = group.getFormat(trackIndex)
                            val label = format.language ?: "Audio ${trackIndex + 1}"
                            audioLabels.add(label)
                            audioOverrides.add(rendererIndex to trackIndex)
                        }
                    }
                }
            }
        }

        if (audioLabels.isNotEmpty()) {
            bind.spinnerAudio.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                audioLabels
            )
            bind.spinnerAudio.setSelection(0)

            bind.spinnerAudio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val (renderer, track) = audioOverrides[position]
                    selector?.setParameters(
                        selector.buildUponParameters()
                            .setSelectionOverride(
                                renderer,
                                mappedTracks!!.getTrackGroups(renderer),
                                DefaultTrackSelector.SelectionOverride(0, *intArrayOf(track))
                            )
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // --- Subtitle Tracks ---
        val subtitleLabels = mutableListOf<String>()
        val subtitleOverrides = mutableListOf<Pair<Int, Int>>() // rendererIndex, trackIndex

        mappedTracks?.let { info ->
            for (rendererIndex in 0 until info.rendererCount) {
                if (info.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                    val trackGroups = info.getTrackGroups(rendererIndex)
                    for (groupIndex in 0 until trackGroups.length) {
                        val group = trackGroups.get(groupIndex)
                        for (trackIndex in 0 until group.length) {
                            val format = group.getFormat(trackIndex)
                            if (format.sampleMimeType?.startsWith("text/") == true ||
                                format.sampleMimeType == MimeTypes.APPLICATION_SUBRIP
                            ) {
                                val label = format.label ?: format.language ?: "Subtitle ${trackIndex + 1}"
                                subtitleLabels.add(label)
                                subtitleOverrides.add(rendererIndex to trackIndex)
                            }
                        }
                    }
                }
            }
        }

        if (subtitleLabels.isNotEmpty()) {
            bind.spinnerSubtitle.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                subtitleLabels
            )
            bind.spinnerSubtitle.setSelection(0)

            bind.spinnerSubtitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val (renderer, track) = subtitleOverrides[position]
                    selector?.setParameters(
                        selector.buildUponParameters()
                            .setSelectionOverride(
                                renderer,
                                mappedTracks!!.getTrackGroups(renderer),
                                DefaultTrackSelector.SelectionOverride(0, *intArrayOf(track))
                            )
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.player_more_options)
            .setView(bind.root)
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }
}
