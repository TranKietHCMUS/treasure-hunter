package com.example.treasurehunter.data.viewModel

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
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

        fun shareScore(context: Context) {
            val facebookInstalled = isFacebookInstalled(context)

            if (!facebookInstalled) {
                Log.d("ShareButton", "Facebook is not installed, showing AlertDialog.")
                showShareDialog(context)
                return
            }

            val shareDialog = ShareDialog(context as androidx.fragment.app.FragmentActivity)
            Log.d("ShareButton", "Share button clicked")

            if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                Log.d("ShareButton", "Share via Facebook!")
                val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://www.facebook.com/profile.php?id=61573386952602"))
                    .setQuote("Tôi vừa đạt được $score điểm trong game Treasure Hunter!")
                    .setShareHashtag(ShareHashtag.Builder().setHashtag("#TreasureHunter").build())
                    .build()

                shareDialog.show(linkContent)

                // Show AlertDialog after delay to ask if sharing was successful
                Handler(Looper.getMainLooper()).postDelayed({
                    showConfirmDialog(context)
                }, 3000)
            } else {
                Log.d("ShareButton", "Cannot share through Facebook, showing AlertDialog.")
                showShareDialog(context)
            }
        }

        fun isFacebookInstalled(context: Context): Boolean {
            val packageManager = context.packageManager
            return try {
                packageManager.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        // Ask the user if Facebook sharing worked
        fun showConfirmDialog(context: Context) {
            AlertDialog.Builder(context)
                .setTitle("Bạn có chia sẻ thành công không?")
                .setMessage("Ứng dụng không thể xác nhận bạn đã chia sẻ thành công hay chưa. Vui lòng xác nhận!")
                .setNegativeButton("Chưa. Tôi muốn chia sẻ qua ứng dụng khác") { _, _ ->
                    shareViaIntent(context)  // Fallback to Intent sharing
                }
                .setPositiveButton("Đã chia sẻ thành công") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // Show the fallback AlertDialog immediately if Facebook isn't available
        fun showShareDialog(context: Context) {
            AlertDialog.Builder(context)
                .setTitle("Không thể chia sẻ qua Facebook")
                .setMessage("Bạn chưa cài đặt ứng dụng Facebook hoặc ứng dụng không thể chia sẻ. Bạn có muốn chia sẻ qua ứng dụng khác không?")
                .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Chia sẻ qua ứng dụng khác") { _, _ ->
                    shareViaIntent(context)
                }
                .show()
        }

        // Intent-based sharing (alternative method)
        fun shareViaIntent(context: Context) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ thành tích!")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Tôi vừa đạt được $score điểm trong game Treasure Hunter! #TreasureHunter\nhttps://www.facebook.com/profile.php?id=61573386952602"
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"))
        }
    }
}