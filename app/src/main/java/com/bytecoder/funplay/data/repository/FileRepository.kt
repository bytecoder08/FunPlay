package com.bytecoder.funplay.data.repository

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileRepository(private val context: Context) {
    // Basic SAF listing for chosen tree or doc uri
    suspend fun listChildren(docUri: Uri): List<Pair<Boolean, String>> = withContext(Dispatchers.IO) {
        val children = mutableListOf<Pair<Boolean, String>>() // (isDir, name)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            docUri, DocumentsContract.getDocumentId(docUri)
        )
        context.contentResolver.query(childrenUri, arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE
        ), null, null, null)?.use { c ->
            val idCol = 0; val nameCol = 1; val mimeCol = 2
            while (c.moveToNext()) {
                val mime = c.getString(mimeCol)
                val isDir = DocumentsContract.Document.MIME_TYPE_DIR == mime
                val name = c.getString(nameCol)
                children += isDir to name
            }
        }
        children
    }
}
