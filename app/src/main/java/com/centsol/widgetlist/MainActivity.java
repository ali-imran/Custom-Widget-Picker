package com.centsol.widgetlist;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
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
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.minHeight;
import static android.R.attr.minWidth;
import static android.R.attr.numColumns;

public class MainActivity extends AppCompatActivity implements WidgetSelectListner {
    static final int APPWIDGET_HOST_ID = 2037;
    private static final int REQUEST_CREATE_APPWIDGET = 5;

    private static final int REQUEST_PICK_APPWIDGET = 9;
    ArrayList<WidgetModel> packageMap = new ArrayList<>();
    AppWidgetManager mAppWidgetManager;
    ListView listView;
    int cols = 4;
    int rows = 4;
    float cellWidth = 200;
    float cellHeight = 200;
    private AppWidgetHostView widgets[] = new AppWidgetHostView[16];
    private AppWidgetHost mAppWidgetHost;
    private int widgCount = 0;

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
        mAppWidgetHost.startListening();



        List<AppWidgetProviderInfo> infoList = mAppWidgetManager.getInstalledProviders();
        final PackageManager pm = getApplicationContext().getPackageManager();
        String lastPackage = "";
        int index = 0;
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
    public void onWidgetSelect(int pos, int subIndex) {
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        completeAddAppWidget(packageMap.get(pos).widgetItems.get(subIndex).info, appWidgetId);
    }



    private void completeAddAppWidget(AppWidgetProviderInfo appWidgetInfo, int appWidgetId) {

   





    /* Launcher would calculate the grid spans needed to fit this widget

     * It would also do a check operation to abort if the cell user picked wasn't acceptable

     * given the size of the widget they chose

     */


        //What we'll do is log the info about the widget for help in letting user reposition it


        int width = appWidgetInfo.minWidth;

        int height = appWidgetInfo.minHeight;



/*

next we need to make a record of where we are adding this widget

what the launcher is doing is spawning a helper object where it saves details about the widget

it saves the number of cells wide and tall the widget is

it adds the spawned object to the array list for widgetinfos

the array list is a member of LauncherInfo helper object

the model seems to retain the references to everything that's been placed on the Launcher

*/

        //we can get a reference to our main view here, and then add a relative layout to it.

        //I can probably directly reference the relative layout I want and then add widgets filling in from the top

        //just need to figure out how to determine if the widget being selected is too long to fit on existing row

        //to decide whether to place it on right of last widget or on the bottom

        RelativeLayout parent = (RelativeLayout) findViewById(R.id.widget_container);

        parent.removeAllViews();

        //Log.v("getting parent ref","the ID of the parent is " + parent.getId());


        //AppWidgetHostView newWidget = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);


        //we need to store this widget in an array. the views can be recreated but we need to have a persistent ref

        //FIXME currently we aren't persistent, need to learn how to make activity save persistent state


        widgets[widgCount] = attachWidget(mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo), width, height);


        parent.addView(widgets[widgCount]);


        Log.v("widget was added", "the ID of the widget view is " + widgets[widgCount].getId());


        widgCount++;


        //launcher is doing something to pass this view to their the workspace or the celllayout


        //so every single widget that gets created is one instance of the AppWidgetHostView.

        //the viewgroup we would have to maintain holds all the appwidgethostviews/

    }
    //the created new widget is passed raw with the data about its size, then we figure out how to position it

    private AppWidgetHostView attachWidget(AppWidgetHostView widget, int w, int h) {


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams

                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (widgCount == 0) params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            //first widget goes at the top of the relative view widget area

        else params.addRule(RelativeLayout.RIGHT_OF, widgets[widgCount - 1].getId());


        widget.setLayoutParams(params);


        widget.setId(100 + widgCount);

        return widget;

    }





    @Override

    public void onDestroy() {

        //mDestroyed = true;


        super.onDestroy();


        try {

            mAppWidgetHost.stopListening();

        } catch (NullPointerException ex) {

            Log.w("lockscreen destroy", "problem while stopping AppWidgetHost during Lockscreen destruction", ex);

        }

    }
}
