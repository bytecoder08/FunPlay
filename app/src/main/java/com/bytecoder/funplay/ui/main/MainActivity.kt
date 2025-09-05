package com.bytecoder.funplay.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.bytecoder.funplay.R
import com.bytecoder.funplay.databinding.ActivityMainBinding
import com.bytecoder.funplay.ui.explorer.ExplorerFragment
import com.bytecoder.funplay.ui.playlist.PlaylistFragment
import com.bytecoder.funplay.ui.video.VideoListFragment
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val PERM_REQ = 101

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.topAppBar)

        bind.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_videos -> switch(VideoListFragment(), "Videos")
                R.id.nav_playlists -> switch(PlaylistFragment(), "Playlists")
                R.id.nav_explorer -> switch(ExplorerFragment(), "Explorer")
            }
            true
        }
        bind.bottomNav.selectedItemId = R.id.nav_videos

        ensurePermissions()
    }

    private fun ensurePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_VIDEO), PERM_REQ)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_REQ)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERM_REQ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted → reload video list
                val frag = supportFragmentManager.findFragmentByTag("Videos")
                if (frag is VideoListFragment) {
                    frag.reloadVideos()
                }
            } else {
                Toast.makeText(this, "Permission required to show videos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switch(fragment: androidx.fragment.app.Fragment, tag: String) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment, tag)
        }
    }
}
