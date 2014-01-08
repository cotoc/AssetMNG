/*
 * Copyright © 2012 Infotec Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jp.iftc.androidasset.widget;


import jp.iftc.androidasset.R;
import jp.iftc.androidasset.db.AssetInfoDAO;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * @author 0A7044
 *
 */
public class AssetWidget extends AppWidgetProvider{
    @SuppressWarnings("unused")
    private static final String TAG = AssetWidget.class.getSimpleName();
    private final AssetWidget self = this;

    private AssetInfoDAO mDao;

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        String[] columns = {"Check_Result"};
        String[] narrows = {"0"};
        int counters = 0;

        mDao = new AssetInfoDAO(context);

        counters = mDao.getNarrowListCount(columns, narrows);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        rv.setTextViewText(R.id.widget_text, "未チェック：" + String.valueOf(counters) + "件" );
        appWidgetManager.updateAppWidget(appWidgetIds, rv);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        super.onReceive(context, intent);
    }
}
