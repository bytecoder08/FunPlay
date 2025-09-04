package com.bytecoder.funplay.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.bytecoder.funplay.R
import com.bytecoder.funplay.databinding.ActivityMainBinding
import com.bytecoder.funplay.ui.explorer.ExplorerFragment
import com.bytecoder.funplay.ui.playlist.PlaylistFragment
import com.bytecoder.funplay.ui.video.VideoListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.topAppBar)

        bind.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_videos -> switch(VideoListFragment())
                R.id.nav_playlists -> switch(PlaylistFragment())
                R.id.nav_explorer -> switch(ExplorerFragment())
            }
            true
        }
        bind.bottomNav.selectedItemId = R.id.nav_videos
    }

    private fun switch(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
        }
    }
}
