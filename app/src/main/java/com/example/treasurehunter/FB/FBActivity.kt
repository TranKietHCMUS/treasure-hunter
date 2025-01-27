package com.example.treasurehunter.FB

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.treasurehunter.R
import com.facebook.share.model.ShareHashtag
import com.facebook.share.widget.ShareDialog

class FBActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_b)

        val hastag = ShareHashtag.Builder()
            .setHashtag("#TreasureHunter")
            .build()

        val shareContent = com.facebook.share.model.ShareLinkContent.Builder()
            .setQuote("text")
            .setShareHashtag(hastag)
            .setContentUrl(Uri.parse("facebook.com"))
            .build()

        Log.i("FACEBOOK", "Share to Facebook")

        ShareDialog.show(this, shareContent)

        Log.i("FACEBOOK", "Shared successfully")
    }
}