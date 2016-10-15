package com.centsol.widgetlist;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WidgetSelectListner {
    static final int APPWIDGET_HOST_ID = 2085;
    private static final int REQUEST_CREATE_APPWIDGET = 5;

    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_BIND_APPWIDGET = 11;
    ArrayList<WidgetModel> packageMap = new ArrayList<>();
    AppWidgetManager mAppWidgetManager;
    ListView listView;
    int cols = 4;
    int rows = 4;
    float cellWidth = 200;
    float cellHeight = 200;
    AppWidgetHostView hostView;
    int appWidgetId;
    AppWidgetProviderInfo appWidgetInfo;
    private AppWidgetHostView widgets[] = new AppWidgetHostView[16];
    private AppWidgetHost mAppWidgetHost;
    private int widgCount = 0;
    private LinearLayout widgetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widgets_view);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);
        cellWidth = Utilities.pxFromDp(70, metrics);
        cellHeight = Utilities.pxFromDp(70, metrics);
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, APPWIDGET_HOST_ID);
        widgetView = (LinearLayout) findViewById(R.id.widget_container);


        List<AppWidgetProviderInfo> infoList = mAppWidgetManager.getInstalledProviders();

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
            widgetItem.info = info;

            if (lastPackage.equals(info.provider.getPackageName())) {
                packageMap.get(packageMap.size() - 1).widgetItems.add(widgetItem);
            } else {
                widgetModel.widgetItems.add(widgetItem);
                packageMap.add(widgetModel);
            }

            lastPackage = info.provider.getPackageName();


        }

        listView = (ListView) findViewById(R.id.widgets_list_view);

        ListAdabper mAdapter = new ListAdabper(this, packageMap, this);
        listView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    public void onWidgetSelect(int pos, int subIndex) {
        appWidgetId = mAppWidgetHost.allocateAppWidgetId();

        appWidgetInfo = packageMap.get(pos).widgetItems.get(subIndex).info;

        boolean success = mAppWidgetManager.bindAppWidgetIdIfAllowed(
                appWidgetId, appWidgetInfo.provider);
        if (success) {
            configureWidget(appWidgetInfo, appWidgetId, widgetView);
        } else {

            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, appWidgetInfo.provider);
         /*   mAppWidgetManager.a(appWidgetInfo.provider)
                    .addToIntent(intent, AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE);*/
            // TODO: we need to make sure that this accounts for the options bundle.
            // intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, options);
            startActivityForResult(intent, REQUEST_BIND_APPWIDGET);
        }
    }


    @Override

    public void onDestroy() {

        super.onDestroy();

        try {

            mAppWidgetHost.stopListening();


        } catch (NullPointerException ex) {

            Log.w("lockscreen destroy", "problem while stopping AppWidgetHost during Lockscreen destruction", ex);

        }

    }


    private void configureWidget(AppWidgetProviderInfo appWidgetInfo, int appWidgetId, View v1) {


        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);


        } else {

            createWidget(appWidgetInfo, appWidgetId, v1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_CREATE_APPWIDGET && resultCode == RESULT_OK) {

                createWidget(appWidgetInfo, appWidgetId, widgetView);


            } else if ((requestCode == REQUEST_CREATE_APPWIDGET || requestCode == REQUEST_PICK_APPWIDGET) && resultCode == RESULT_CANCELED && data != null) {
                int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                if (appWidgetId != -1) {
                    mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                }
            } else if (requestCode == REQUEST_BIND_APPWIDGET) {
                // This is called only if the user did not previously have permissions to bind widgets

                if (resultCode == RESULT_OK) {
                    boolean success =   mAppWidgetManager.bindAppWidgetIdIfAllowed(
                            appWidgetId, appWidgetInfo.provider);
                    if(success) {
                        createWidget(appWidgetInfo, appWidgetId, widgetView);
                    }else{
                        Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }

                    // When the user has granted permission to bind widgets, we should check to see if
                    // we can inflate the default search bar widget.

                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createWidget(AppWidgetProviderInfo appWidgetInfo, int appWidgetId, View layout1) {


        hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);

       // hostView.setAppWidget(appWidgetId, appWidgetInfo);
        hostView.setLayoutParams(new LinearLayout.LayoutParams(widgetView.getWidth(), widgetView.getHeight()));
        widgetView = (LinearLayout) layout1;
        widgetView.removeAllViews();
        widgetView.addView(hostView);
        widgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
