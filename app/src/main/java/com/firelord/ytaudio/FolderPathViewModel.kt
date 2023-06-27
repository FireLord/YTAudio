package com.firelord.ytaudio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FolderPathViewModel : ViewModel() {
    val folderPathLiveData: MutableLiveData<String> = MutableLiveData()
}