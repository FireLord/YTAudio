package com.firelord.ytaudio

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firelord.ytaudio.databinding.ActivityMainBinding
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var folderPathViewModel: FolderPathViewModel
    private val REQUEST_CODE_OPEN_DIRECTORY = 1
    private var folderPathOut: String = ""
    private val TAG = "BaseApplication"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the ViewModel instance
        folderPathViewModel = ViewModelProvider(this).get(FolderPathViewModel::class.java)

        // start download
        binding.button.text = "Download"
        binding.button.setOnClickListener {
            var url = binding.textInputLayout.editText?.text.toString()
            if (url == "") {
                url = "https://youtu.be/ijE2MMtzkHg";
            }
            getVideo(url)
        }

        binding.button2.setOnClickListener {
            openFolderSelection()
        }
    }

    private fun getVideo(url: String) {
        val request = YoutubeDLRequest(url)
        val videoInfo = YoutubeDL.getInstance().getInfo(url)
        val title = videoInfo.title

        folderPathViewModel.folderPathLiveData.observe(this) { folderPath ->
            request.addOption("-o", folderPath + "/%(title)s.%(ext)s")
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
        Toast.makeText(this, "Download Done $title", Toast.LENGTH_LONG).show()
    }

    private fun openFolderSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == RESULT_OK) {
            val treeUri = resultData?.data

            contentResolver.takePersistableUriPermission(
                treeUri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            val folderPath = treeUri.path.toString()
            val externalStorageRoot = Environment.getExternalStorageDirectory().path
            val updatedPath = folderPath.replace("/tree/primary:", "$externalStorageRoot/")

            folderPathViewModel.folderPathLiveData.value = updatedPath
        }
    }
}