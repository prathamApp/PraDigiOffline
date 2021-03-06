package com.pratham.prathamdigital.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aloj.progress.DownloadProgressView;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_AgeFilterAdapter;
import com.pratham.prathamdigital.adapters.RV_LevelAdapter;
import com.pratham.prathamdigital.adapters.RV_LibraryContentAdapter;
import com.pratham.prathamdigital.adapters.RV_RecommendAdapter;
import com.pratham.prathamdigital.adapters.RV_SubLibraryAdapter;
import com.pratham.prathamdigital.async.ImageDownload;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.content_playing.Activity_WebView;
import com.pratham.prathamdigital.content_playing.TextToSp;
import com.pratham.prathamdigital.custom.GalleryLayoutManager;
import com.pratham.prathamdigital.custom.ScaleTransformer;
import com.pratham.prathamdigital.custom.custom_fab.FloatingActionButton;
import com.pratham.prathamdigital.custom.dialogs.SweetAlertDialog;
import com.pratham.prathamdigital.custom.fancy_toast.TastyToast;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.ExtractInterface;
import com.pratham.prathamdigital.interfaces.Interface_Level;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Level;
import com.pratham.prathamdigital.models.Modal_Rasp_Content;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.ConnectivityReceiver;
import com.pratham.prathamdigital.util.CopyFiles;
import com.pratham.prathamdigital.util.FileUtil;
import com.pratham.prathamdigital.util.NetworkChangeReceiver;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;
import com.pratham.prathamdigital.util.SDCardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// Location

/**
 * Created by HP on 17-08-2017.
 */

public class
Activity_Main extends ActivityManagePermission implements MainActivityAdapterListeners,
        VolleyResult_JSON, Observer, ProgressUpdate, Interface_Level, ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener, ConnectivityReceiver.ConnectivityReceiverListener, ExtractInterface {

    private static final String TAGs = Activity_Main.class.getSimpleName();
    private static final int SEARCH = 1;
    private static final int MY_LIBRARY = 2;
    private static final int RECOMMEND = 3;
    private static final int LANGUAGE = 4;
    private static final int ACTIVITY_LANGUAGE = 1;
    private static final int ACTIVITY_DOWNLOAD = 2;
    private static final int ACTIVITY_SEARCH = 3;
    private static final int ACTIVITY_PDF = 4;
    private static final int ACTIVITY_DOWNLOAD_METHOD = 5;
    @BindView(R.id.content_rv)
    RecyclerView content_rv;
    @BindView(R.id.gallery_rv)
    RecyclerView gallery_rv;
    @BindView(R.id.c_fab_language)
    FloatingActionButton fab_language;
    @BindView(R.id.fab_share)
    FloatingActionButton fab_share;
    @BindView(R.id.c_fab_search)
    FloatingActionButton fab_search; // Import Data
    //    @BindView(R.id.fab_recom)
//    FloatingActionButton fab_recom;
    @BindView(R.id.fab_my_library)
    FloatingActionButton fab_my_library;
    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    @BindView(R.id.rl_no_content)
    RelativeLayout rl_no_content;
    @BindView(R.id.rl_no_internet)
    RelativeLayout rl_no_internet;
    @BindView(R.id.img_no_net)
    ImageView img_no_net;
    @BindView(R.id.main_fab_download)
    DownloadProgressView main_fab_download;
    @BindView(R.id.main_download_title)
    TextView main_download_title;
    @BindView(R.id.main_rl_download)
    RelativeLayout main_rl_download;
    @BindView(R.id.level_rv)
    RecyclerView level_rv;

    int[] english_age_id = {1100, 1101, 1102, 1103};
    int[] hindi_age_id = {20, 21, 22, 23};
    int[] marathi_age_id = {25, 26, 27, 28};
    int[] kannada_age_id = {30, 31, 32, 33};
    int[] telugu_age_id = {35, 36, 37, 38};
    int[] bengali_age_id = {40, 41, 42, 43};
    int[] gujarati_age_id = {45, 46, 47, 48};
    int[] assamese_age_id = {50, 51, 52, 53};
    int[] punjabi_age_id = {55, 56, 57, 58};
    int[] odiya_age_id = {60, 61, 62, 63};
    int[] tamil_age_id = {65, 66, 67, 68};

    int[] childs = {R.drawable.khel_badi, R.drawable.khel_puri, R.drawable.dekho_aur_seekho};
    private String[] age;
    private String[] parent_ids;
    private boolean isInitialized;
    RV_AgeFilterAdapter ageFilterAdapter;
    RV_RecommendAdapter rv_recommendAdapter;
    RV_LevelAdapter rv_levelAdapter;
    private AlertDialog dialog;
    private ArrayList<Modal_ContentDetail> arrayList_content = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> to_be_downloaded = new ArrayList<>();
    private ArrayList<Modal_Level> arrayList_level = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> contentDownloading = new ArrayList<>();
    public static DatabaseHandler db;
    private int selected_content;
    private ArrayList<Modal_ContentDetail> downloadContents = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> subContents = new ArrayList<>();
    private RV_LibraryContentAdapter libraryContentAdapter;
    private RV_SubLibraryAdapter subLibraryAdapter;
    private boolean isLibrary = false;
    private GalleryLayoutManager layoutManager;
    String googleId;
    private String url;
    public static TextToSp ttspeech;

    Boolean SDcard;
    public static final int RequestPermissionCode = 1;
    int RequestCheckResult;
    boolean RequestTF;

    // Location
    // LogCat tag
    private static final String TAG = Activity_Main.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    ArrayList<String> path = new ArrayList<String>();
    boolean isConnected;
    SharedPreferences pref;
    int SDCardLocationChooser = 7;
    private String shareItPath;
    private int PraDigiPath = 99;
    private Uri treeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);
        ButterKnife.bind(this);
        ttspeech = new TextToSp(this);
        dialog = PD_Utility.showLoader(Activity_Main.this);
        db = new DatabaseHandler(Activity_Main.this);
        PD_Utility.setLocale(this, db.GetUserLanguage());
        googleId = db.getGoogleID();
        Log.d("googleId::", googleId);
        isInitialized = false;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        PD_Constant.RASP_IP = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("RASP_IP", null);
        // Location
        // First we need to check availability of play services
//        if (checkPlayServices()) {
//            // Building the GoogleApi client
//            buildGoogleApiClient();
//            // createLocationRequest();
//        }
        // Checking Internet Connection
//        checkConnection();
//        if (isConnected) {
//            // Push the new Score to Server if connected to Internet
//            try {
//                PushDataToServer push = new PushDataToServer();
//                push.execute();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
/*
        // NO LONGER NEEDED AS WE ARE DOING THE SAME THING IN onResume() but STILL DONT DELETE
        // Show location
         displayLocation();
        // Toggling the periodic location updates
       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                togglePeriodicLocationUpdates();
            }
        }, 2000);*/
    }

    @Override
    public void onExtractDone(String zipPath) {
//        fab_my_library.performClick();
    }

    // Push the new Score to Server if connected to Internet
//    public class PushDataToServer extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//            // Runs on UI thread
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            // Runs on the background thread
//            pushNewData();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void res) {
//            // Runs on the UI thread
//        }
//    }

    // Check internet Connection
