package com.centsol.widgetlist;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.minHeight;
import static android.R.attr.minWidth;
import static android.R.attr.numColumns;

public class MainActivity extends AppCompatActivity {

    ArrayList<WidgetModel> packageMap = new ArrayList<>();
    ListView listView;
    int cols = 4;
    int rows = 4;
    float cellWidth = 200;
    float cellHeight = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widgets_view);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);
        cellWidth = Utilities.pxFromDp(70,metrics);
        cellHeight = Utilities.pxFromDp(70,metrics);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        List<AppWidgetProviderInfo> infoList = manager.getInstalledProviders();
        final PackageManager pm = getApplicationContext().getPackageManager();
        String lastPackage = "";

        for (AppWidgetProviderInfo info : infoList) {
            WidgetModel widgetModel = new WidgetModel();
            WidgetItem widgetItem = new WidgetItem();

            widgetItem.lable = info.loadLabel(pm);

            try {

                ApplicationInfo ai;
                try {
                    ai = pm.getApplicationInfo(info.provider.getPackageName(), 0);
                } catch (final PackageManager.NameNotFoundException e) {
                    ai = null;
                }
                widgetModel.appName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                widgetModel.icon = pm.getApplicationIcon(info.provider.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Rect widgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(
                    this, info.provider, null);

            int spanX = Math.max(1, (int) Math.ceil(
                    (info.minWidth + widgetPadding.left + widgetPadding.right) / cellWidth));

            int spanY = Math.max(1, (int) Math.ceil(
                    (info.minHeight + widgetPadding.top + widgetPadding.bottom) / cellHeight));

            widgetItem.hSpan = Math.min(spanX, cols);
            widgetItem.vSpan = Math.min(spanY, rows);
            widgetItem.image = info.loadPreviewImage(this, densityDpi);
            widgetItem.minHeigh = info.minHeight;
            widgetItem.minWidth = info.minWidth;
            widgetItem.minResizeHeigh = info.minResizeHeight;
            widgetItem.minResizeWidth = info.minResizeWidth;
            widgetItem.resizeMode = info.resizeMode;


            if (lastPackage.equals(info.provider.getPackageName())) {
                packageMap.get(packageMap.size() - 1).widgetItems.add(widgetItem);
            } else {
                widgetModel.widgetItems.add(widgetItem);
                packageMap.add(widgetModel);
            }

            lastPackage = info.provider.getPackageName();



        }

        listView = (ListView) findViewById(R.id.widgets_list_view);

        ListAdabper mAdapter = new ListAdabper(this, packageMap);
        listView.setAdapter(mAdapter);

    }
}
