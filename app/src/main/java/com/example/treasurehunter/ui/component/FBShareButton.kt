package com.example.treasurehunter.ui.component

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.treasurehunter.FB.FBActivity
import com.facebook.share.model.ShareHashtag
import com.facebook.share.widget.ShareDialog

@Composable
fun FBShareButton() {
    val context = LocalContext.current
    Button(onClick = {
        shareToFacebook(
            context = context,
            text = "Đây là nội dung bài đăng của bạn!",
            imageUri = Uri.parse("facebook.com")
        )

        // back up thi comment cai tren lai
//        val intent = Intent(context, FBActivity::class.java)
//        context.startActivity(intent)
    }) {
        Text("Share on Facebook")
    }
}

fun shareToFacebook(context: Context, text: String, imageUri: Uri?) {
    val hastag = ShareHashtag.Builder()
        .setHashtag("#TreasureHunter")
        .build()

    val shareContent = com.facebook.share.model.ShareLinkContent.Builder()
        .setQuote(text)
        .setShareHashtag(hastag)
        .setContentUrl(imageUri)
        .build()

    Log.i("FACEBOOK", "Share to Facebook")

    ShareDialog.show(context as Activity, shareContent)

    Log.i("FACEBOOK", "Shared successfully")
}