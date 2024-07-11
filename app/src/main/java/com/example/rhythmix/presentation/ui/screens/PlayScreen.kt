package com.example.rhythmix.presentation.ui.screens

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.rhythmix.R
import com.example.rhythmix.data.domain.ControlEnums
import com.example.rhythmix.data.domain.MusicData
import com.example.rhythmix.databinding.ScreenPlayBinding
import com.example.rhythmix.presentation.service.MyService
import com.example.rhythmix.utils.MyAppManager
import com.example.rhythmix.utils.setChangeProgress

class PlayScreen : Fragment(R.layout.screen_play) {
    private var binding: ScreenPlayBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScreenPlayBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding!!.buttonNext.setOnClickListener { startMyService(ControlEnums.NEXT) }
        binding!!.buttonPrev.setOnClickListener { startMyService(ControlEnums.PREV) }
        binding!!.buttonManage.setOnClickListener { startMyService(ControlEnums.MANAGE) }

        requireActivity().window.navigationBarColor = Color.parseColor("#080819")


        MyAppManager.playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
        MyAppManager.currentTimeLiveData.observe(viewLifecycleOwner, currentTimeObserver)
        binding!!.backk.setOnClickListener {
            findNavController().navigateUp()
        }



        binding!!.seekBar.setChangeProgress { progress, fromUser ->
            if (fromUser) {
                MyAppManager.currentTime = progress.toLong()
//                binding.seekBar.progress = progress
                startMyService(ControlEnums.SEEK)

                val totalMilliseconds: Long = progress.toLong()
                val seconds: Long = totalMilliseconds / 1000 % 60
                val minutes: Long = totalMilliseconds / 1000 / 60
                val formattedTime: String = String.format("%02d:%02d", minutes, seconds)



                binding!!.currentTime.text = formattedTime
            }
        }

    }

    private val playMusicObserver = Observer<MusicData> {
        binding!!.textMusicName.text = it.title
        binding!!.textArtistName.text = it.artist

        binding!!.currentTime.text = MyAppManager.currentTime.toString()
        binding!!.totalTime.text=MyAppManager.duration.toString()
        binding!!.seekBar.max=MyAppManager.duration



        val totalMilliseconds: Long = MyAppManager.fullTime.toLong()
        val totalSeconds: Long = totalMilliseconds / 1000
        val minutes: Long = totalSeconds / 60
        val seconds: Long = totalSeconds % 60
        val formattedTime: String = String.format("%02d:%02d", minutes, seconds)

        binding!!.totalTime.text = formattedTime



    }




    val currentTimeObserver = Observer<Long> {
//        binding.seekBar.progress = (AppManager.currentTime * 100 / AppManager.fullTime).toInt()
        binding!!.seekBar.progress = it.toInt()

        val totalMilliseconds: Long = it
        val seconds: Long = totalMilliseconds / 1000 % 60
        val minutes: Long = totalMilliseconds / 1000 / 60
        val formattedTime: String = String.format("%02d:%02d", minutes, seconds)

        val totalMilliseconds1: Long = MyAppManager.duration.toLong()
        val totalSeconds1: Long = totalMilliseconds1 / 1000
        val minutes1: Long = totalSeconds1 / 60
        val seconds1: Long = totalSeconds1 % 60
        val formattedTime1: String = String.format("%02d:%02d", minutes1, seconds1)

        binding!!.currentTime.text = formattedTime
    }

    private val isPlayingObserver = Observer<Boolean> {
        if (it) binding!!.buttonManage.setImageResource(R.drawable.ic_pause)
        else binding!!.buttonManage.setImageResource(R.drawable.ic_play)
    }
     val currentTimeLiveData = Observer<Long> {
        binding!!.seekBar.progress = (MyAppManager.currentTime * 100 / MyAppManager.fullTime).toInt()
        binding!!.currentTime.text = MyAppManager.currentTime.toString()
    }

     fun startMyService(action: ControlEnums) {
        val intent = Intent(requireActivity(), MyService::class.java)
        intent.putExtra("COMMAND", action)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else {
            requireActivity().startService(intent)
        }
    }
}