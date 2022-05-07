package com.dwtraining.lom.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.dwtraining.lom.data.DatabaseBountyHunter

class FugitivesContentProvider : ContentProvider() {
    private val database by lazy { DatabaseBountyHunter(context!!) }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        selection?.let { return database.getLastFugitive(it.toInt()) }
        return null
    }

    override fun onCreate(): Boolean = false
    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun getType(uri: Uri): String? = null
}