package com.firelord.ytaudio.presentation

import android.app.Application
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // YT instance
        try {
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
        } catch (e: YoutubeDLException) {
            Log.e(TAG, "failed to initialize youtubedl-android", e)
        }
        Thread { YoutubeDL.getInstance().updateYoutubeDL(this) }.start()
    }

    companion object {
        const val TAG = "BaseApplication"
    }
}