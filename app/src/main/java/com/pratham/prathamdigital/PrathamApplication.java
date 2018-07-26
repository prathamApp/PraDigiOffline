package com.pratham.prathamdigital;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.androidnetworking.AndroidNetworking;
import com.pratham.prathamdigital.util.ConnectivityReceiver;

import net.vrallev.android.cat.Cat;

import org.apache.commons.net.ftp.FTPClient;

import java.util.UUID;

/**
 * Created by HP on 09-08-2017.
 */

public class PrathamApplication extends Application {
    private static PrathamApplication mInstance;
    public static FTPClient client1;
    public static String hotspot_name = "";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static String getVersion() {
        Context context = getAppContext();
        String packageName = context.getPackageName();
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Cat.e("Unable to find the name " + packageName + " in the package");
            return null;
        }
    }


    public static String path = "";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        mInstance = this;
    }


    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static String sessionId = UUID.randomUUID().toString();

    public static synchronized PrathamApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        PrathamApplication.path = path;
    }
}

