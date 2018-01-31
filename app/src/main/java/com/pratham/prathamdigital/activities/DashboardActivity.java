package com.pratham.prathamdigital.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.ftpSettings.WifiApControl;

import org.apache.commons.net.ftp.FTPClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import mayurmhm.mayur.ftpmodule.ftpExplorer.AddFTPServerActivity;
//import mayurmhm.mayur.ftpmodule.ftpExplorer.MainActivity;
//import mayurmhm.mayur.ftpmodule.ftpSettings.FsService;
//import mayurmhm.mayur.ftpmodule.ftpSettings.WifiApControl;

//import org.apache.commons.net.ftp.FTP;

public class DashboardActivity extends AppCompatActivity {

    Switch sw_FtpServer;

    EditText edt_ServerName, edt_HostName, edt_Port, edt_Login, edt_Password;
    Switch sw_AnonymousConnection;
    Button btn_Connect, btn_Reset;
    LinearLayout linearLayout;
    private Uri treeUri;
    private String networkSSID = "PrathamHotSpot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Memory Allocation
        sw_FtpServer = (Switch) findViewById(R.id.btn_ftpSettings);
        edt_ServerName = (EditText) findViewById(R.id.edt_Servername);
        edt_HostName = (EditText) findViewById(R.id.edt_HostName);
        edt_Port = (EditText) findViewById(R.id.edt_Port);
        edt_Login = (EditText) findViewById(R.id.edt_Login);
        edt_Password = (EditText) findViewById(R.id.edt_Password);
        sw_AnonymousConnection = (Switch) findViewById(R.id.sw_AnonymousConnection);
        btn_Connect = (Button) findViewById(R.id.btn_Save);
        btn_Reset = (Button) findViewById(R.id.btn_Reset);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        btn_Connect.setVisibility(View.GONE);
        btn_Reset.setVisibility(View.GONE);

        // Switch Press Action
        sw_FtpServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    // Start HotSpot
                    CreateWifiAccessPoint createOne = new CreateWifiAccessPoint();
                    createOne.execute((Void) null);
                } else if (isChecked == false) {
                    // Stop Hotspot
                    turnOnOffHotspot(DashboardActivity.this, false);
                    // Stop Server
                    stopServer();
                    // Snackbar instead of Toast
                    Snackbar snackbar = Snackbar.make(linearLayout, "Server Stopped !!!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private class CreateWifiAccessPoint extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
            boolean methodFound = false;
            for (Method method : wmMethods) {
                if (method.getName().equals("setWifiApEnabled")) {
                    methodFound = true;
                    WifiConfiguration netConfig = new WifiConfiguration();
                    netConfig.SSID = networkSSID;
                    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    try {
                        final boolean apStatus = (Boolean) method.invoke(wifiManager, netConfig, true);
                        for (Method isWifiApEnabledMethod : wmMethods)
                            if (isWifiApEnabledMethod.getName().equals("isWifiApEnabled")) {
                                while (!(Boolean) isWifiApEnabledMethod.invoke(wifiManager)) {
                                }
                                for (Method method1 : wmMethods) {
                                    if (method1.getName().equals("getWifiApState")) {
                                    }
                                }
                            }

                    } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return methodFound;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            // delay for creating hotspot
            try {
                // Snackbar instead of Toast
                Snackbar snackbar = Snackbar.make(linearLayout, "HotSpot Created !!!", Snackbar.LENGTH_LONG);
                snackbar.show();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Start Server
            startServer();
            // Snackbar instead of Toast
            Snackbar snackbar = Snackbar.make(linearLayout, "Server Started !!!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    // Turns off WiFi HotSpot
    public static void turnOnOffHotspot(Context context, boolean isTurnToOn) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiApControl apControl = WifiApControl.getApControl(wifiManager);
        if (apControl != null) {

            // TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
            //if (isWifiOn(context) && isTurnToOn) {
            //  turnOnOffWifi(context, false);
            //}

            apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
                    isTurnToOn);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean serviceRunning = checkServiceRunning();

        if (!serviceRunning)
            sw_FtpServer.setChecked(false);
    }

    // Checking FTP Service is on or not
    public boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("mayurmhm.mayur.ftpmodule.ftpSettings.FsService"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startServer() {
        sendBroadcast(new Intent(FsService.ACTION_START_FTPSERVER));
    }

    private void stopServer() {
        sendBroadcast(new Intent(FsService.ACTION_STOP_FTPSERVER));
    }


    // Connect to FTP Server
    @SuppressLint("StaticFieldLeak")
    public void connect(View view) {

        // todo automatically connect to PrathamHotSpot
        connectToPrathamHotSpot();

//         Delay for Network change
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // todo Validate fields & if Connected to FTP Server then Open File Explorer if correct
        if (edt_ServerName.getText().toString().trim().length() > 0
                && edt_HostName.getText().toString().trim().length() > 0
                && edt_Port.getText().toString().trim().length() > 0
                && edt_Login.getText().toString().trim().length() > 0
                && edt_Password.getText().toString().trim().length() > 0) {

            // todo if connected to FTP Server
//            final FTPClient[] client = new FTPClient[1];
            FTPClient client1 = new FTPClient();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        client1.connect(edt_HostName.getText().toString(), Integer.parseInt(edt_Port.getText().toString()));
                        client1.login(edt_Login.getText().toString(), edt_Password.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    // todo if connected to ftp server list files
                    if (client1.isConnected()) {
                        Snackbar snackbar = Snackbar.make(linearLayout, "Connected to FTP Server !!!", Snackbar.LENGTH_LONG);
                        snackbar.show();


                        // goto FE Activity
                        Intent i = new Intent(DashboardActivity.this, ShowFilesOnDevice.class);
                        PrathamApplication.client1 = client1;
                        Bundle bundle = new Bundle();
                        bundle.putString("treeUri", String.valueOf(treeUri));
                        i.putExtras(bundle);
                        startActivity(i);
                    } else {
                        // not connected to ftp server
                        Snackbar snackbar = Snackbar.make(linearLayout, "Not Connected to FTP Server !!!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }.execute();
        } else {
            // Details incomplete
            Snackbar snackbar = Snackbar.make(linearLayout, "Please fill all fields !!!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }


    private void connectToPrathamHotSpot() {

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", networkSSID);
        wifiConfiguration.priority = 99999;

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfiguration);

        if (wifiManager.isWifiEnabled()) { //---wifi is turned on---
            //---disconnect it first---
            wifiManager.disconnect();
        } else { //---wifi is turned off---
            //---turn on wifi---
            wifiManager.setWifiEnabled(true);
            wifiManager.disconnect();
        }

        wifiManager.enableNetwork(netId, true);
        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wifiManager.reconnect();
    }

    // Reset Form
    public void resetValues(View view) {

        // Snackbar instead of Toast
        Snackbar snackbar = Snackbar.make(linearLayout, "Cleared !!!", Snackbar.LENGTH_LONG);
        snackbar.show();

        edt_ServerName.getText().clear();
        edt_HostName.getText().clear();
        edt_Port.getText().clear();
        edt_Login.getText().clear();
        edt_Password.getText().clear();
        sw_AnonymousConnection.setChecked(false);
        sw_FtpServer.setChecked(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        treeUri = data.getData();
        final int takeFlags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
        }
    }

    public void chooseSDCard(View view) {
        btn_Connect.setVisibility(View.VISIBLE);
        btn_Reset.setVisibility(View.VISIBLE);
        // Snackbar instead of Toast
        Snackbar snackbar = Snackbar.make(linearLayout, "SD Card Selected !!!", Snackbar.LENGTH_LONG);
        snackbar.show();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }
}
