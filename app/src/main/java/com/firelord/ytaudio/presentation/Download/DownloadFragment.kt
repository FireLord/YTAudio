package com.firelord.ytaudio.presentation.Download

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.firelord.ytaudio.R
import com.firelord.ytaudio.databinding.FragmentDownloadBinding
import com.firelord.ytaudio.presentation.App
import com.firelord.ytaudio.presentation.Main.MainActivityViewModel
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

class DownloadFragment : Fragment() {
    private lateinit var binding: FragmentDownloadBinding
    private val sharedViewModel: MainActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_download, container, false)

        sharedViewModel.viewsCount.observe(viewLifecycleOwner){ viewCount ->
            binding.tvViews.text = viewCount.toString()
        }

        sharedViewModel.title.observe(viewLifecycleOwner){ title ->
            binding.tvTitle.text = title.toString()
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
           binding.tvLikes.text = likesCount.toString()
        }

        sharedViewModel.downloadLink.observe(viewLifecycleOwner){ downloadLink ->
            getVideo(downloadLink)
        }

        return binding.root
    }


    private fun getVideo(url: String) {
        val request = YoutubeDLRequest(url)

        sharedViewModel.folderPathLiveData.observe(viewLifecycleOwner) { folderPath ->
            if (folderPath.isNotEmpty()){
                request.addOption("-o", folderPath + "/%(title)s.%(ext)s")
            }
            else {
                //TODO fix it
                val youtubeDLDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "ytaudio-download"
                )
                request.addOption("-o", youtubeDLDir.absolutePath + "/%(title)s.%(ext)s")
            }
        }
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
        }
    }
}