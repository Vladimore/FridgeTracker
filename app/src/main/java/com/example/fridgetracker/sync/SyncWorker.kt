package com.example.fridgetracker.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fridgetracker.data.local.ProductDatabase

/** Retryable seam for the real REST sync client. The stub keeps local data intact. */
class SyncWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val pending = ProductDatabase.getInstance(applicationContext).syncOperationDao().pending()
        if (pending.isEmpty()) return Result.success()
        return Result.retry()
    }
}