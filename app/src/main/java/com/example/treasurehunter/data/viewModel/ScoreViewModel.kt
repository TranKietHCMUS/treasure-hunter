package com.example.treasurehunter.data.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.example.treasurehunter.R
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.ShareMediaContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ScoreViewModel @Inject constructor() : ViewModel() {
    companion object {
        var score = 0;

        fun increaseScore() {
            score += 1;
            if (PuzzleViewModel.isSolved) {
                score += 1;
            }
        }

//        fun shareScore(context: Context) {
//            val shareContent = ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("https://treasure_hunter.com/"))
//                .setQuote("Tôi vừa đạt được $score điểm trong game!")
//                .build()
//
//            ShareDialog(context as Activity).also { shareDialog ->
//                shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC)
//            }
//        }

//        fun shareScore(context: Context) {
//            try {
//                val textToShare = "Tôi vừa đạt được $score điểm trong game Treasure Hunter!"
//
//                // Lấy bitmap từ drawable
//                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logotitle)
//
//                // Lưu ảnh vào cache
//                val file = File(context.cacheDir, "shared_image.png")
//                FileOutputStream(file).use { out ->
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//                }
//
//                // Lấy URI của ảnh
//                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
//
//                // Intent chia sẻ
//                val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                    type = "image/*"
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    putExtra(Intent.EXTRA_TEXT, textToShare)
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                }
//
//                // Mở share dialog
//                context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"))
//            } catch (e: Exception) {
//                // Xử lý lỗi nếu cần
//                e.printStackTrace()
//            }
//        }

        fun shareScore(context: Context) {
            val shareDialog = ShareDialog(context as androidx.fragment.app.FragmentActivity)

            if (ShareDialog.canShow(SharePhotoContent::class.java)) {
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.app_avatar)

                val photo = SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build()

                val photoContent = SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build()

                val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://creations.mtdv.me/cvAm2KNYWr"))
                    .setQuote("Tôi vừa đạt được $score điểm trong game Treasure Hunter!")
                    .setShareHashtag(ShareHashtag.Builder().setHashtag("#TreasureHunter").build())
                    .build()

                shareDialog.show(photoContent)
            }
        }


    }
}