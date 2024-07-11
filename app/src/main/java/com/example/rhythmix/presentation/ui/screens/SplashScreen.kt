package com.example.rhythmix.presentation.ui.screens

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rhythmix.R
import com.example.rhythmix.utils.MyAppManager
import com.example.rhythmix.utils.checkPermissions
import com.example.rhythmix.utils.getMusicsCursor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("CustomSplashScreen")
class SplashScreen : Fragment(R.layout.screen_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().window.navigationBarColor = Color.parseColor("#080819")
        object : CountDownTimer(1500, 900) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                if (Build.VERSION.SDK_INT>32){
                    requireActivity().checkPermissions(arrayOf(Manifest.permission.READ_MEDIA_AUDIO,POST_NOTIFICATIONS)) {
                        requireContext().getMusicsCursor().onEach {
                            MyAppManager.cursor = it
                            findNavController().navigate(R.id.action_splashScreen_to_mainScreen)
                        }.launchIn(lifecycleScope)
                    }
                }else{
                    requireActivity().checkPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        requireContext().getMusicsCursor().onEach {
                            MyAppManager.cursor = it

                            findNavController().navigate(R.id.action_splashScreen_to_mainScreen)
                        }.launchIn(lifecycleScope)
                    }
                }
            }

        }.start()

    }

}