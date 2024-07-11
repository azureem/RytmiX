//package com.example.rhythmix.presentation.service
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Intent
//import android.media.MediaPlayer
//import android.net.Uri
//import android.os.Build
//import android.os.IBinder
//import android.widget.RemoteViews
//import androidx.core.app.NotificationCompat
//import com.example.rhythmix.R
//import com.example.rhythmix.app.MyApp
//import com.example.rhythmix.data.domain.ControlEnums
//import com.example.rhythmix.data.domain.MusicData
//import com.example.rhythmix.utils.MyAppManager
//import com.example.rhythmix.utils.getMusicDataByPosition
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.launch
//import java.io.File
//
//class MyService : Service() {
//    override fun onBind(p0: Intent?): IBinder? = null
//    private val CHANNEL = "DEMO"
//    private var mediaPlayer: MediaPlayer? = null
//    //private val mediaPlayer get() = _mediaPlayer!!
//    private val scope = CoroutineScope(Dispatchers.IO + Job())
//    private var job: Job? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        mediaPlayer = MediaPlayer()
//        createChannel()
//        startMyService()
//    }
//
//    fun startMyService() {
//        val notification: Notification = NotificationCompat.Builder(this, CHANNEL)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle("Music player")
//            .setCustomContentView(createRemoteView())
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .build()
//        startForeground(1, notification)
//    }
//
//    private fun createChannel() {
//        if (Build.VERSION.SDK_INT >= 26) {
//            val channel =
//                NotificationChannel("DEMO", CHANNEL, NotificationManager.IMPORTANCE_DEFAULT)
//            channel.setSound(null, null)
//            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            service.createNotificationChannel(channel)
//        }
//    }
//    private fun createRemoteView(): RemoteViews {
//        val view = RemoteViews(this.packageName, R.layout.remote_view)
//        val musicData = MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
//        view.setTextViewText(R.id.textMusicName, musicData.title)
//        view.setTextViewText(R.id.textArtistName, musicData.artist)
//        if (mediaPlayer!!.isPlaying) {
//            view.setImageViewResource(R.id.buttonManage, R.drawable.ic_pause)
//        } else view.setImageViewResource(R.id.buttonManage, R.drawable.ic_play)
//        view.setOnClickPendingIntent(R.id.buttonPrev, createPendingIntent(ControlEnums.PREV))
//        view.setOnClickPendingIntent(R.id.buttonManage, createPendingIntent(ControlEnums.MANAGE))
//        view.setOnClickPendingIntent(R.id.buttonNext, createPendingIntent(ControlEnums.NEXT))
//        view.setOnClickPendingIntent(R.id.buttonCancel, createPendingIntent(ControlEnums.CANCEL))
//
//        return view
//    }
//    private fun createPendingIntent(action: ControlEnums): PendingIntent {
//        val intent = Intent(this, MyService::class.java)
//        intent.putExtra("COMMAND", action)
//        return PendingIntent.getService(
//            this,
//            action.position,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startMyService()
//        val command = intent!!.extras?.getSerializable("COMMAND") as ControlEnums
//        doneCommand(command)
//        return START_NOT_STICKY
//    }
//
//    private fun doneCommand(command: ControlEnums) {
//        val data: MusicData =
//            MyAppManager.cursor?.getMusicDataByPosition(MyAppManager.selectMusicPos)!!
//        when (command) {
//            ControlEnums.MANAGE -> {
//                if (mediaPlayer!!.isPlaying) doneCommand(ControlEnums.PAUSE)
//                else doneCommand(ControlEnums.PLAY)
//            }
//
//            ControlEnums.PLAY -> {
//                if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
//                mediaPlayer = MediaPlayer.create(this, Uri.fromFile(File(data.data ?: "")))
//                mediaPlayer!!.start()
//                mediaPlayer!!.setOnCompletionListener { doneCommand(ControlEnums.NEXT) }
//                MyAppManager.fullTime = data.duration!!
//                mediaPlayer!!.seekTo(MyAppManager.currentTime.toInt())
//
//                job?.cancel()
//                job = scope.launch {
//                    changeProgress().collectLatest {
//                        MyAppManager.currentTime = it
//                        MyAppManager.currentTimeLiveData.postValue(it)
//                    }
//                }
//                MyAppManager.isPlayingLiveData.value = true
//                MyAppManager.playMusicLiveData.value = data
//
//
//                startMyService()
//            }
//
//            ControlEnums.PAUSE -> {
//                mediaPlayer!!.stop()
//                job?.let { it.cancel() }
//                MyAppManager.isPlayingLiveData.value = false
//                startMyService()
//            }
//
//            ControlEnums.POS->{
//
//                if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
//                mediaPlayer = MediaPlayer.create(this, Uri.fromFile(File(data.data ?: "")))
//                mediaPlayer!!.stop()
//                mediaPlayer!!.setOnCompletionListener { doneCommand(ControlEnums.NEXT) }
//
//                job?.cancel()
//                job = scope.launch {
//                    changeProgress().collectLatest {
//                        MyAppManager.currentTime = it
//                        MyAppManager.currentTimeLiveData.postValue(it)
//                    }
//                }
//
//                MyAppManager.playMusicLiveData.value = data
//
//
//
//            }
//
//            ControlEnums.NEXT -> {
//                MyAppManager.currentTime = 0
//                if (MyAppManager.selectMusicPos + 1 == MyAppManager.cursor!!.count) MyAppManager.selectMusicPos =
//                    0
//                else MyAppManager.selectMusicPos++
//                doneCommand(ControlEnums.PLAY)
//            }
//
//            ControlEnums.PREV -> {
//                MyAppManager.currentTime = 0
//                if (MyAppManager.selectMusicPos == 0) MyAppManager.selectMusicPos =
//                    MyAppManager.cursor!!.count - 1
//                else MyAppManager.selectMusicPos--
//                doneCommand(ControlEnums.PLAY)
//            }
//
//
//            ControlEnums.SEEK -> {
//                val time =  MyAppManager.duration/ 100
//                mediaPlayer!!.seekTo(MyAppManager.currentTime.toInt())
////
//                job?.cancel()
//                job = scope.launch {
//                    changeProgress().collectLatest {
//
////                        AppManager.currentTime = mediaPlayer?.currentPosition?.toLong()?:0
////                        AppManager.currentTimeLiveData.postValue(mediaPlayer?.currentPosition?.toLong())
//                        MyAppManager.currentTime = it
//                        MyAppManager.currentTimeLiveData.postValue(it)
//                    }
//                }
//            }
//
//            ControlEnums.CANCEL -> {
//                mediaPlayer!!.stop()
//                stopSelf()
//            }
//        }
//    }
//
//    private fun changeProgress(): Flow<Long> = flow {
//        for (i in MyAppManager.currentTime until MyAppManager.fullTime step 1000) {
//            delay(1000)
//            emit(i)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        job?.cancel()
//    }
//}