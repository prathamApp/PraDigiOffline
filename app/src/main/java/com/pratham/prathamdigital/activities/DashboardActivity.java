package com.pratham.prathamdigital.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.ftpSettings.WifiApControl;

import org.apache.commons.net.ftp.FTPClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

//import mayurmhm.mayur.ftpmodule.ftpExplorer.AddFTPServerActivity;
//import mayurmhm.mayur.ftpmodule.ftpExplorer.MainActivity;
//import mayurmhm.mayur.ftpmodule.ftpSettings.FsService;
//import mayurmhm.mayur.ftpmodule.ftpSettings.WifiApControl;

//import org.apache.commons.net.ftp.FTP;

public class DashboardActivity extends AppCompatActivity {

    Switch sw_FtpServer;

    //    EditText edt_ServerName, edt_HostName, edt_Port, edt_Login, edt_Password;
    EditText edt_HostName, edt_Port, edt_Login, edt_Password;
    //Switch sw_AnonymousConnection;
    Button btn_Connect, btn_Reset;
    LinearLayout linearLayout;
    private Uri treeUri;
    private String networkSSID = "PrathamHotSpot";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Memory Allocation
        sw_FtpServer = (Switch) findViewById(R.id.btn_ftpSettings);
//        edt_ServerName = (EditText) findViewById(R.id.edt_Servername);
        edt_HostName = (EditText) findViewById(R.id.edt_HostName);
        edt_Port = (EditText) findViewById(R.id.edt_Port);
        edt_Login = (EditText) findViewById(R.id.edt_Login);
        edt_Password = (EditText) findViewById(R.id.edt_Password);
        //  sw_AnonymousConnection = (Switch) findViewById(R.id.sw_AnonymousConnection);
        btn_Connect = (Button) findViewById(R.id.btn_Save);
        btn_Reset = (Button) findViewById(R.id.btn_Reset);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

//        btn_Connect.setVisibility(View.GONE);
//        btn_Reset.setVisibility(View.GONE);

        pd = new ProgressDialog(this);

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
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(DashboardActivity.this);
            pd.setMessage("Starting Server ... Please wait !!!");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

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

            if (pd != null)
                pd.dismiss();

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

    public String getWifiName(Context context) {
        String ssid = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
        }

        ssid = wifiInfo.getSSID();
        Log.d("ssaid::", ssid);
        return ssid;
    }

    // Connect to FTP Server
    @SuppressLint("StaticFieldLeak")
    public void connect(View view) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd.setMessage("Connecting ... Please wait !!!");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                // Check if already connected to PrathamHotspot
                String SSID = getWifiName(DashboardActivity.this).replace("\"", "");

                if (SSID.equalsIgnoreCase(networkSSID)) {
                    // Connected to PrathamHotspot
                } else {
                    // todo automatically connect to PrathamHotSpot
                    connectToPrathamHotSpot();
                    // Delay for Network change
//                    try {
//                        Thread.sleep(6000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }


                // todo Validate fields & if Connected to FTP Server then Open File Explorer if correct
//                if (edt_ServerName.getText().toString().trim().length() > 0
                if (edt_HostName.getText().toString().trim().length() > 0
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

                                pd.dismiss();

                                Snackbar snackbar = Snackbar.make(linearLayout, "Connected to FTP Server !!!", Snackbar.LENGTH_LONG);
                                snackbar.show();

                                // goto FE Activity
                                Intent i = new Intent(DashboardActivity.this, ShowFilesOnDevice.class);
                                PrathamApplication.client1 = client1;
//                                Bundle bundle = new Bundle();
//                                bundle.putString("treeUri", String.valueOf(treeUri));
//                                i.putExtras(bundle);
                                startActivity(i);
                            } else {
                                pd.dismiss();
                                // custom dialog to manually connect to Pratham Hotspot

                                Snackbar snackbar = Snackbar
                                        .make(linearLayout, "Manually connect to PrathamHotspot !!!", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                                final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                                                intent.setComponent(cn);
                                                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });

                                // Changing message text color
                                snackbar.setActionTextColor(Color.RED);

                                // Changing action button text color
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.YELLOW);
                                snackbar.show();

                      /*          // not connected to ftp server
                                Snackbar snackbar = Snackbar.make(linearLayout, "Please connect to PrathamHotspot Manually !!!", Snackbar.LENGTH_LONG);
                                snackbar.show();
*/
                            }
                        }
                    }.execute();
                } else {
                    // Details incomplete
                    Snackbar snackbar = Snackbar.make(linearLayout, "Please fill all fields !!!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    pd.dismiss();

                }
                return null;
            }

        }.execute();


    }


    private void connectToPrathamHotSpot() {

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", networkSSID);
        wifiConfiguration.priority = 99999;
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

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

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wifiManager.reconnect();

                break;
            }
        }
//        wifiManager.enableNetwork(netId, true);

//        wifiManager.reconnect();
    }

    // Reset Form
    public void resetValues(View view) {

        // Snackbar instead of Toast
        Snackbar snackbar = Snackbar.make(linearLayout, "Cleared !!!", Snackbar.LENGTH_LONG);
        snackbar.show();

//        edt_ServerName.getText().clear();
        edt_HostName.getText().clear();
        edt_Port.getText().clear();
        edt_Login.getText().clear();
        edt_Password.getText().clear();
        //sw_AnonymousConnection.setChecked(false);
        sw_FtpServer.setChecked(false);
    }

}
