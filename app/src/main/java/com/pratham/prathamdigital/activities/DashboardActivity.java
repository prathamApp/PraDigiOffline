package com.pratham.prathamdigital.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.hotspot_android.ConnectedDevice;
import com.pratham.prathamdigital.util.hotspot_android.ConnectionResult;
import com.pratham.prathamdigital.util.hotspot_android.Hotspot;
import com.pratham.prathamdigital.util.hotspot_android.HotspotListener;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity implements HotspotListener, VolleyResult_JSON {

    private Uri treeUri;
    private String networkSSID = "PrathamHotSpot";
    PowerManager pm;
    PowerManager.WakeLock wl;
    private WifiManager wifiManager;
    Hotspot hotspot;
    String itemValue;

    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.ftp_connect_ll)
    LinearLayout ftp_connect_ll;
    @BindView(R.id.enterIP_layout)
    LinearLayout enterIP_layout;
    @BindView(R.id.wifi_list_layout)
    LinearLayout wifi_list_layout;
    @BindView(R.id.wifi_list)
    ListView wifi_list;
    @BindView(R.id.scan_pb)
    ProgressBar scan_pb;
    @BindView(R.id.et_ip_address)
    EditText et_ip_address;
    @BindView(R.id.et_port)
    EditText et_port;
    @BindView(R.id.et_wifi_pass)
    EditText et_wifi_pass;
    @BindView(R.id.btn_connect)
    Button btn_connect;

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
            itemValue = (String) wifi_list.getItemAtPosition(position);
            // Show Alert
//            Toast.makeText(getApplicationContext(),
//                    "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
//                    .show();
            String ip = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this)
                    .getString("RASP_IP", null);

            if (!PD_Utility.checkWhetherConnectedToRaspberry(DashboardActivity.this) || (ip == null || ip.isEmpty())) {
                wifi_list_layout.setVisibility(View.GONE);
                enterIP_layout.setVisibility(View.VISIBLE);
            } else {
                onBackPressed();
            }
        }
    };

    @OnClick(R.id.btn_connect)
    public void setBtn_connect() {
        String ip = et_ip_address.getText().toString();
        String port = et_port.getText().toString();
        String pass = et_wifi_pass.getText().toString();
        if (!ip.isEmpty() && !port.isEmpty() && !pass.isEmpty()) {
            PD_Constant.RASP_IP = "http://" + ip + ":" + port;
            if (!PD_Utility.checkWhetherConnectedToRaspberry(DashboardActivity.this)) {
                WifiUtils.withContext(getApplicationContext())
                        .connectWith(itemValue, pass)
                        .onConnectionResult(new ConnectionSuccessListener() {
                            @Override
                            public void isSuccessful(boolean isSuccess) {
                                if (isSuccess) {
//                                    callFacilityAPI();
                                    notifySuccess("", "");
                                } else {
                                    Toast.makeText(DashboardActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .start();
            } else {
                notifySuccess("", "");
//                callFacilityAPI();
            }
        } else {
            Toast.makeText(DashboardActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
    }

//    private void callFacilityAPI() {
//        JSONObject credentials = new JSONObject();
//        try {
//            credentials.put("username", "pratham");
//            credentials.put("password", "pratham");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new PD_ApiRequest(DashboardActivity.this, DashboardActivity.this)
//                .getacilityIdfromRaspberry("SESSION",
//                        PD_Constant.RASP_IP + "/api/session/", credentials);
//    }

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

    @Override
    public void notifySuccess(String requestType, String response) {
//        Log.d("response:::", response);
//        Log.d("response:::", "requestType:: " + requestType);
//        if (requestType.equalsIgnoreCase("SESSION")) {
//            Gson gson = new Gson();
//            Modal_RaspFacility facility = gson.fromJson(response, Modal_RaspFacility.class);
        PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this)
                .edit()
                .putString("FACILITY", "50de93886d39077b902c6d2f8ff96040")
                .apply();
        PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this)
                .edit()
                .putString("RASP_IP", PD_Constant.RASP_IP)
                .apply();
        onBackPressed();
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        Toast.makeText(DashboardActivity.this, "check the values entered", Toast.LENGTH_SHORT).show();
        WifiUtils.withContext(getApplicationContext()).disableWifi();
    }
}
