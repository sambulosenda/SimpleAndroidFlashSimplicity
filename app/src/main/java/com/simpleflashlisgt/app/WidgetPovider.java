package com.simpleflashlisgt.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by sambulosenda on 30/12/13.
 */
public class WidgetPovider  extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    Intent intent = new Intent(context, MainActivity.class );
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

    RemoteViews views = new RemoteViews(context.getPackageName().R.layout.widget_layout);
    views.setOnClickPendingIntent(R.id.widgetView, pendingIntent);
    appWidgetManager.updateAppWidget(appWidgetIds, views);
}
