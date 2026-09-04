package com.taskmanager.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import com.taskmanager.presentation.MainActivity

/**
 * Home-screen widget showing today's tasks + a quick-add entry point
 * (issue 32: had to open the app to see tasks). Uses Glance (Compose).
 *
 * NOTE: wiring real today-tasks data requires a WorkManager-backed state
 * updater + GlanceAppWidgetManager.updateAll; this ships the shell that
 * launches the app (where Quick Add lives).
 */
class TodayTasksWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12)
            ) {
                Text(
                    text = "Today's tasks",
                    
                )
                Text(
                    text = "Tap to add a task",
                    modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
                )
            }
        }
    }
}

class TodayTasksWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TodayTasksWidget()
}
