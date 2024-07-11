package com.example.rhythmix.presentation.ui.screens

import android.annotation.SuppressLint
import  android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rhythmix.R
import com.example.rhythmix.data.domain.ControlEnums
import com.example.rhythmix.data.domain.MusicData
import com.example.rhythmix.databinding.ScreenMusicListBinding
import com.example.rhythmix.presentation.service.MyService
import com.example.rhythmix.presentation.ui.adapter.MyAdapter
import com.example.rhythmix.utils.MyAppManager

class MainScreen : Fragment(R.layout.screen_music_list) {
    private var binding: ScreenMusicListBinding? = null
    private val adapter = MyAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScreenMusicListBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding!!.rvMusic.adapter = adapter
        binding!!.rvMusic.layoutManager = LinearLayoutManager(requireContext())
        requireActivity().window.navigationBarColor = Color.parseColor("#080819")
        adapter.cursor = MyAppManager.cursor

        binding!!.bottomPart.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreen_to_playScreen)
        }

        binding!!.buttonNextScreen.setOnClickListener { startMyService(ControlEnums.NEXT) }
        binding!!.buttonPrevScreen.setOnClickListener { startMyService(ControlEnums.PREV) }
        binding!!.buttonManageScreen.setOnClickListener { startMyService(ControlEnums.MANAGE) }

        MyAppManager.playMusicLiveData.observe(viewLifecycleOwner, musicPlayScreen)
        MyAppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayMusicObserver)


        if (MyAppManager.selectMusicPos == -1) {
            MyAppManager.selectMusicPos = 0;
            startMyService(ControlEnums.POS)
        }

        adapter.onClickedMusic {
            MyAppManager.selectMusicPos = it
            startMyService(ControlEnums.PLAY)
            adapter.getColor(it)
            adapter.notifyDataSetChanged()
            MyAppManager.isChanged = true;

        }
    }

    private fun startMyService(action: ControlEnums) {
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra("COMMAND", action)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else requireActivity().startService(intent)
    }


    private val musicPlayScreen = Observer<MusicData> {
        binding?.apply {
            textMusicNameScreen.text = it.title
            textArtistNameScreen.text = it.artist
            MyAppManager.duration = it.duration!!.toInt()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val isPlayMusicObserver = Observer<Boolean> {
        if (it) {
            binding!!.buttonManageScreen.setImageResource(R.drawable.ic_pause)
            adapter.lottieAnimationView!!.playAnimation()
            adapter.colorItem = MyAppManager.selectMusicPos
            adapter.getColor(MyAppManager.selectMusicPos)
            adapter.notifyDataSetChanged()
            MyAppManager.isChanged = true;
        } else {
            binding!!.buttonManageScreen.setImageResource(R.drawable.ic_play)
            adapter.getColor(MyAppManager.selectMusicPos)
            adapter.getColorAnimation(MyAppManager.selectMusicPos)
            adapter.notifyDataSetChanged()
            adapter.colorItem = -1;
            adapter.lottieAnimationView!!.pauseAnimation()
            adapter.notifyDataSetChanged()
        }
    }
}