package com.firelord.ytaudio.presentation.Success

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.firelord.ytaudio.R
import com.firelord.ytaudio.databinding.FragmentSuccessBinding
import com.firelord.ytaudio.presentation.Main.MainActivityViewModel

class SuccessFragment : Fragment() {
    private lateinit var binding: FragmentSuccessBinding
    private val sharedViewModel: MainActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.viewsCount.observe(viewLifecycleOwner){ viewCount ->
            binding.tvViews.text = formatNumber(viewCount.toLong())
        }

        sharedViewModel.likesCount.observe(viewLifecycleOwner){ likesCount ->
            binding.tvLikes.text = formatNumber(likesCount.toLong())
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

        binding.progressBar3.indeterminateDrawable.setColorFilter(Color.parseColor("#72BD6C"), PorterDuff.Mode.SRC_IN)
        binding.progressBar3.progressDrawable.setColorFilter(Color.parseColor("#72BD6C"), PorterDuff.Mode.SRC_IN)

        binding.btDownloadAgain.setOnClickListener {
            sharedViewModel.cleanup()
            binding.root.findNavController().navigate(R.id.action_successFragment_to_homeFragment)
        }
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
}