package com.example.rhythmix.utils

import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import com.example.rhythmix.data.domain.ControlEnums
import com.example.rhythmix.data.domain.MusicData

object MyAppManager {
    var selectMusicPos: Int = -1
    var cursor: Cursor? = null
    var isChanged = false
    var lastCommand: ControlEnums = ControlEnums.PLAY

    var currentTime : Long = 0L
    var fullTime : Long = 0L

    var duration = 0

    val currentTimeLiveData = MutableLiveData<Long>()

    val playMusicLiveData = MutableLiveData<MusicData>()
    val isPlayingLiveData = MutableLiveData<Boolean>()
}