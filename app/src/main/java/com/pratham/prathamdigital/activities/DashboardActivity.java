package com.pratham.prathamdigital.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.ftpSettings.WifiApControl;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.hotspot_android.ConnectedDevice;
import com.pratham.prathamdigital.util.hotspot_android.ConnectionResult;
import com.pratham.prathamdigital.util.hotspot_android.Hotspot;
import com.pratham.prathamdigital.util.hotspot_android.HotspotListener;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener;

import org.apache.commons.net.ftp.FTPClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity implements HotspotListener {

    private Uri treeUri;
    private String networkSSID = "PrathamHotSpot";
    PowerManager pm;
    PowerManager.WakeLock wl;
    private WifiManager wifiManager;
    Hotspot hotspot;

    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.ftp_connect_ll)
    LinearLayout ftp_connect_ll;
    @BindView(R.id.wifi_list_layout)
    LinearLayout wifi_list_layout;
    @BindView(R.id.wifi_list)
    ListView wifi_list;
    @BindView(R.id.scan_pb)
    ProgressBar scan_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hotspot = new Hotspot(this);
        hotspot.setListener(this);
        // Wake lock for disabling sleep
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();
    }

    @OnClick(R.id.btn_CreateFTPServer)
    public void createFtpServer() {
        Toast.makeText(DashboardActivity.this, networkSSID, Toast.LENGTH_SHORT).show();
        hotspot.start(networkSSID, "");
    }

    @OnClick(R.id.btn_ConnectWifi)
    public void connectToWifi() {
        rootLayout.setVisibility(View.GONE);
        ftp_connect_ll.setVisibility(View.GONE);
        wifi_list_layout.setVisibility(View.VISIBLE);
        WifiUtils.enableLog(true);
        WifiUtils.withContext(getApplicationContext()).enableWifi();
        WifiUtils.withContext(getApplicationContext()).scanWifi(scanResultsListener).start();
    }

    ScanResultsListener scanResultsListener = new ScanResultsListener() {
        @Override
        public void onScanResults(@NonNull List<ScanResult> scanResults) {
            scan_pb.setVisibility(View.GONE);
            wifi_list.setVisibility(View.VISIBLE);
            String[] wifi_result = new String[scanResults.size()];
            for (int i = 0; i < scanResults.size(); i++) {
                wifi_result[i] = scanResults.get(i).SSID;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DashboardActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, wifi_result);
            wifi_list.setAdapter(adapter);
            wifi_list.setOnItemClickListener(onItemClickListener);
        }
    };

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // ListView Clicked item index
            int itemPosition = position;
            // ListView Clicked item value
            String itemValue = (String) wifi_list.getItemAtPosition(position);
            // Show Alert
//            Toast.makeText(getApplicationContext(),
//                    "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
//                    .show();
            if (!PD_Utility.checkWhetherConnectedToRaspberry(DashboardActivity.this)) {
                WifiUtils.withContext(getApplicationContext())
                        .connectWith(itemValue, "pratham123")
                        .onConnectionResult(new ConnectionSuccessListener() {
                            @Override
                            public void isSuccessful(boolean isSuccess) {
//                                Toast.makeText(DashboardActivity.this, "CONNECTED YAY", Toast.LENGTH_SHORT).show();
                                if (isSuccess) {
                                    PrathamApplication.hotspot_name = itemValue;
                                    onBackPressed();
                                }
                            }
                        })
                        .start();
            } else {
                onBackPressed();
            }
        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        wl.release();
        super.onDestroy();
    }

    @Override
    public void OnDevicesConnectedRetrieved(ArrayList<ConnectedDevice> clients) {

    }

    @Override
    public void OnHotspotStartResult(ConnectionResult result) {
        Toast.makeText(DashboardActivity.this, "Hotspot Created " + result.isSuccessful(), Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(5000);
            DashboardActivity.this.sendBroadcast(new Intent(FsService.ACTION_START_FTPSERVER));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
