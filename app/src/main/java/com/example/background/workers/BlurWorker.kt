package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI

class BlurWorker(
  context: Context,
  workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
  override fun doWork(): Result {

    val resourceUri = inputData.getString(KEY_IMAGE_URI)

    if (TextUtils.isEmpty(resourceUri)) {
      Log.e("BlurWorker", "Invalid input uri")
      throw IllegalArgumentException("Invalid input uri")
    }

    makeStatusNotification("Blurring Image", applicationContext)


    sleep()

    try {

      val picture = BitmapFactory.decodeStream(applicationContext.contentResolver.openInputStream(
      Uri.parse(resourceUri)))
      val blurredPicture = blurBitmap(picture, applicationContext)
      val uri = writeBitmapToFile(applicationContext, blurredPicture)
      makeStatusNotification(uri.toString(), applicationContext)

      val outputData = workDataOf(KEY_IMAGE_URI to uri.toString())

      return Result.success(outputData)

    } catch (t: Throwable) {
      Log.e("BlurWorker", "Error applying blur")

      return Result.failure()
    }

  }
}