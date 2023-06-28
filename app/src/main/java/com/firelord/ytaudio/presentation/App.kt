package com.firelord.ytaudio.presentation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import java.lang.Exception


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
        if (isNetworkConnected()){
            Thread {
                try {
                    YoutubeDL.getInstance().updateYoutubeDL(this)
                }
                catch (e: Exception){
                    Log.d(TAG,"failed to update youtubeDL")
                }
            }.start()
        }
        else{
            Toast.makeText(this,"PLease turn on net",Toast.LENGTH_LONG).show()
        }
    }
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    companion object {
        const val TAG = "BaseApplication"
    }
}