// Receiver for checking connection
//    private void checkConnection() {
//        isConnected = ConnectivityReceiver.isConnected();
//    }

    private void pushNewData() {
        // Get New Data from DB(sentFlag = 0)
        DatabaseHandler sdb = new DatabaseHandler(Activity_Main.this);
        List<Modal_Score> scores = sdb.getNewScores();
        if (scores != null && scores.size() > 0) {
            JSONArray scoreData = new JSONArray();
            try {
                for (int i = 0; i < scores.size(); i++) {
                    JSONObject _obj = new JSONObject();
                    Modal_Score scoreObj = (Modal_Score) scores.get(i);
                    try {
                        _obj.put("sessionId", scoreObj.SessionId);
                        _obj.put("deviceId", scoreObj.DeviceId);
                        _obj.put("resourceId", scoreObj.ResourceId);
                        _obj.put("questionId", scoreObj.QuestionId);
                        _obj.put("scoredMarks", scoreObj.ScoredMarks);
                        _obj.put("location", scoreObj.Location);
                        _obj.put("totalMarks", scoreObj.TotalMarks);
                        _obj.put("startDateTime", scoreObj.StartTime);
                        _obj.put("endDateTime", scoreObj.EndTime);
                        _obj.put("level", scoreObj.Level);
                        scoreData.put(_obj);
                        // creating json file
//                        String requestString = "{  \"scoreData\": " + scoreData + "}";
//                        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//                        WriteSettings(Activity_Main.this, requestString, "pushNewDataToServer-" + (deviceId.equals(null) ? "0000" : deviceId));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Pushing File to Server
                Log.d("array:::", scoreData.toString());
                showDialog();
                new PD_ApiRequest(Activity_Main.this, Activity_Main.this)
                        .pushDataToRaspberry("SCORE", PD_Constant.RASP_IP + "/pratham/datastore/",
                                scoreData.toString(), "raspberry", "score");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get Address
    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to display the location on UI
     */
//    private void displayLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            double latitude = mLastLocation.getLatitude();
//            double longitude = mLastLocation.getLongitude();
//            // pass latitude & longitude to address to get the Location Name
//            Address locationAddress = getAddress(latitude, longitude);
//            if (locationAddress != null) {
//                String address = locationAddress.getAddressLine(0);
//                String address1 = locationAddress.getAddressLine(1);
//                String city = locationAddress.getLocality();
//                String state = locationAddress.getAdminArea();
//                String country = locationAddress.getCountryName();
//                String postalCode = locationAddress.getPostalCode();
//                String currentLocation;
//                if (!TextUtils.isEmpty(address)) {
//                    currentLocation = address;
//                    SharedPreferences.Editor editor = pref.edit();
//                    // Clear Prev Data
//                    editor.clear();
//                    editor.commit(); // commit changes
//                    // Store Last Known Location
//                    editor.putString("prefLocation", currentLocation); // Storing string
//                    editor.commit(); // commit changes
//                    //Toast.makeText(this, currentLocation, Toast.LENGTH_SHORT).show();
//                }
//
//            }
//            //Toast.makeText(this, "lat : " + latitude + ", lon : " + longitude, Toast.LENGTH_SHORT).show();
//        } else {
//            // Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_SHORT).show();
//        }
//    }

    /**
     * Creating google api client object
     */
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API).build();
//    }

    /**
     * Method to verify google play services on the device
     */
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "This device is not supported.", Toast.LENGTH_LONG)
//                        .show();
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
    @Override
    protected void onStart() {
        super.onStart();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    /**
     * Creating location request object
     */
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
//    }
//
    /**
     * Starting the location updates
     */
//    protected void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }h
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
//        displayLocation();
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();
        // Displaying the new location on UI
//        displayLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        PrathamApplication.getInstance().setConnectivityListener(this);
        // Location
//        checkPlayServices();
        // Resuming the periodic location updates
//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
        NetworkChangeReceiver.getObservable().addObserver(this);
        if ((!isInitialized)) {
            layoutManager = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL);
            layoutManager.setItemTransformer(new ScaleTransformer());
            layoutManager.attach(gallery_rv);
            gallery_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
            LinearLayoutManager layoutManager3 = new GridLayoutManager(Activity_Main.this, 3);
            content_rv.setLayoutManager(layoutManager3);
            content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
            LinearLayoutManager layoutManager4 = new LinearLayoutManager(Activity_Main.this, LinearLayoutManager.HORIZONTAL, false);
            level_rv.setLayoutManager(layoutManager4);
            isInitialized = true;
            Boolean IntroStatus = db.CheckIntroShownStatus(googleId);
            if (!IntroStatus) {
                // Show Intro & then set flag as shown
                Log.d("IntroStatus:", db.CheckIntroShownStatus(googleId) + "");
                ShowIntro(LANGUAGE);
            } else {
                fab_my_library.performClick();
            }
        }
        if (!isPermissionsGranted(Activity_Main.this, new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    BackupDatabase.backup(Activity_Main.this);
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                }
            });
        } else {
            BackupDatabase.backup(Activity_Main.this);
        }
    }

    private void ShowIntro(final int target) {
        int id = 0;
        String text = "";
        String content_text = "";
        if (target == MY_LIBRARY) {
            id = R.id.fab_my_library;
            text = getResources().getString(R.string.my_library);
            content_text = getResources().getString(R.string.view_contents);
        } /*else if (target == SEARCH) {
            id = R.id.c_fab_search;
            text = getResources().getString(R.string.search);
            content_text = getResources().getString(R.string.search_and_download);
        }  else if (target == RECOMMEND) {
            id = R.id.fab_recom;
            text = getResources().getString(R.string.recommended);
            content_text = getResources().getString(R.string.download_recommended);
        } */ else {
            id = R.id.c_fab_language;
            text = getResources().getString(R.string.language);
            content_text = getResources().getString(R.string.select_language);
        }
//        new MaterialTapTargetPrompt.Builder(Activity_Main.this)
//                .setTarget(findViewById(id))
//                .setPrimaryTextTypeface(PD_Utility.getFont(Activity_Main.this))
//                .setPrimaryText(text)
//                .setSecondaryTextTypeface(PD_Utility.getFont(Activity_Main.this))
//                .setSecondaryText(content_text)
//                .setAnimationInterpolator(new FastOutSlowInInterpolator())
//                .setBackButtonDismissEnabled(false)
//                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
//                    @Override
//                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
//                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
//                            /*if (target == SEARCH) {
////                                ShowIntro(LANGUAGE);
//                                ShowIntro(RECOMMEND);
//                            } else */
//                            if (target == MY_LIBRARY) {
        fab_my_library.performClick();
//
//                            } /*else if (target == RECOMMEND) {
//                                db.SetIntroFlagTrue(1, googleId);
//                                fab_recom.performClick();
//                            } */ else {
//                                fab_language.performClick();
//                            }
//                        }
//                    }
//                })
//                .show();
    }

    private void initializeGalleryAdapater() {
        arrayList_level.clear();
        if (isLibrary) {
            downloadContents = db.Get_Contents(PD_Constant.TABLE_PARENT, "0");
            ArrayList<Integer> positionWithNoChilds = new ArrayList<>();
            for (int i = 0; i < downloadContents.size(); i++) {
                int count = db.Get_Total_Contents(downloadContents.get(i).getNodeid());
                if (count == 0) {
                    db.deleteContentFromParent(downloadContents.get(i).getNodeid());
                    positionWithNoChilds.add(i);
                }
            }
            PD_Utility.DEBUG_LOG(1, TAG, "positionWithNoChilds::" + positionWithNoChilds.size());
            for (int j = positionWithNoChilds.size() - 1; j >= 0; j--) {
                downloadContents.remove(positionWithNoChilds.get(j));
            }
            PD_Utility.DEBUG_LOG(1, TAG, "db_list_size::" + downloadContents.size());
            if (downloadContents.size() > 0) {
                gallery_rv.setVisibility(View.VISIBLE);
                rl_no_internet.setVisibility(View.GONE);
                rl_no_data.setVisibility(View.GONE);
                rl_no_content.setVisibility(View.GONE);
                content_rv.setVisibility(View.VISIBLE);
                libraryContentAdapter = new RV_LibraryContentAdapter(Activity_Main.this, this, downloadContents);
                gallery_rv.setAdapter(libraryContentAdapter);
            } else {
                setRecyclerLevel(null);
                gallery_rv.setVisibility(View.GONE);
                content_rv.setVisibility(View.GONE);
                rl_no_data.setVisibility(View.VISIBLE);
            }
        } else {
            // todo child & parent logic
            gallery_rv.setVisibility(View.VISIBLE);
            rl_no_data.setVisibility(View.GONE);
            age = getResources().getStringArray(R.array.main_contents);
            //age = getResources().getStringArray(R.array.main_contents);
            ageFilterAdapter = new RV_AgeFilterAdapter(this, this, age, childs);
            gallery_rv.setAdapter(ageFilterAdapter);
        }
        layoutManager.setOnItemSelectedListener(itemSelectedListener);
    }

    GalleryLayoutManager.OnItemSelectedListener itemSelectedListener = new GalleryLayoutManager.OnItemSelectedListener() {
        @Override
        public void onItemSelected(RecyclerView recyclerView, View item, final int position) {
            arrayList_level.clear();
            if (isLibrary) {
                content_rv.setVisibility(View.VISIBLE);
                subContents = db.Get_Contents(PD_Constant.TABLE_CHILD, downloadContents.get(position).getNodeid());
                Modal_Level level = new Modal_Level();
                level.setName(downloadContents.get(position).getNodetitle());
                level.setId(downloadContents.get(position).getNodeid());
                setRecyclerLevel(level);
                if (subLibraryAdapter == null) {
                    subLibraryAdapter = new RV_SubLibraryAdapter(Activity_Main.this, Activity_Main.this, subContents);
                    content_rv.setAdapter(subLibraryAdapter);
                } else {
                    content_rv.setAdapter(subLibraryAdapter);
                    content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerRecommend);
                    subLibraryAdapter.updateData(subContents);
                }
            } else {
                // todo scroll up change for raspberry
//                Toast.makeText(Activity_Main.this, ".mnn,", Toast.LENGTH_SHORT).show();
//                showDialog();
                new PD_ApiRequest(Activity_Main.this, Activity_Main.this).
                        getContentFromRaspberry("CONTENT_CHILD",
                                PD_Constant.RASP_IP + "/api/contentnode?parent=f9da12749d995fa197f8b4c0192e7b2c");
//                subContents.clear();
//                for (Modal_ContentDetail content : arrayList_content) {
//                    if (content.getParentid() != null) {
//                        if (content.getParentid().equalsIgnoreCase(parent_ids[position]))
//                            subContents.add(content);
//                    }
//                }
//                if (subContents.size() > 0) {
//                    content_rv.setVisibility(View.VISIBLE);
//                    rl_no_content.setVisibility(View.GONE);
//                    if (rv_recommendAdapter == null) {
//                        rv_recommendAdapter = new RV_RecommendAdapter(Activity_Main.this,
//                                Activity_Main.this, subContents);
//                        content_rv.setAdapter(rv_recommendAdapter);
//                    } else {
//                        content_rv.setAdapter(rv_recommendAdapter);
//                        content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
//                        rv_recommendAdapter.updateData(subContents);
//                        Log.d("content_size::", arrayList_content.size() + "");
//                    }
//                } else {
//                    content_rv.setVisibility(View.GONE);
//                    rl_no_content.setVisibility(View.VISIBLE);
//                }
            }
        }
    };

    private void setRecyclerLevel(Modal_Level level) {
        if (level != null) {
            arrayList_level.add(level);
        }
        if (rv_levelAdapter == null) {
            rv_levelAdapter = new RV_LevelAdapter(Activity_Main.this, Activity_Main.this, arrayList_level);
            level_rv.setAdapter(rv_levelAdapter);
        } else {
            rv_levelAdapter.updateLevel(arrayList_level);
        }
    }

    ViewTreeObserver.OnPreDrawListener preDrawListenerBrowse = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            gallery_rv.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < gallery_rv.getChildCount(); i++) {
                View view = gallery_rv.getChildAt(i);
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(i * 100);
            }
            return true;
        }
    };
    ViewTreeObserver.OnPreDrawListener preDrawListenerContent = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            content_rv.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < content_rv.getChildCount(); i++) {
                View view = content_rv.getChildAt(i);
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };
    ViewTreeObserver.OnPreDrawListener preDrawListenerRecommend = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            content_rv.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < content_rv.getChildCount(); i++) {
                View view = content_rv.getChildAt(i);
                view.animate().cancel();
                view.setTranslationX(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationX(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };

    @SuppressLint("RestrictedApi")
    @OnClick(R.id.c_fab_language)
    public void setLanguage() {
        Intent intent = new Intent(Activity_Main.this, Activity_LanguagDialog.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                fab_language, "transition_dialog");
        startActivityForResult(intent, ACTIVITY_LANGUAGE, options.toBundle());
    }

    @OnClick(R.id.fab_my_library)
    public void setFabLibrary() {
        if (!db.CheckIntroShownStatus(googleId)) {
            db.SetIntroFlagTrue(1, googleId);
        }
        isLibrary = true;
        // Main Content ListView Title
        PD_Utility.setFont(Activity_Main.this, txt_title);
        txt_title.setAlpha(0f);
        txt_title.setText(getResources().getString(R.string.my_library));
        txt_title.animate().alpha(1f).setStartDelay(100).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
        initializeGalleryAdapater(/*isLibrary*/);
    }

    @OnClick(R.id.c_fab_search)
    public void importData() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(Activity_Main.this, fab_search);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //PUSH DATA TO KOLIBRI
                pushNewData();
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    private void intStorageAvailable() {
        new CopyFiles(db, shareItPath, Activity_Main.this,
                Activity_Main.this).execute();
    }


    public void extSDCardAvailable() {
        // File Picker
        if (!isPermissionsGranted(Activity_Main.this, new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
//                            chooseFile();
                    selectSDCard();
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                }
            });
        } else {
            selectSDCard();
            // Choose content.zip file containing data
            //chooseFile();    // Internal Storage Code

            // Call File Choser for selectiong POS Ext Zip
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Main.this);
//                    alertDialogBuilder.setMessage("Please select content.zip from Device !!!");
//                    alertDialogBuilder.setPositiveButton("Ok",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface arg0, int arg1) {
//                                    chooseFile();
//                                }
//                            });
//                    AlertDialog alertDialog = alertDialogBuilder.create();
//                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
//                    alertDialog.setTitle("POSexternal.zip à¤«à¤¼à¤¾à¤‡à¤² à¤šà¥à¤¨à¥‡à¤‚");
//                    alertDialog.setCanceledOnTouchOutside(false);
//                    alertDialog.show();
        }

    }

    public static boolean hasRealRemovableSdCard(Context context) {
        return ContextCompat.getExternalFilesDirs(context, null).length >= 2;
    }

    // Update Media
