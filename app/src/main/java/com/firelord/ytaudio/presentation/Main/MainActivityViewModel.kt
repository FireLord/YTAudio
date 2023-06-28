package com.firelord.ytaudio.presentation.Main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivityViewModel : ViewModel() {
    val folderPathLiveData: MutableLiveData<String> = MutableLiveData()
    val downloadLink: MutableLiveData<String> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val thumbnail: MutableLiveData<String> = MutableLiveData()
    val viewsCount: MutableLiveData<String> = MutableLiveData()
    val likesCount: MutableLiveData<String> = MutableLiveData()
    val downloadButton: MutableLiveData<String> = MutableLiveData()
    val videoInfoLiveData: MutableLiveData<VideoInfo> = MutableLiveData()
    val videoInfoException: MutableLiveData<Exception> = MutableLiveData()

    fun getVideoInfo(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = YoutubeDL.getInstance().getInfo(url)
                videoInfoLiveData.postValue(result)
            } catch (e: Exception) {
                videoInfoException.postValue(e)
            }
        }
    }
}