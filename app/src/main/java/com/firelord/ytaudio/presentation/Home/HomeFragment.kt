package com.firelord.ytaudio.presentation.Home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.firelord.ytaudio.R
import com.firelord.ytaudio.databinding.FragmentHomeBinding
import com.firelord.ytaudio.presentation.App
import com.firelord.ytaudio.presentation.App.Companion.TAG
import com.firelord.ytaudio.presentation.Main.MainActivityViewModel
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import kotlin.math.log

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: MainActivityViewModel by activityViewModels()
    private val REQUEST_CODE_OPEN_DIRECTORY = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btDownload.text = "Download"

        // start download
        sharedViewModel.videoInfoLiveData.observe(viewLifecycleOwner) {
            handleFetchVideoInfoSuccess(it)
        }

        sharedViewModel.videoInfoException.observe(viewLifecycleOwner) {
            handleFetchVideoInfoFailure(it)
        }

        binding.btDownload.setOnClickListener { buttonIt ->
            var url = binding.tfLink.editText?.text.toString()
            if (url == "") {
                url = "https://youtu.be/ijE2MMtzkHg";
            }

            getVideoInfo(url)
        }
        binding.tfPath.setStartIconOnClickListener {
            openFolderSelection()
            sharedViewModel.folderPathLiveData.observe(viewLifecycleOwner) { folderPath ->
                binding.tfPath.editText?.setText(folderPath)
            }
        }
    }
    private fun handleFetchVideoInfoSuccess(videoInfo: VideoInfo?) {
        binding.progressBar2.visibility = View.GONE
        videoInfo?.let {
            sharedViewModel.title.value = videoInfo.title
            sharedViewModel.thumbnail.value = videoInfo.thumbnail
            sharedViewModel.viewsCount.value = videoInfo.viewCount
            sharedViewModel.likesCount.value = videoInfo.likeCount

            proceedToDownloadScreen(it.url)
        }
    }
    private fun getVideoInfo(url: String){
        binding.btDownload.text = "Grabbing info"
        binding.progressBar2.visibility = View.VISIBLE
        sharedViewModel.getVideoInfo(url)
    }
    private fun handleFetchVideoInfoFailure(exception: Exception?) {
        binding.btDownload.text = "Download"
        binding.progressBar2.visibility = View.GONE
        Log.d("Error",exception?.message.toString())
    }

    private fun proceedToDownloadScreen(url: String?) {
        binding.root.findNavController().navigate(R.id.action_homeFragment_to_downloadFragment)
        sharedViewModel.downloadLink.value = url ?: binding.tfLink.editText?.text.toString()
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