package com.mohang.instadownload.instadownloader

import android.Manifest
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.mohang.instadownload.instadownloader.instadownloader.Constants
import kotlinx.android.synthetic.main.content_detail.*
import retrofit2.Call
import retrofit2.Callback


class DetailActivity : AppCompatActivity() {

    private lateinit var mgr: DownloadManager
     var media: Media?=null
     var shortcode: String?=null
    lateinit var videoView: VideoView

    lateinit var progress:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        btn_download.isEnabled = false

        progress= ProgressDialog.show(this,"Please wait","Pease wait");




        findViewById<View>(R.id.btn_download)
        videoView=findViewById(R.id.postVideo)
        shortcode = intent.getStringExtra(SHORT_CODE)
        if (TextUtils.isEmpty(shortcode)) {
            finish()
            return
        }
        val title = intent.getStringExtra(TITLE);
        if (!TextUtils.isEmpty(title)) {
            postHeading.text = title
        }

        val display = intent.getStringExtra(DISPLAY_URL);
        if (!TextUtils.isEmpty(display)) {
            Glide.with(this).load(display).into(postImage)
        }



        mgr = getSystemService(DOWNLOAD_SERVICE) as DownloadManager


        btn_download.setOnClickListener {


            if (askPermission()) {

                try {
                    startDownload()
                }catch (e:Exception){
                    Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@DetailActivity, "Please provide external write permission in settings", Toast.LENGTH_SHORT).show()
            }


        }

        Service.getService().detailPost.detail(Constants.INSTA_DETAIL.replace("{shortcode}", shortcode?:"")).enqueue(object : Callback<Media> {
            override fun onResponse(call: Call<Media>, response: retrofit2.Response<Media>) {


               progress.dismiss()
                media = response?.body()
                if (response.isSuccessful) {
                    // if(cricData.)
                    btn_download.isEnabled = true
                    Glide.with(this@DetailActivity).load(media?.grapgql?.getShortcode_media()?.display_url).into(postImage)
                    var video:Boolean=media?.grapgql?.shortcode_media?.isIs_video!!


                    if(video){
                      //  playVideo(media?.grapgql?.shortcode_media?.video_url)
                    }


                }

            }


            override fun onFailure(call: Call<Media>, t: Throwable) {
                progress.dismiss()

                Toast.makeText(this@DetailActivity, "failed to read post", Toast.LENGTH_SHORT).show()
            }
        })


    }

    fun playVideo(url:String?) {
        try {
            postImage.visibility=View.INVISIBLE
            videoView.visibility=View.VISIBLE
            var uri = Uri.parse(url);
            // VideoView simpleVideoView = (VideoView) findViewById(R.id.simpleVideoView); // initiate a video view
            videoView.setVideoURI(uri);
            videoView.start();
        }catch (e:Exception){
            Toast.makeText(this,"failed to play video",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        startDownload()
                    }catch (e:Exception){
                        Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("permission denied"," here ");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun startDownload() {

        var shortCodeMedia = media?.grapgql?.shortcode_media

        var url = if (shortCodeMedia?.isIs_video!!) {
            shortCodeMedia?.video_url
        } else {
            shortCodeMedia?.display_url
        }
        val uri = Uri.parse(url);

        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs()

        var lastDownload = mgr.enqueue(DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(""+shortcode)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        shortcode + ".mp4"))


    }

    companion object {

        const val SHORT_CODE = "shortcode"
        const val DISPLAY_URL = "display_url";
        const val TITLE = "title"
    }

    var oncomplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

          //  Util.createNotification("Ha ha ","complete",this@DetailActivity)
            Toast.makeText(this@DetailActivity, "complete", Toast.LENGTH_SHORT).show()
        }
    }
    var onNotificationClick = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(this@DetailActivity, "notification click", Toast.LENGTH_SHORT).show()

        }
    }

    fun askPermission(): Boolean {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Log.d("came  --------------", "here   ----------")
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        100)
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        100)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false
        }
        return true
    }







}