//    private void chooseFile() {
//        DialogProperties properties = new DialogProperties();
//        properties.selection_mode = DialogConfigs.SINGLE_MODE;
//        properties.selection_type = DialogConfigs.FILE_SELECT;
//        properties.root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
//        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
//        properties.extensions = null;
//        FilePickerDialog dialog = new FilePickerDialog(Activity_Main.this, properties);
//        dialog.setTitle("Select a File");
//        dialog.setDialogSelectionListener(new DialogSelectionListener() {
//            @Override
//            public void onSelectedFilePaths(String[] files) {
//                //files is the array of the paths of files selected by the Application User.
//                Log.d("path:::", files[0]);
//                shareItPath = files[0];
////                selectSDCard();
//            }
//        });
//        dialog.show();
//    }

    private void selectSDCard() {
//        final String ReceivedFileName = shareItPath.replace("content://com.estrongs.files/storage/emulated/0/SHAREit/files/", "");
//        Log.d("shareItPath ::::", shareItPath);
//        Log.d("ReceivedFileName ::::", ReceivedFileName);
        // If Zip Found
//        if (ReceivedFileName.endsWith("content.zip")) {
//                    String TargetPath = shareItPath.replace("POSexternal.zip","");
//        String target = new File(shareItPath).getParent();
//        Log.d("target::", target);

        // If Shared Pref are empty
        if (PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", null) == null
                && PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("PATH", "").equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Main.this);
            //  alertDialogBuilder.setMessage("Keep your Tablet Activity_Main charged & Select External SD Card Path !!!");
            LayoutInflater factory = LayoutInflater.from(Activity_Main.this);
            final View view = factory.inflate(R.layout.custom_alert_box_sd_card, null);
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            startActivityForResult(intent, SDCardLocationChooser);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setTitle("Choose SD card in Root Location");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setView(view);
            alertDialog.show();
//            } else {
//                try {
//                    new CopyFiles(shareItPath, Activity_Main.this,
//                            Activity_Main.this).execute();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        } else {

            // Ask Dialog even if shared pref available or filled
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Main.this);
//            //  alertDialogBuilder.setMessage("Keep your Tablet Activity_Main charged & Select External SD Card Path !!!");
//            LayoutInflater factory = LayoutInflater.from(Activity_Main.this);
//            final View view = factory.inflate(R.layout.custom_alert_box_sd_card, null);
//            alertDialogBuilder.setView(view);
//            alertDialogBuilder.setPositiveButton("Ok",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                            startActivityForResult(intent, SDCardLocationChooser);
//                        }
//                    });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
//            alertDialog.setTitle("Choose SD card in Root Location");
//            alertDialog.setCanceledOnTouchOutside(false);
//            alertDialog.setView(view);
//            alertDialog.show();
            String path = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("PATH", "");
            Uri treeUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", ""));
            extractToSDCard(path, treeUri);

