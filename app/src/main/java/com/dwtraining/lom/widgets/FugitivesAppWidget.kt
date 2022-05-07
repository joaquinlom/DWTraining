package com.dwtraining.lom.widgets

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.dwtraining.lom.R
import com.dwtraining.lom.data.COLUMN_NAME_NAME
import com.dwtraining.lom.data.COLUMN_NAME_PHOTO
import com.dwtraining.lom.utils.PictureTools
import java.util.*

class FugitivesAppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    override fun onEnabled(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.SECOND, 1)
        }
        alarmManager.setRepeating(AlarmManager.RTC, calendar.timeInMillis,
            15000, createClockIntent(context))
    }

    override fun onDisabled(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createClockIntent(context))
    }

    @SuppressLint("Range")
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val controls = RemoteViews(context.packageName, R.layout.fugitives_app_widget)
        val cursor = context.contentResolver.query(CONTENT_URI, null, isCaptured.toString(), null, null)
        var name: String? = ""
        var photoPath = ""
        cursor?.let {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(COLUMN_NAME_NAME))
                photoPath = it.getString(it.getColumnIndex(COLUMN_NAME_PHOTO))
            }
            it.close()
        }
        var bitmap: Bitmap? = null
        val status: String
        when {
            isCaptured == 0 -> status = context.getString(R.string.fugitives)
            photoPath.isEmpty() -> status = context.getString(R.string.captured)
            else -> {
                status = context.getString(R.string.captured)
                bitmap = PictureTools.decodeSampledBitmapFromUri(photoPath, 200, 200)
            }
        }
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(context, FugitivesAppWidget::class.java).apply {
                action = CLICK_WIDGET_UPDATE;
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
        } else {
            Intent(CLICK_WIDGET_UPDATE).apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        controls.apply {
            setTextViewText(R.id.nameFugitiveWidget, name)
            setTextViewText(R.id.labelStatusWidget, status)
            if (bitmap == null) {
                setImageViewResource(R.id.pictureFugitiveWidget, R.mipmap.ic_launcher)
            } else {
                setImageViewBitmap(R.id.pictureFugitiveWidget, bitmap)
            }
            setOnClickPendingIntent(R.id.buttonChangeWidget, pendingIntent)
        }
        appWidgetManager.updateAppWidget(appWidgetId, controls)
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        when(intent?.action) {
            CLICK_WIDGET_UPDATE -> {
                isCaptured = if (isCaptured == 0) 1 else 0
                val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
                if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    context?.let { updateAppWidget(it, appWidgetManager, widgetId) }
                }
            }
            CLOCK_WIDGET_UPDATE -> {
                val thisAppWidget = ComponentName(context?.packageName ?: "", javaClass.name)
                val ids = appWidgetManager.getAppWidgetIds(thisAppWidget)
                for (widgetId in ids) {
                    context?.let { updateAppWidget(it, appWidgetManager, widgetId) }
                }
            }
            else -> return
        }
    }
    private fun createClockIntent(context: Context): PendingIntent? {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(context, FugitivesAppWidget::class.java).apply { action = CLOCK_WIDGET_UPDATE }
        } else {
            Intent(CLOCK_WIDGET_UPDATE)
        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private companion object {
        const val uri = "content://com.dwtraining.lom.content.provider"
        const val CLOCK_WIDGET_UPDATE = "com.dwtraining.lom.widgets.action.UPDATE_WIDGET"
        const val CLICK_WIDGET_UPDATE = "com.dwtraining.lom.widgets.action.UPDATE_BY_CLICK_WIDGET"
        val CONTENT_URI: Uri = Uri.parse(uri)
        var isCaptured = 0
    }

}