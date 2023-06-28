package com.firelord.ytaudio.presentation.Download

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.telephony.PhoneNumberUtils.formatNumber
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.firelord.ytaudio.R
import com.firelord.ytaudio.databinding.FragmentDownloadBinding
import com.firelord.ytaudio.presentation.App
import com.firelord.ytaudio.presentation.Main.MainActivityViewModel
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class DownloadFragment : Fragment() {
    private lateinit var binding: FragmentDownloadBinding
    private val sharedViewModel: MainActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_download, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.viewsCount.observe(viewLifecycleOwner){ viewCount ->
            binding.tvViews.text = formatNumber(viewCount.toLong())
        }

        sharedViewModel.title.observe(viewLifecycleOwner){ title ->
            binding.tvTitle.text = title.toString()
        }

        sharedViewModel.progressVal.observe(viewLifecycleOwner) {progressVal ->
            binding.tvProgressData.text = "$progressVal%"
            binding.progressBar.progress = progressVal.toInt()
        }


        sharedViewModel.progressName.observe(viewLifecycleOwner) {progressName ->
            checkString(progressName)
        }

        sharedViewModel.thumbnail.observe(viewLifecycleOwner){ thumbnail ->
            val cornerRadius = 10
            val requestOptions = RequestOptions()
                .transform(RoundedCorners(cornerRadius))
            Glide.with(this)
                .load(thumbnail)
                .apply(requestOptions)
                .into(binding.ivThumbnail)
        }

        sharedViewModel.likesCount.observe(viewLifecycleOwner){ likesCount ->
            binding.tvLikes.text = formatNumber(likesCount.toLong())
        }

        sharedViewModel.downloadLink.observe(viewLifecycleOwner){ downloadLink ->
            lifecycleScope.launch ( Dispatchers.IO ) {
                getVideo(downloadLink)
            }
        }
    }
    private fun folderPath():String{
        val youtubeBackupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ytaudio-download")
        var youtubeDLDir = ""
        val userFolderPath = sharedViewModel.folderPathLiveData.value.toString()
        if (userFolderPath=="null")
        {
            youtubeDLDir = youtubeBackupDir.absolutePath
        }
        else {
            youtubeDLDir = userFolderPath
        }
        Log.d("path",youtubeDLDir)
        return youtubeDLDir
    }
    private fun getVideo(url: String) {
        Thread {
            Looper.prepare()
            try {
                val request = YoutubeDLRequest(url)
                request.addOption("-o", folderPath() + "/%(title)s.%(ext)s")
                request.addOption("-x")
                request.addOption("--audio-format", "mp3")
                request.addOption("--audio-quality", "0")
                request.addOption("--add-metadata")
                request.addOption("--embed-thumbnail")
                request.addOption("--compat-options", "embed-thumbnail-atomicparsley")
                request.addOption("--force-overwrites")
                YoutubeDL.getInstance().execute(request)
                { progress: Float, v: Long, s: String ->
                    Log.d(App.TAG, s)
                    Log.d(App.TAG, v.toString())
                    Log.d(App.TAG, progress.toString())
                    sharedViewModel.progressVal.postValue(progress)
                    sharedViewModel.progressName.postValue(s)
                }
            } catch (e: Exception) {
                Log.d("DownloadException", e.message.toString())
            }
        }.start()
    }

    private fun formatNumber(num: Long): String {
        val suffixes = mapOf(
            1000000000L to "B",
            1000000L to "M",
            1000L to "K"
        )

        for (suffix in suffixes.keys.sortedDescending()) {
            if (Math.abs(num) >= suffix) {
                val formattedNum = num.toDouble() / suffix
                // Format number with 1 decimal place
                val formattedString = "%.1f".format(formattedNum)
                return "$formattedString${suffixes[suffix]}"
            }
        }

        // If the number is less than 1000, return it as is
        return num.toString()
    }

    private fun checkString(stringToCheck: String) {
        var outPut = ""
        if (stringToCheck.contains("[download]", ignoreCase = true)) {
            outPut = "Downloading..."
            binding.progressBar.indeterminateDrawable.setColorFilter(Color.parseColor("#3690FA"), PorterDuff.Mode.SRC_IN)
            binding.progressBar.progressDrawable.setColorFilter(Color.parseColor("#3690FA"), PorterDuff.Mode.SRC_IN)
        }

        else if (stringToCheck.contains("[ExtractAudio]", ignoreCase = true)) {
            outPut = "Converting..."
            binding.progressBar.indeterminateDrawable.setColorFilter(Color.parseColor("#FFBB0E"), PorterDuff.Mode.SRC_IN)
            binding.progressBar.progressDrawable.setColorFilter(Color.parseColor("#FFBB0E"), PorterDuff.Mode.SRC_IN)
        }

        else if (stringToCheck.contains("[EmbedThumbnail]", ignoreCase = true)) {
            outPut = "Saving..."
            binding.progressBar.indeterminateDrawable.setColorFilter(Color.parseColor("#28C6D0"), PorterDuff.Mode.SRC_IN)
            binding.progressBar.progressDrawable.setColorFilter(Color.parseColor("#28C6D0"), PorterDuff.Mode.SRC_IN)
        }

        else{
            outPut = "Starting..."
        }
        binding.tvDownloading.text = outPut
    }
}