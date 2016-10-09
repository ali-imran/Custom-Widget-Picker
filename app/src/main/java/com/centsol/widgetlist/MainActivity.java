package com.centsol.widgetlist;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

public class MainActivity extends AppCompatActivity {

    ArrayList<WidgetModel> packageMap = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widgets_view);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);
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


            widgetItem.image = info.loadPreviewImage(this, densityDpi);
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
