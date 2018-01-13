package com.pierfrancescosoffritti.chromecastyoutubesample

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.MediaRouteButton
import android.view.View
import com.google.android.gms.cast.framework.*
import com.pierfrancescosoffritti.chromecastyoutubesample.chromecast.ChromecastContainer
import com.pierfrancescosoffritti.chromecastyoutubesample.chromecast.ChromecastCommunicationChannel
import com.pierfrancescosoffritti.chromecastyoutubesample.chromecast.ChromecastManager
import com.pierfrancescosoffritti.chromecastyoutubesample.youTube.chromecast.ChromecastYouTubePlayer
import com.pierfrancescosoffritti.chromecastyoutubesample.youTube.MyYouTubePlayerListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener
import kotlinx.android.synthetic.main.activity_main.*
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.ContextThemeWrapper


class MainActivity : AppCompatActivity(), ChromecastContainer {

    private lateinit var chromeCastYouTubePlayer : ChromecastYouTubePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        initYouTubePlayers()
        val mediaRouterButton = initChromecast()
    }

    private fun initUI() {
        chromecast_controls.visibility = View.GONE
    }

    override fun onSessionStarting() {
        youtube_player_view.visibility = View.GONE
        chromecast_controls.visibility = View.VISIBLE
    }

    override fun onSessionEnding() {
        youtube_player_view.visibility = View.VISIBLE
        chromecast_controls.visibility = View.GONE
    }

    override fun setCommunicationChannel(communicationChannelChromecast: ChromecastCommunicationChannel) {
        chromeCastYouTubePlayer.initialize(communicationChannelChromecast, YouTubePlayerInitListener { it.addListener(MyYouTubePlayerListener(it)) })
    }

    private fun initYouTubePlayers() {
        initChromecastYouTubePlayer()
        initLocalYouTube()
    }

    private fun initChromecastYouTubePlayer() {
        chromeCastYouTubePlayer = ChromecastYouTubePlayer()
    }

    private fun initLocalYouTube() {
        this.lifecycle.addObserver(youtube_player_view)

        youtube_player_view.initialize({ youTubePlayer ->

            youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                override fun onReady() {
                    val videoId = "6JYIGclVQdw"
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })

        }, true)
    }

    private fun initChromecast() : MediaRouteButton {
        val chromecastManager = ChromecastManager(this, this)
        lifecycle.addObserver(chromecastManager)

        val mediaRouterButton = initMediaRouterButton()

        return mediaRouterButton
    }

    private fun initMediaRouterButton() : MediaRouteButton {
        val mediaRouterButton = MediaRouteButton(this)

        val castContext = ContextThemeWrapper(this, android.support.v7.mediarouter.R.style.Theme_MediaRouter)
        val a = castContext.obtainStyledAttributes(null, android.support.v7.mediarouter.R.styleable.MediaRouteButton, android.support.v7.mediarouter.R.attr.mediaRouteButtonStyle, 0)
        val drawable = a.getDrawable(android.support.v7.mediarouter.R.styleable.MediaRouteButton_externalRouteEnabledDrawable)
        a.recycle()
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white))

        mediaRouterButton.setRemoteIndicatorDrawable(drawable)

        youtube_player_view.playerUIController.addView(mediaRouterButton)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, mediaRouterButton)

        return mediaRouterButton
    }
}
