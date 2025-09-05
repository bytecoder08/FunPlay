package com.bytecoder.funplay.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile

object PermissionUtils {

    // --- SAF (Storage Access Framework) helpers ---

    /**
     * Launches SAF to pick a folder.
     */
    fun createOpenDirectoryIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/storage"))
        }
    }

    /**
     * Launches SAF to pick a single video file.
     */
    fun createOpenVideoIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
        }
    }

    /**
     * Launches SAF to pick a subtitle file (SRT/VTT).
     */
    fun createOpenSubtitleIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
    }

    /**
     * Grant persistent read permission for a URI (folder or file).
     */
    fun takePersistablePermission(context: Context, uri: Uri) {
        val takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        try {
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Checks whether we still have persisted permission for a URI.
     */
    fun hasPersistedPermission(context: Context, uri: Uri): Boolean {
        val perms = context.contentResolver.persistedUriPermissions
        return perms.any { it.uri == uri && it.isReadPermission }
    }

    /**
     * Convert a SAF URI into a DocumentFile (so you can browse contents).
     */
    fun asDocumentFile(context: Context, uri: Uri): DocumentFile? {
        return DocumentFile.fromTreeUri(context, uri)
    }

    // --- Legacy support for Android < 10 ---

    fun requiresLegacyStorage(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    }
}