//            new CopyFiles(db, shareItPath, Activity_Main.this,
//                    Activity_Main.this).execute();
        }
    }


//    // Update Media Content             // OLD CODE INTERNAL
//    private void chooseFile() {
//        DialogProperties properties = new DialogProperties();
//        properties.selection_mode = DialogConfigs.SINGLE_MODE;
//        properties.selection_type = DialogConfigs.FILE_SELECT;
//        properties.root = new File("/");
//        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
//        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
//        properties.extensions = null;
//        FilePickerDialog dialog = new FilePickerDialog(Activity_Main.this, properties);
//        dialog.setTitle("Select a File");
//        dialog.setDialogSelectionListener(new DialogSelectionListener() {
//            @Override
//            public void onSelectedFilePaths(String[] files) {
//                //files is the array of the paths of files selected by the Application User.
//                Log.d("path:::", files[0]);
//                if (files[0].endsWith("content.zip")) {
//                    new CopyTask(Activity_Main.this, files[0], Activity_Main.this);
//                }
//            }
//        });
//        dialog.show();
//    }


//    @OnClick(R.id.c_fab_search)
//    public void setFabSearch() {
//        Intent intent = new Intent(Activity_Main.this, Activity_Search.class);
//        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
//                fab_search, "transition_search");
//        startActivityForResult(intent, ACTIVITY_SEARCH, options.toBundle());
//    }
//
//    @OnClick(R.id.fab_recom)
//    public void setFabRecom() {
//        if (!db.CheckIntroShownStatus(googleId)) {
//            db.SetIntroFlagTrue(1, googleId);
//        }
//        isLibrary = false;
//        txt_title.setAlpha(0f);
//        txt_title.setText(getResources().getString(R.string.recommended));
//        PD_Utility.setFont(Activity_Main.this, txt_title);
//        txt_title.animate().alpha(1f).setStartDelay(100).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
//        initializeGalleryAdapater(isLibrary);
//    }

    @SuppressLint("RestrictedApi")
    @OnClick(R.id.fab_share)
    public void FTPModule() {
        if (to_be_downloaded.size() == 0)
            contentDownloading.clear();
        arrayList_level.clear();
        // Select PraDigi Path
        String path = "";
        // TODO Path Change
        // Check folder exists on Internal
        File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
        String ip = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this)
                .getString("RASP_IP", null);
        if (ip == null || ip.isEmpty()) {
            Intent i = new Intent(Activity_Main.this, DashboardActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this, fab_share, "transition_dialog");
            startActivityForResult(i, ACTIVITY_DOWNLOAD_METHOD, options.toBundle());
        } else {
//            if (intPradigi.exists()) {
//                // Data found on Internal Storage
//                //path = Environment.getExternalStorageDirectory() + "/PraDigi/databases/PrathamDB";
//                PrathamApplication.setPath(intPradigi.toString());
//                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).edit().putString("PATH",
//                        intPradigi.toString()).apply();
//                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).edit().putString("URI",
//                        null).apply();
//                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).edit().putBoolean("IS_SDCARD",
//                        false).apply();
//                // goto Dashboard if path is selected
//                if (!PD_Utility.checkWhetherConnectedToRaspberry(Activity_Main.this)) {
//                    Intent i = new Intent(Activity_Main.this, DashboardActivity.class);
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this, fab_share, "transition_dialog");
//                    startActivityForResult(i, ACTIVITY_DOWNLOAD_METHOD, options.toBundle());
//                } else {
//                    onActivityResult(ACTIVITY_DOWNLOAD_METHOD, 0, null);
//                }
//            }
//            // Check extSDCard present or not
//            else if (hasRealRemovableSdCard(Activity_Main.this)) {
//                // sd card select
//                if (PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", null) == null) {
//                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    startActivityForResult(intent, PraDigiPath);
//                } else {
//                    PrathamApplication.setPath(PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("PATH", ""));
            if (!PD_Utility.checkWhetherConnectedToRaspberry(Activity_Main.this)) {
                Intent i = new Intent(Activity_Main.this, DashboardActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this, fab_share, "transition_dialog");
                startActivityForResult(i, ACTIVITY_DOWNLOAD_METHOD, options.toBundle());
            } else {
                onActivityResult(ACTIVITY_DOWNLOAD_METHOD, 0, null);
            }
//                }
//            }
        }

