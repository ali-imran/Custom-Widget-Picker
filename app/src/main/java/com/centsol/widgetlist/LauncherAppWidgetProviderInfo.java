package com.centsol.widgetlist;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;


public class LauncherAppWidgetProviderInfo extends AppWidgetProviderInfo {

    public boolean isCustomWidget = false;



    public static LauncherAppWidgetProviderInfo fromProviderInfo(Context context,
            AppWidgetProviderInfo info) {

        // In lieu of a public super copy constructor, we first write the AppWidgetProviderInfo
        // into a parcel, and then construct a new LauncherAppWidgetProvider info from the
        // associated super parcel constructor. This allows us to copy non-public members without
        // using reflection.
        Parcel p = Parcel.obtain();
        info.writeToParcel(p, 0);
        p.setDataPosition(0);
        LauncherAppWidgetProviderInfo lawpi = new LauncherAppWidgetProviderInfo(p);
        p.recycle();
        return lawpi;
    }

    public LauncherAppWidgetProviderInfo(Parcel in) {
        super(in);

    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getLabel(PackageManager packageManager) {
        if (isCustomWidget) {
            return Utilities.trim(label);
        }
        return super.loadLabel(packageManager);
    }



    public String toString(PackageManager pm) {
        if (isCustomWidget) {
            return "WidgetProviderInfo(" + provider + ")";
        }
        return String.format("WidgetProviderInfo provider:%s package:%s short:%s label:%s",
                provider.toString(), provider.getPackageName(), provider.getShortClassName(), getLabel(pm));
    }


 }
