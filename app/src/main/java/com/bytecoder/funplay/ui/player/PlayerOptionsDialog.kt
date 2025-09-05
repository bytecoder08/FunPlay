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

        // Shared ExoPlayer instance (via PlayerManager)
        val player: ExoPlayer =
            com.bytecoder.funplay.player.PlayerManager.get(requireContext()).exoPlayer
        val selector = player.trackSelector as? DefaultTrackSelector
        val mappedTracks = selector?.currentMappedTrackInfo

        // --- Playback speed ---
        bind.speedSlider.value = player.playbackParameters.speed
        bind.speedSlider.addOnChangeListener { _, value, _ ->
            player.setPlaybackSpeed(value)
            scope.launch { PlaybackConfig.saveSpeed(requireContext(), value) }
        }

        // --- Audio tracks ---
        val audioLanguages = mutableListOf<String>()
        val audioEntries = mutableListOf<Triple<Int, Int, Int>>() // renderer, group, track

        mappedTracks?.let { info ->
            for (rendererIndex in 0 until info.rendererCount) {
                if (info.getRendererType(rendererIndex) == C.TRACK_TYPE_AUDIO) {
                    val groups = info.getTrackGroups(rendererIndex)
                    for (groupIndex in 0 until groups.length) {
                        val group = groups[groupIndex]
                        for (trackIndex in 0 until group.length) {
                            val fmt = group.getFormat(trackIndex)
                            val lang = fmt.language ?: fmt.label ?: "Audio ${audioLanguages.size + 1}"
                            audioLanguages += lang
                            audioEntries += Triple(rendererIndex, groupIndex, trackIndex)
                        }
                    }
                }
            }
        }

        if (audioLanguages.isNotEmpty()) {
            bind.spinnerAudio.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                audioLanguages
            )
            bind.spinnerAudio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val (rendererIndex, groupIndex, trackIndex) = audioEntries[position]
                    val groups = mappedTracks!!.getTrackGroups(rendererIndex)
                    val override = DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex)
                    selector?.setParameters(
                        selector.buildUponParameters()
                            .setSelectionOverride(rendererIndex, groups, override)
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        } else {
            bind.spinnerAudio.visibility = View.GONE
        }

        // --- Subtitle tracks ---
        val subtitleLabels = mutableListOf<String>()
        val subtitleEntries = mutableListOf<Triple<Int, Int, Int>>() // renderer, group, track

        mappedTracks?.let { info ->
            for (rendererIndex in 0 until info.rendererCount) {
                if (info.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                    val groups = info.getTrackGroups(rendererIndex)
                    for (groupIndex in 0 until groups.length) {
                        val group = groups[groupIndex]
                        for (trackIndex in 0 until group.length) {
                            val fmt = group.getFormat(trackIndex)
                            val mime = fmt.sampleMimeType ?: ""
                            val isText = mime.startsWith("text/") || mime == MimeTypes.APPLICATION_SUBRIP
                            if (isText) {
                                val label = fmt.label ?: fmt.language ?: "Subtitle ${subtitleLabels.size + 1}"
                                subtitleLabels += label
                                subtitleEntries += Triple(rendererIndex, groupIndex, trackIndex)
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
            bind.spinnerSubtitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val (rendererIndex, groupIndex, trackIndex) = subtitleEntries[position]
                    val groups = mappedTracks!!.getTrackGroups(rendererIndex)
                    val override = DefaultTrackSelector.SelectionOverride(groupIndex, trackIndex)
                    selector?.setParameters(
                        selector.buildUponParameters()
                            .setSelectionOverride(rendererIndex, groups, override)
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        } else {
            bind.spinnerSubtitle.visibility = View.GONE
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.player_more_options)
            .setView(bind.root)
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }
}