/*        // Select Download Method
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.download_method);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ImageButton createFTPServer = (ImageButton) dialog.findViewById(R.id.btn_CreateFTPServer);
        ImageButton connectFTPServer = (ImageButton) dialog.findViewById(R.id.btn_ConnectFTPServer);

        createFTPServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity_Main.this, DashboardActivity.class);
                i.putExtra("FTPMethod","Create");
                startActivity(i);
            }
        });
        connectFTPServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity_Main.this, DashboardActivity.class);
                i.putExtra("FTPMethod","Connect");
                startActivity(i);
            }
        });*/


    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PraDigiPath) {
            if (data != null && data.getData() != null) {
                treeUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                }
                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).edit().putString("PATH",
                        SDCardUtil.getFullPathFromTreeUri(treeUri, Activity_Main.this)).apply();
                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).edit().putString("URI",
                        String.valueOf(treeUri)).apply();
                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).edit().putBoolean("IS_SDCARD",
                        true).apply();
                PrathamApplication.setPath(PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("PATH", ""));

                Toast.makeText(this, "SD Card Selected !!!", Toast.LENGTH_SHORT).show();

                if (!PD_Utility.checkWhetherConnectedToRaspberry(Activity_Main.this)) {
                    Intent i = new Intent(Activity_Main.this, DashboardActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this, fab_share, "transition_dialog");
                    startActivityForResult(i, ACTIVITY_DOWNLOAD_METHOD, options.toBundle());
                } else {
                    onActivityResult(ACTIVITY_DOWNLOAD_METHOD, 0, null);
                }

            } else {
                Toast.makeText(this, "Please select SD Card!!!", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == ACTIVITY_LANGUAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String language = data.getStringExtra(PD_Constant.LANGUAGE);
                Log.d("language_before_insert:", language);
                db.SetUserLanguage(language, db.getGoogleID());
                Log.d("language_after_insert::", db.GetUserLanguage());
                PD_Utility.setLocale(this, db.GetUserLanguage());
                if (!db.CheckIntroShownStatus(googleId)) {
//                    ShowIntro(MY_LIBRARY);
                } else {
                    if (!isLibrary) {
//                        fab_recom.performClick();
                    } else {
                        fab_my_library.performClick();
                    }
                }
            }
        } else if (requestCode == ACTIVITY_DOWNLOAD) {
            if (resultCode == Activity.RESULT_OK) {
//                selected_content = data.getIntExtra("position", selected_content);
//                arrayList_content.remove(data.getIntExtra("position", selected_content));
//                rv_recommendAdapter.notifyItemRemoved(selected_content);
//                rv_recommendAdapter.notifyItemRangeChanged(selected_content, arrayList_content.size());
//                rv_recommendAdapter.setSelectedIndex(-1, null);
//                rv_recommendAdapter.updateData(arrayList_content);
            }
        } else if (requestCode == ACTIVITY_SEARCH) {
            fab_my_library.performClick();
        } else if (requestCode == ACTIVITY_DOWNLOAD_METHOD) {
            showDialog();
            new PD_ApiRequest(Activity_Main.this, Activity_Main.this).
                    getContentFromRaspberry("CONTENT_HEADER",
                            PD_Constant.RASP_IP + "/api/contentnode?content_id=f9da12749d995fa197f8b4c0192e7b2c");
        } else if (requestCode == SDCardLocationChooser) {
            Uri treeUri = data.getData();
            String path = SDCardUtil.getFullPathFromTreeUri(treeUri, Activity_Main.this);
            Log.d("SDCardPath :::", path);
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            try {
                // check path is correct or not
                extractToSDCard(path, treeUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // method to copy files to sd card
    private void extractToSDCard(String path, final Uri treeUri) {
        String base_path = FileUtil.getExtSdCardFolder(new File(path), Activity_Main.this);

        if (base_path != null && base_path.equalsIgnoreCase(path)) {
            // SD Card Path
            Log.d("Base path :::", base_path);
            Log.d("targetPath :::", path);

            File extPraDigi = new File(base_path + "/PraDigi");
            // Check content available or not
            if (extPraDigi.exists()) {
                // Path ( Selected )
                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this)
                        .edit().putString("URI", treeUri.toString()).apply();
                PreferenceManager.getDefaultSharedPreferences(Activity_Main.this)
                        .edit().putString("PATH", path).apply();

//            new UnZipTask(CrlShareReceiveProfiles.this, shareItPath).execute();
                new CopyFiles(db, shareItPath, Activity_Main.this,
                        Activity_Main.this).execute();
            } else {
                Toast.makeText(this, "Sorry !!!\nPraDigi Content not Available !!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Alert Dialog Call itself if wrong path selected
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Main.this);
            //  alertDialogBuilder.setMessage("Keep your Tablet Sufficiently charged & Select External SD Card Path !!!");
            LayoutInflater factory = LayoutInflater.from(Activity_Main.this);
            final View view = factory.inflate(R.layout.custom_alert_box_sd_card, null);
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            startActivityForResult(intent, SDCardLocationChooser);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setTitle("Select External SD Card Location");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setView(view);
            alertDialog.show();

            Toast.makeText(Activity_Main.this, "Please Select SD Card only !!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void browserButtonClicked(int position) {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void contentButtonClicked(final int position, View holder) {
        if (isLibrary) {
            if (subContents.get(position).getNodetype().equalsIgnoreCase("Resource")) {
                if (subContents.get(position).getResourcetype().equalsIgnoreCase("Game")) {
                    if (!isPermissionGranted(Activity_Main.this, PermissionUtils.Manifest_RECORD_AUDIO)) {
                        askCompactPermissions(new String[]{PermissionUtils.Manifest_RECORD_AUDIO}, new PermissionResult() {
                            @Override
                            public void permissionGranted() {
                                openGameInWebView(subContents.get(position));
                            }

                            @Override
                            public void permissionDenied() {
                            }

                            @Override
                            public void permissionForeverDenied() {
                                TastyToast.makeText(getApplicationContext(), getString(R.string.provide_audio_permission), TastyToast.LENGTH_LONG,
                                        TastyToast.WARNING);
                            }
                        });
                    } else {
                        openGameInWebView(subContents.get(position));
                    }
                } else if (subContents.get(position).getResourcetype().equalsIgnoreCase("Pdf")) {
                    Intent intent = new Intent(Activity_Main.this, Activity_PdfViewer.class);
                    //TODO change path
                    File directory = Activity_Main.this.getDir("PrathamPdf", Context.MODE_PRIVATE);
                    if (directory == null || directory.listFiles().length == 0) {
                        String path = "";
                        // Check folder exists on Internal
                        File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
                        if (intPradigi.exists()) {
                            // Data found on Internal Storage
                            path = Environment.getExternalStorageDirectory() + "/PraDigi/app_PrathamPdf";
                        }
                        // Check extSDCard present or not
                        else if (hasRealRemovableSdCard(Activity_Main.this)) {
                            // SD Card Available
                            // SD Card Path
                            String uri = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", "");
                            DocumentFile pickedDir = DocumentFile.fromTreeUri(Activity_Main.this, Uri.parse(uri));
                            DocumentFile tmp = pickedDir.findFile("PraDigi");
                            DocumentFile tmp1 = tmp.findFile("app_PrathamPdf");
//                        DocumentFile tmp2 = tmp1.findFile(subContents.get(position).getResourcepath());
                            path = SDCardUtil.getRealPathFromURI(Activity_Main.this, tmp1.getUri());
                            if (path == null) {
                                path = SDCardUtil.getFullPathFromTreeUri(pickedDir.getUri(), Activity_Main.this) + "/PraDigi/app_PrathamPdf";
                            }
                        } else {
                            // Data Not Available anywhere
                            finish();
                        }
                        directory = new File(path);
                    }
                    Log.d("game_filepath:::", directory.getAbsolutePath() + "/" + subContents.get(position).getNodekeywords() + ".pdf");
                    intent.putExtra("pdfPath", "file:///" + directory.getAbsolutePath() + "/" + subContents.get(position).getNodekeywords() + ".pdf");
                    intent.putExtra("pdfTitle", subContents.get(position).getNodetitle());
                    intent.putExtra("resId", subContents.get(position).getResourceid());
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                            holder, "transition_pdf");
                    Runtime rs = Runtime.getRuntime();
                    rs.freeMemory();
                    rs.gc();
                    rs.freeMemory();
                    startActivityForResult(intent, ACTIVITY_PDF, options.toBundle());
                } else if (subContents.get(position).getResourcetype().equalsIgnoreCase("Video")) {
                    Intent intent = new Intent(Activity_Main.this, Activity_GenericVPlayer.class);
                    //TODO change path
                    File directory = Activity_Main.this.getDir("PrathamVideo", Context.MODE_PRIVATE);
                    if (directory == null || directory.listFiles().length == 0) {
                        String path = "";
                        // Check folder exists on Internal
                        File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
                        if (intPradigi.exists()) {
                            // Data found on Internal Storage
                            path = Environment.getExternalStorageDirectory() + "/PraDigi/app_PrathamVideo";
                        }
                        // Check extSDCard present or not
                        else if (hasRealRemovableSdCard(Activity_Main.this)) {
                            // SD Card Available
                            // SD Card Path
                            String uri = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", "");
                            DocumentFile pickedDir = DocumentFile.fromTreeUri(Activity_Main.this, Uri.parse(uri));
                            DocumentFile tmp = pickedDir.findFile("PraDigi");
                            DocumentFile tmp1 = tmp.findFile("app_PrathamVideo");
                            path = SDCardUtil.getRealPathFromURI(Activity_Main.this, tmp1.getUri());
                            if (path == null) {
                                path = SDCardUtil.getFullPathFromTreeUri(pickedDir.getUri(), Activity_Main.this) + "/PraDigi/app_PrathamVideo";
                            }
                        } else {
                            // Data Not Available anywhere
                            finish();
                        }
                        directory = new File(path);
                    }
                    Log.d("game_filepath:::", directory.getAbsolutePath() + "/" + subContents.get(position).getNodekeywords() + ".mp4");
                    intent.putExtra("videoPath", "file:///" + directory.getAbsolutePath() + "/" + subContents.get(position).getNodekeywords() + ".mp4");
                    intent.putExtra("videoTitle", subContents.get(position).getNodetitle());
                    intent.putExtra("resId", subContents.get(position).getResourceid());
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
//                            holder, "transition_video");
                    Runtime rs = Runtime.getRuntime();
                    rs.freeMemory();
                    rs.gc();
                    rs.freeMemory();
                    startActivity(intent);
                }
            } else {
                ArrayList<Modal_ContentDetail> list = db.Get_Contents(PD_Constant.TABLE_CHILD, subContents.get(position).getNodeid());
                Modal_Level level = new Modal_Level();
                level.setId(subContents.get(position).getNodeid());
                level.setName(subContents.get(position).getNodetitle());
                setRecyclerLevel(level);
                subContents.clear();
                subContents.addAll(list);
                Log.d("sub_content_size::", subContents.size() + "");
                subLibraryAdapter.updateData(subContents);
            }
        } else {
            Modal_Level level = new Modal_Level();
            level.setId(subContents.get(position).getNodeid());
            level.setName(subContents.get(position).getNodetitle());
            setRecyclerLevel(level);
            String p_id = subContents.get(position).getNodeid();
            if (contentDownloading.size() > 0) {
                for (int i = 0; i < contentDownloading.size(); i++) {
                    if (!contentDownloading.get(i).getNodeid().equalsIgnoreCase(p_id)) {
                        contentDownloading.add(subContents.get(position));
                        break;
                    }
                }
            } else {
                contentDownloading.add(subContents.get(position));
            }

            showDialog();
            new PD_ApiRequest(Activity_Main.this, Activity_Main.this).
                    getContentFromRaspberry("CONTENT_CHILD",
                            PD_Constant.RASP_IP + "/api/contentnode?parent=" + p_id);

//            showChildContent(p_id);
        }
    }

    private void openGameInWebView(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(Activity_Main.this, Activity_WebView.class);
        //
        // TODO change path
        File directory = Activity_Main.this.getDir("PrathamGame", Context.MODE_PRIVATE);
        if (directory == null) {
            String path = "";
            // Check folder exists on Internal
            File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
            if (intPradigi.exists()) {
                // Data found on Internal Storage
                path = Environment.getExternalStorageDirectory() + "/PraDigi/app_PrathamGame";
            }
            // Check extSDCard present or not
            else if (hasRealRemovableSdCard(Activity_Main.this)) {
                // SD Card Available
                // SD Card Path
                String uri = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", "");
                DocumentFile pickedDir = DocumentFile.fromTreeUri(Activity_Main.this, Uri.parse(uri));
                DocumentFile tmp = pickedDir.findFile("PraDigi");
                DocumentFile tmp1 = tmp.findFile("app_PrathamGame");
                path = SDCardUtil.getRealPathFromURI(Activity_Main.this, tmp1.getUri());
                if (path == null) {
                    path = SDCardUtil.getFullPathFromTreeUri(pickedDir.getUri(), Activity_Main.this) + "/PraDigi/app_PrathamGame";
                }
            } else {
                // Data Not Available anywhere
                finish();
            }
            directory = new File(path);
//            //OLD CODE
//            String uri = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", "");
//            DocumentFile pickedDir = DocumentFile.fromTreeUri(Activity_Main.this, Uri.parse(uri));
//            DocumentFile tmp = pickedDir.findFile("PraDigi");
//            DocumentFile tmp1 = tmp.findFile("app_PrathamGame");
//            String path = SDCardUtil.getRealPathFromURI(Activity_Main.this, tmp1.getUri());
        }
        intent.putExtra("index_path", directory.getAbsolutePath() + "/" + contentDetail.getNodekeywords() + "/index.html");
        intent.putExtra("path", directory.getAbsolutePath() + "/" +
                new StringTokenizer(contentDetail.getNodekeywords() + "/index.html", "/").nextToken() + "/");
        intent.putExtra("resId", contentDetail.getResourceid());
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        startActivity(intent);
    }

    @Override
    public void levelButtonClicked(int position) {
    }

    @Override
    public void downloadClick(final int position, final View holder) {
        selected_content = position;
        if (!isPermissionsGranted(Activity_Main.this, new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    if (to_be_downloaded.size() > 2) {
                        TastyToast.makeText(getApplicationContext(), getString(R.string.let_them_download), TastyToast.LENGTH_LONG,
                                TastyToast.WARNING);
                    } else {
                        rv_recommendAdapter.reveal(holder);
                        to_be_downloaded.add(subContents.get(position));
                        subContents.get(position).setDownloading(true);
                        rv_recommendAdapter.setSelectedIndex(position, null);
                        if (to_be_downloaded.size() == 1) {
                            Log.d("pressed::", "again");
                            notifySuccess("DOWNLOAD", "");
                        }
                    }
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                    Toast.makeText(Activity_Main.this, "Provide Permission for storage first", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            if (to_be_downloaded.size() > 3) {
                TastyToast.makeText(getApplicationContext(), getString(R.string.let_them_download), TastyToast.LENGTH_LONG,
                        TastyToast.WARNING);
            } else {
                rv_recommendAdapter.reveal(holder);
                to_be_downloaded.add(subContents.get(position));
                subContents.get(position).setDownloading(true);
                rv_recommendAdapter.setSelectedIndex(position, null);
                if (to_be_downloaded.size() == 1) {
                    Log.d("pressed::", "again");
                    notifySuccess("DOWNLOAD", "");
                }
            }
        }
    }

    @Override
    public void downloadComplete(int position) {
    }

    @Override
    public void onContentDelete(final int position) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance
                        String parentId = subContents.get(position).getParentid();
                        String substr = subContents.get(position).getNodekeywords();
                        String nodeId = subContents.get(position).getNodeid();
                        deleteResource(subContents.get(position).getResourcetype(), substr, nodeId, parentId);
                        sDialog.setTitleText("Deleted!")
                                .setContentText("Your imaginary file has been deleted!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        fab_my_library.performClick();
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }

    private void deleteResource(String resourcetype, String resourcePath, String nodeId, String parentId) {
        if (!resourcetype.isEmpty()) {
            //TODO change path
            File dir = getDir("Pratham" + resourcetype, Context.MODE_PRIVATE);
            if (dir == null || dir.listFiles().length == 0) {
                String path = "";
                // Check folder exists on Internal
                File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
                if (intPradigi.exists()) {
                    // Data found on Internal Storage
                    path = Environment.getExternalStorageDirectory() + "/PraDigi/app_Pratham" + resourcetype;
                }
                // Check extSDCard present or not
                else if (hasRealRemovableSdCard(Activity_Main.this)) {
                    // SD Card Available
                    // SD Card Path
                    String uri = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", "");

                    DocumentFile pickedDir = DocumentFile.fromTreeUri(Activity_Main.this, Uri.parse(uri));
                    DocumentFile tmp = pickedDir.findFile("PraDigi");
                    DocumentFile tmp1 = tmp.findFile("app_PrathamGame");
//                        DocumentFile tmp2 = tmp1.findFile(subContents.get(position).getResourcepath());
                    path = SDCardUtil.getRealPathFromURI(Activity_Main.this, tmp1.getUri());
                    if (path == null) {
                        path = SDCardUtil.getFullPathFromTreeUri(pickedDir.getUri(), Activity_Main.this) + "/PraDigi/app_PrathamGame";
                    }
                } else {
                    // Data Not Available anywhere
                    finish();
                }

                /*// OLD CODE
                String uri = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this).getString("URI", "");
                DocumentFile pickedDir = DocumentFile.fromTreeUri(Activity_Main.this, Uri.parse(uri));
                DocumentFile tmp = pickedDir.findFile("PraDigi");
                DocumentFile tmp1 = tmp.findFile("app_PrathamGame");
//                        DocumentFile tmp2 = tmp1.findFile(subContents.get(position).getResourcepath());
                String path = SDCardUtil.getRealPathFromURI(Activity_Main.this, tmp1.getUri());
                */
                dir = new File(path);
            }
            File file;
            if (resourcetype.equalsIgnoreCase("Video")) {
                file = new File(dir.getAbsolutePath() + "/" + resourcePath + ".mp4");
            } else if (resourcetype.equalsIgnoreCase("Pdf")) {
                file = new File(dir.getAbsolutePath() + "/" + resourcePath + ".pdf");
            } else {
                file = new File(dir.getAbsolutePath() + "/" + resourcePath);
            }
            Log.d("dir_path2::", file.getAbsolutePath());
            boolean deleted = deleteDir(file);
            if (deleted) {
                db.deleteContentFromChild(nodeId);
                checkAndDeleteParent(parentId);
            }
            Log.d("dir_path_deleted::", "" + deleted);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                Log.d("dir_child_path::", new File(dir, children[i]).getAbsolutePath());
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    private void checkAndDeleteParent(String parentId) {
        int count = db.Get_Total_Contents(parentId);
        Log.d("remaining::", "" + count);
        if (count == 0) {
            String p_id = db.getParentID(parentId); //Getting the parentId of the parent node
            db.deleteContentFromChild(parentId);
            if (!p_id.equalsIgnoreCase("-1")) {
                checkAndDeleteParent(p_id);
            }
        }
    }

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            Gson gson = new Gson();
            if (requestType.equalsIgnoreCase("BROWSE")) {
                arrayList_content.clear();
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                arrayList_content = gson.fromJson(response, listType);
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + arrayList_content.size());
                //retrieving ids of downloaded contents from database
                arrayList_content = removeContentIfDownloaded(arrayList_content);
                arrayList_content = checkIfAlreadyDownloading(arrayList_content);
                //inflating the recommended content recycler view
                if (arrayList_content.size() > 0) {
                    content_rv.setVisibility(View.VISIBLE);
                    rl_no_content.setVisibility(View.GONE);
                    if (rv_recommendAdapter == null) {
                        rv_recommendAdapter = new RV_RecommendAdapter(Activity_Main.this, this, arrayList_content);
                        content_rv.setAdapter(rv_recommendAdapter);
                    } else {
                        content_rv.setAdapter(rv_recommendAdapter);
                        content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
                        rv_recommendAdapter.updateData(arrayList_content);
                        Log.d("content_size::", arrayList_content.size() + "");
                    }
                } else {
                    content_rv.setVisibility(View.GONE);
                    rl_no_content.setVisibility(View.VISIBLE);
                }
            } else if (requestType.equalsIgnoreCase("DOWNLOAD")) {
//                contentDownloading.clear();
                contentDownloading.add(to_be_downloaded.get(0));
//                getContentParents(to_be_downloaded.get(0).getParentid());
                String url = to_be_downloaded.get(0).getResourcepath();
                String fileName = to_be_downloaded.get(0).getResourcepath().substring(
                        to_be_downloaded.get(0).getResourcepath().lastIndexOf('/') + 1);
                String foldername = to_be_downloaded.get(0).getResourcetype();
                PD_Utility.DEBUG_LOG(1, TAG, "filename:::" + fileName + " foldername:::" + foldername);
                PowerManager pm = (PowerManager) Activity_Main.this.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                if (fileName.length() > 0) {
                    main_download_title.setText("Downloading...\n" + to_be_downloaded.get(0).getNodetitle());
                    main_fab_download.setDownloading(true);
                    main_fab_download.setClickable(false);
                    main_rl_download.setVisibility(View.VISIBLE);
                    main_rl_download.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_scale_up));
                    new ZipDownloader(Activity_Main.this, Activity_Main.this,
                            url, foldername, fileName, wl);
                }
            } else if (requestType.equalsIgnoreCase("SCORE")) {
                // Reset sentFlag to 1 if Pushed
                Log.d("success:::", "score");
                Toast.makeText(Activity_Main.this, "Data Pushed successfully", Toast.LENGTH_SHORT).show();
                resetSentFlag();
                if (dialog != null)
                    dialog.dismiss();
            } else if (requestType.equalsIgnoreCase("CONTENT_CHILD")) {
                // Reset sentFlag to 1 if Pushed
                subContents.clear();
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Content>>() {
                }.getType();
                List<Modal_Rasp_Content> rasp_contents = gson.fromJson(response, listType);
                for (Modal_Rasp_Content modal_rasp_content : rasp_contents) {
                    subContents.add(modal_rasp_content.setContentToConfigNodeStructure(modal_rasp_content));
                }
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + subContents.size());
                subContents = removeContentIfDownloaded(subContents);
                subContents = checkIfAlreadyDownloading(subContents);
                if (subContents.size() > 0) {
                    content_rv.setVisibility(View.VISIBLE);
                    rl_no_content.setVisibility(View.GONE);
                    if (rv_recommendAdapter == null) {
                        rv_recommendAdapter = new RV_RecommendAdapter(Activity_Main.this,
                                Activity_Main.this, subContents);
                        content_rv.setAdapter(rv_recommendAdapter);
                    } else {
                        content_rv.setAdapter(rv_recommendAdapter);
                        content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
                        rv_recommendAdapter.updateData(subContents);
                        Log.d("content_size::", arrayList_content.size() + "");
                    }
                } else {
                    content_rv.setVisibility(View.GONE);
                    rl_no_content.setVisibility(View.VISIBLE);
                }

                if (dialog != null)
                    dialog.dismiss();
            } else if (requestType.equalsIgnoreCase("CONTENT_HEADER")) {
                arrayList_content.clear();
                isLibrary = false;
                gallery_rv.setVisibility(View.VISIBLE);
                rl_no_data.setVisibility(View.GONE);
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Content>>() {
                }.getType();
                List<Modal_Rasp_Content> rasp_contents = gson.fromJson(response, listType);
                for (Modal_Rasp_Content modal_rasp_content : rasp_contents) {
                    arrayList_content.add(modal_rasp_content.setContentToConfigNodeStructure(modal_rasp_content));
                }
                contentDownloading.addAll(arrayList_content);
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + arrayList_content.size());
//                arrayList_content = removeContentIfDownloaded(arrayList_content);
//                arrayList_content = checkIfAlreadyDownloading(arrayList_content);
                ArrayList<String> temp_age = new ArrayList<>();
                ArrayList<String> temp_parent = new ArrayList<>();
                for (int i = 0; i < arrayList_content.size(); i++) {
                    if (arrayList_content.get(i).getParentid() == null) {
                        temp_age.add(arrayList_content.get(i).getNodetitle());
                        temp_parent.add(arrayList_content.get(i).getNodeid());
                    }
                }
                age = new String[temp_age.size()];
                parent_ids = new String[temp_parent.size()];
                for (int i = 0; i < temp_age.size(); i++) {
                    age[i] = temp_age.get(i);
                    parent_ids[i] = temp_parent.get(i);
                }
                ageFilterAdapter = new RV_AgeFilterAdapter(this, this, age, childs);
                gallery_rv.setAdapter(ageFilterAdapter);
                Modal_Level level = new Modal_Level();
                level.setId(parent_ids[0]);
                level.setName(age[0]);
                setRecyclerLevel(level);
//                if (dialog != null)
//                    dialog.dismiss();
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

//    private void getContentParents(String parentid) {
//        for (Modal_ContentDetail contentDetail : arrayList_content) {
//            if (parentid.equalsIgnoreCase(contentDetail.getNodeid())) {
//                contentDownloading.add(contentDetail);
//                if (contentDetail.getParentid() != null) {
//                    getContentParents(contentDetail.getParentid());
//                }
//                break;
//            }
//        }
//    }

    private void resetSentFlag() {
        // Reset Sent Flag to 1 if Pushed
        try {
            DatabaseHandler sdb = new DatabaseHandler(Activity_Main.this);
            sdb.updateSentFlag();
            Log.d("FlagStatus :::", "Data Sent hence flag set to 1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Modal_ContentDetail> checkIfAlreadyDownloading(ArrayList<Modal_ContentDetail> arrayList_content) {
        if (to_be_downloaded.size() > 0) {
            Log.d("to_be_downloaded::", to_be_downloaded.size() + "");
            for (int i = 0; i < to_be_downloaded.size(); i++) {
                for (int j = 0; j < arrayList_content.size(); j++) {
                    if (arrayList_content.get(j).getResourceid().equalsIgnoreCase(to_be_downloaded.get(i).getResourceid())) {
                        Log.d("to_be_downloaded::", "downloadeding content");
                        arrayList_content.get(j).setDownloading(true);
                    }
                }
            }
        }
        return arrayList_content;
    }

    private ArrayList<Modal_ContentDetail> removeContentIfDownloaded(ArrayList<Modal_ContentDetail> arrayList_content) {
        ArrayList<String> downloaded_ids = db.getDownloadContentID(PD_Constant.TABLE_CHILD);
        if (downloaded_ids.size() > 0) {
            Log.d("contents_downloaded::", downloaded_ids.size() + "");
            for (int i = 0; i < downloaded_ids.size(); i++) {
                for (int j = 0; j < arrayList_content.size(); j++) {
                    if (arrayList_content.get(j).getNodetype().equalsIgnoreCase("Resource")) {
                        if (String.valueOf(arrayList_content.get(j).getNodeid()).equalsIgnoreCase(downloaded_ids.get(i))) {
                            Log.d("contents_downloaded::", "downloaded content removed");
                            arrayList_content.remove(j);
                        }
                    }
                }
            }
        }
        return arrayList_content;
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("response:::", "requestType:: " + requestType);
            Log.d("error:::", error.toString());
            if (requestType.equalsIgnoreCase("BROWSE")) {
                content_rv.setVisibility(View.GONE);
                rl_no_content.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        Log.d("update:::", "called");
        updateInternetConnection();
    }

    private void updateInternetConnection() {
        if (!isLibrary) {
            if (!PD_Utility.isInternetAvailable(Activity_Main.this)) {
                content_rv.setVisibility(View.GONE);
                rl_no_content.setVisibility(View.GONE);
                rl_no_internet.setVisibility(View.VISIBLE);
                AnimatedVectorDrawable avd = (AnimatedVectorDrawable)
                        Activity_Main.this.getDrawable(R.drawable.avd_no_connection);
                img_no_net.setImageDrawable(avd);
                Drawable animation = img_no_net.getDrawable();
                if (animation instanceof Animatable) {
                    ((Animatable) animation).start();
                }
            } else {
                content_rv.setVisibility(View.VISIBLE);
                rl_no_internet.setVisibility(View.GONE);
            }
        } else {
            content_rv.setVisibility(View.VISIBLE);
            rl_no_internet.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
//        if (dialog == null)
        dialog = PD_Utility.showLoader(Activity_Main.this);
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
//        stopLocationUpdates();
        NetworkChangeReceiver.getObservable().deleteObserver(this);
    }

    @Override
    protected void onDestroy() {
        Log.d("Destroyed Score Entry", "Destroyed Score Entry");
        if (ttspeech != null) {
            ttspeech.shutDownTTS();
            Log.d("tts_destroyed", "TTS Destroyed");
        }
        super.onDestroy();
    }

    @Override
    public void onProgressUpdate(int progress) {
        Log.d("onProgressUpdate::", "" + progress);
        main_fab_download.setProgress((float) (progress * .01));
        if (progress == 100) {
            main_download_title.setText(R.string.please_wait);
            main_fab_download.setDownloading(true);
        }
    }

    @Override
    public void onZipDownloaded(boolean isDownloaded) {
        if (isDownloaded) {
            for (Modal_ContentDetail downloadedContent : contentDownloading) {
                if (downloadedContent.getNodeserverimage() != null) {
                    String filename = downloadedContent.getNodeserverimage()
                            .substring(downloadedContent.getNodeserverimage().lastIndexOf('/') + 1);
                    new ImageDownload(Activity_Main.this, filename).execute(downloadedContent.getNodeserverimage());
                }
            }
        } else {
            main_rl_download.startAnimation(AnimationUtils.loadAnimation(Activity_Main.this, R.anim.fab_scale_down));
            main_rl_download.setVisibility(View.GONE);
            TastyToast.makeText(getApplicationContext(), getString(R.string.something_went_wrong), TastyToast.LENGTH_LONG,
                    TastyToast.ERROR);
            to_be_downloaded.remove(0);
        }
//        new ZipDownloader(context, progressUpdate, directoryPath + "/" + filename, foldername);
    }

    @Override
    public void onZipExtracted(boolean isExtracted) {
        getParentsAndAddToDB(contentDownloading, to_be_downloaded.get(0).getNodeid(), null);
        if (isExtracted) {
            for (int i = 0; i < subContents.size(); i++) {
                if (subContents.get(i).getNodeid() == to_be_downloaded.get(0).getNodeid()) {
                    subContents.remove(i);
                    rv_recommendAdapter.notifyItemRemoved(i);
                    rv_recommendAdapter.notifyItemRangeChanged(i, subContents.size());
                    rv_recommendAdapter.setSelectedIndex(-1, null);
                    rv_recommendAdapter.updateData(subContents);
                    break;
                }
            }
            to_be_downloaded.remove(0);
            if (to_be_downloaded.size() > 0) {
                notifySuccess("DOWNLOAD", "");
            } else {
                main_rl_download.startAnimation(AnimationUtils.loadAnimation(Activity_Main.this, R.anim.fab_scale_down));
                main_rl_download.setVisibility(View.GONE);
            }
        }
    }

    private void getParentsAndAddToDB(ArrayList<Modal_ContentDetail> download, String nodeid
            , ArrayList<Modal_ContentDetail> parentsContent) {
        if (parentsContent == null) {
            parentsContent = new ArrayList<>();
        }
        for (int i = 0; i < download.size(); i++) {
            if (download.get(i).getNodeid().equalsIgnoreCase(nodeid)) {
                parentsContent.add(download.get(i));
                if (download.get(i).getParentid() != null) {
                    getParentsAndAddToDB(download, download.get(i).getParentid(), parentsContent);
                } else {
                    addContentToDatabase(parentsContent);
                }
                break;
            }
        }
    }

    private void addContentToDatabase(ArrayList<Modal_ContentDetail> download_content) {
        ArrayList<String> p_ids = db.getDownloadContentID(PD_Constant.TABLE_PARENT);
        ArrayList<String> c_ids = db.getDownloadContentID(PD_Constant.TABLE_CHILD);
        for (int i = 0; i < download_content.size(); i++) {
            if (download_content.get(i).getParentid() == null) {
                if (!p_ids.contains(download_content.get(i).getNodeid()))
                    db.Add_Content(PD_Constant.TABLE_PARENT, download_content.get(i));
            } else {
                if (!c_ids.contains(download_content.get(i).getNodeid()))
                    db.Add_Content(PD_Constant.TABLE_CHILD, download_content.get(i));
            }
        }
    }

    @Override
    public void lengthOfTheFile(int length) {
    }

    int back = 0;

    @Override
    public void onBackPressed() {
        if (back == 0) {
            TastyToast.makeText(getApplicationContext(), getString(R.string.press_again_to_exit), TastyToast.LENGTH_LONG,
                    TastyToast.SUCCESS);
            back += 1;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void levelClicked(final int position) {
        if (isLibrary) {
            ArrayList<Modal_ContentDetail> list = db.Get_Contents(PD_Constant.TABLE_CHILD, arrayList_level.get(position).getId());
            arrayList_level.subList(position + 1, arrayList_level.size()).clear();
            subContents.clear();
            subContents.addAll(list);
            Log.d("sub_content_size::", subContents.size() + "");
            subLibraryAdapter.updateData(subContents);
        } else {
            String p_id = arrayList_level.get(position).getId();
            arrayList_level.subList(position + 1, arrayList_level.size()).clear();
            showDialog();
            new PD_ApiRequest(Activity_Main.this, Activity_Main.this).
                    getContentFromRaspberry("CONTENT_CHILD",
                            PD_Constant.RASP_IP + "/api/contentnode?parent=" + p_id);
        }
    }

    private void showChildContent(String p_id) {
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }
}
