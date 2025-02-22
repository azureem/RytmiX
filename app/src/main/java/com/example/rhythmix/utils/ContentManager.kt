package com.example.rhythmix.utils

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.example.rhythmix.data.domain.MusicData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.DURATION
)

fun Context.getMusicsCursor(): Flow<Cursor> = flow {
    val cursor: Cursor = contentResolver
        .query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
            MediaStore.Audio.Media.IS_MUSIC + "!=0",
            null,
            null
        ) ?: return@flow
    emit(cursor)
}

fun Cursor.getMusicDataByPosition(pos: Int): MusicData {
    this.moveToPosition(pos)
    return MusicData(
        this.getInt(0),
        this.getString(1),
        this.getString(2),
        this.getString(3),
        this.getLong(4)
    )
}