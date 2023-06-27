package com.firelord.ytaudio.presentation.Home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.firelord.ytaudio.R
import com.firelord.ytaudio.databinding.FragmentHomeBinding
import com.firelord.ytaudio.presentation.App.Companion.TAG
import com.firelord.ytaudio.presentation.FolderPathViewModel
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: FolderPathViewModel by activityViewModels()
    private val REQUEST_CODE_OPEN_DIRECTORY = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // start download
        binding.btDownload.setOnClickListener {
            var url = binding.tfLink.editText?.text.toString()
            if (url == "") {
                url = "https://youtu.be/ijE2MMtzkHg";
            }
            getVideo(url)
        }
        binding.tfPath.setStartIconOnClickListener {
            openFolderSelection()

            sharedViewModel.folderPathLiveData.observe(viewLifecycleOwner) { folderPath ->
                binding.tfPath.editText?.setText(folderPath)
            }
        }

        return binding.root
    }

    private fun getVideo(url: String) {
        val request = YoutubeDLRequest(url)
        val videoInfo = YoutubeDL.getInstance().getInfo(url)
        val title = videoInfo.title

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
        { progress: Float, _: Long, s: String ->
            Log.d(TAG, s)
            Log.d(TAG, progress.toString())
        }
        Toast.makeText(activity, "Download Done $title", Toast.LENGTH_LONG).show()
    }

    private fun openFolderSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == RESULT_OK) {
            val treeUri = resultData?.data

            activity?.contentResolver?.takePersistableUriPermission(
                treeUri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            val folderPath = treeUri?.path.toString()
            val externalStorageRoot = Environment.getExternalStorageDirectory().path
            val updatedPath = folderPath.replace("/tree/primary:", "$externalStorageRoot/")

            sharedViewModel.folderPathLiveData.value = updatedPath
        }
    }
}