package com.firelord.ytaudio.presentation.Main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    val folderPathLiveData: MutableLiveData<String> = MutableLiveData()
    val downloadLink: MutableLiveData<String> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val thumbnail: MutableLiveData<String> = MutableLiveData()
    val viewsCount: MutableLiveData<String> = MutableLiveData()
    val likesCount: MutableLiveData<String> = MutableLiveData()
    val downloadButton: MutableLiveData<String> = MutableLiveData()
}