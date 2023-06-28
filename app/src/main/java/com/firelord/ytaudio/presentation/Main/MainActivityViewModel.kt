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
    var folderPathLiveData: MutableLiveData<String> = MutableLiveData()
    var downloadLink: MutableLiveData<String> = MutableLiveData()
    var title: MutableLiveData<String> = MutableLiveData()
    var thumbnail: MutableLiveData<String> = MutableLiveData()
    var viewsCount: MutableLiveData<String> = MutableLiveData()
    var likesCount: MutableLiveData<String> = MutableLiveData()
    var videoInfoLiveData: MutableLiveData<VideoInfo> = MutableLiveData()
    var videoInfoException: MutableLiveData<Exception> = MutableLiveData()
    var progressVal: MutableLiveData<Float> = MutableLiveData()
    var progressName: MutableLiveData<String> = MutableLiveData()
    var resultPassFail: MutableLiveData<Int> = MutableLiveData()
    var failReason: MutableLiveData<String> = MutableLiveData()


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

    fun cleanup() {
//        folderPathLiveData = MutableLiveData()
        downloadLink = MutableLiveData()
        title = MutableLiveData()
        thumbnail = MutableLiveData()
        viewsCount = MutableLiveData()
        likesCount = MutableLiveData()
        videoInfoLiveData = MutableLiveData()
        videoInfoException = MutableLiveData()
        progressVal = MutableLiveData()
        progressName = MutableLiveData()
        resultPassFail = MutableLiveData()
        failReason = MutableLiveData()
    }
}