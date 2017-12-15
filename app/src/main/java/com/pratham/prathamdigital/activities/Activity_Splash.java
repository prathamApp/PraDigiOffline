package com.pratham.prathamdigital.activities;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.Interface_Level;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.GoogleCredentials;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Activity_Splash extends ActivityManagePermission implements GoogleApiClient.OnConnectionFailedListener,
        VolleyResult_JSON, Interface_Level {

    @BindView(R.id.btn_google_login)
    Button btn_google_login;
    @BindView(R.id.img_logo)
    ImageView img_logo;

    DatabaseHandler gdb;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 46;
    private Animation animeWobble;
    String personPhotoUrl = "", email = "", personName = "", googleId = "";
    private boolean isInitialized = false;
    private AlertDialog dialog;
    private GoogleCredentials gObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity__splash);
        ButterKnife.bind(this);
        // Database Initialization
        dialog = PD_Utility.showLoader(this);
        gdb = new DatabaseHandler(this);
        isInitialized = false;
        //mergeDB();

//        copyFilesToRoot();
    }

//    // Update Media
//    private void chooseFile() {
//        DialogProperties properties = new DialogProperties();
//        properties.selection_mode = DialogConfigs.SINGLE_MODE;
//        properties.selection_type = DialogConfigs.FILE_SELECT;
//        properties.root = new File("/");
//        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
//        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
//        properties.extensions = null;
//        FilePickerDialog dialog = new FilePickerDialog(Activity_Splash.this, properties);
//        dialog.setTitle("Select a File");
//        dialog.setDialogSelectionListener(new DialogSelectionListener() {
//            @Override
//            public void onSelectedFilePaths(String[] files) {
//                //files is the array of the paths of files selected by the Application User.
//                Log.d("path:::", files[0]);
//                if (files[0].endsWith("content.zip")) {
//                    new CopyTask(Activity_Splash.this, files[0], Activity_Splash.this);
//                } else {
//                    Intent activityChangeIntent = new Intent(Activity_Splash.this, Activity_Main.class);
//                    startActivity(activityChangeIntent);
//                    overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
//                    finish();
//                }
//            }
//        });
//        dialog.show();
//    }

    private void checkVersion() {
//        String latestVersion = "";
//        String currentVersion = PD_Utility.getCurrentVersion(Activity_Splash.this);
//        Log.d("version::", "Current version = " + currentVersion);
//        try {
//            latestVersion = new GetLatestVersion().execute().get();
//            Log.d("version::", "Latest version = " + latestVersion);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        // Force Update Code
//        if ((!currentVersion.equals(latestVersion)) && latestVersion != null) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Upgrade to a better version !");
//            builder.setCancelable(false);
//            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //Click button action
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.pratham.prathamdigital")));
//                    dialog.dismiss();
//                }
//            });
//            builder.show();
//        } else {
        if (!isInitialized) {
//                if (gdb.getGoogleID().equalsIgnoreCase("")) {
            insertGoogleData();
//                } else {
            updateUI(true, null);
//                }
            isInitialized = true;
        }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
    }

    private void insertGoogleData() {
        gObj = new GoogleCredentials();
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        gObj.GoogleID = deviceId.equals(null) ? "0000" : deviceId;
        gObj.Email = "";
        gObj.PersonName = "";
        gObj.IntroShown = 0;
        gObj.languageSelected = "Hindi";
//        Map<String, Object> params = new HashMap<>();
//        params.put(PD_Constant.GOOGLE_ID, deviceId.equals(null) ? "0000" : deviceId);
//        params.put(PD_Constant.EMAIL, "");
//        params.put(PD_Constant.PERSON_NAME, "");
//        params.put(PD_Constant.LANG, gObj.languageSelected);
//        final JSONObject jsonObject = new JSONObject(params);
//        Log.d("google_data::", jsonObject.toString());
//        if (PD_Utility.isInternetAvailable(Activity_Splash.this)) {
//            showDialog();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    new PD_ApiRequest(Activity_Splash.this, Activity_Splash.this).postDataVolley(Activity_Splash.this,
//                            "POST", PD_Constant.URL.POST_GOOGLE_DATA.toString(), jsonObject);
//                }
//            }, 2000);
//        } else {
//            Toast.makeText(Activity_Splash.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
//        }
        gdb.insertNewGoogleUser(gObj);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("result::", result.toString());
        Log.d("result::", result.isSuccess() + "");
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            Log.e("display name: ", account.getDisplayName());
            if (account.getId() != null) {
                googleId = account.getId();
            }
            if (account.getDisplayName() != null) {
                personName = account.getDisplayName();
            }
            if (account.getPhotoUrl() != null) {
                personPhotoUrl = account.getPhotoUrl().toString();
            }
            if (account.getEmail() != null) {
                email = account.getEmail();
            }
            Log.e("details:::", "Name: " + personName + ", Email: " + email + ", GoogleId: " + googleId);
            if ((personName.length() > 0) && (email.length() > 0) && (googleId.length() > 0)) {
                boolean userExists = gdb.CheckGoogleLogin(googleId);
                if (!userExists) {
                    gObj = new GoogleCredentials();
                    gObj.GoogleID = googleId;
                    gObj.Email = email;
                    gObj.PersonName = personName;
                    gObj.IntroShown = 0;
                    gObj.languageSelected = "Hindi";
                    Map<String, Object> params = new HashMap<>();
                    params.put(PD_Constant.GOOGLE_ID, googleId);
                    params.put(PD_Constant.EMAIL, email);
                    params.put(PD_Constant.PERSON_NAME, personName);
                    params.put(PD_Constant.LANG, gObj.languageSelected);
                    final JSONObject jsonObject = new JSONObject(params);
                    Log.d("google_data::", jsonObject.toString());
                    if (PD_Utility.isInternetAvailable(Activity_Splash.this)) {
                        showDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new PD_ApiRequest(Activity_Splash.this, Activity_Splash.this).postDataVolley(Activity_Splash.this,
                                        "POST", PD_Constant.URL.POST_GOOGLE_DATA.toString(), jsonObject);
                            }
                        }, 2000);
                    } else {
                        Toast.makeText(Activity_Splash.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void showDialog() {
        if (dialog == null)
            dialog = PD_Utility.showLoader(Activity_Splash.this);
        dialog.show();
    }

    private void updateUI(boolean isSignedIn, final Bundle bundle) {
        if (isSignedIn) {
//            PD_Utility.showLoader(Activity_Splash.this);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {

//                }
//            }, 3000);

//            // OLD CODE ABHISHEK CHOOSE FILE
//            final boolean shown = PreferenceManager.getDefaultSharedPreferences(Activity_Splash.this).getBoolean("CONTENT", false);
//            if (!shown) {
//                if (!isPermissionsGranted(Activity_Splash.this, new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
//                        PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
//                    askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
//                            PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
//                        @Override
//                        public void permissionGranted() {
//                            chooseFile();
//                        }
//
//                        @Override
//                        public void permissionDenied() {
//                        }
//
//                        @Override
//                        public void permissionForeverDenied() {
//                        }
//                    });
//                } else {
//                    // Choose content.zip file containing data
//                    chooseFile();
//                }
//            } else {
                Intent activityChangeIntent = new Intent(Activity_Splash.this, Activity_Main.class);
                startActivity(activityChangeIntent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
                finish();
            //}
        } else {
            btn_google_login.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            // Toast.makeText(Activity_Splash.this, "Welcome " + personName, Toast.LENGTH_SHORT).show();
            updateUI(true, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("response:::", "requestType:: " + requestType);
            Log.d("error:::", error.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void levelClicked(int position) {
        PreferenceManager.getDefaultSharedPreferences(Activity_Splash.this)
                .edit().putBoolean("CONTENT", true).apply();
        Intent activityChangeIntent = new Intent(Activity_Splash.this, Activity_Main.class);
        startActivity(activityChangeIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
        finish();
    }

    private void mergeDB() {
        try {
            File dbf = getDir("new_databases", MODE_PRIVATE);
            Log.d("db_path::", dbf.getAbsolutePath());
            SQLiteDatabase db = SQLiteDatabase.openDatabase
                    (dbf.getAbsolutePath().replace("app_new_databases","new_databases")
                            + "/PrathamDB", null, 0);
            SQLiteDatabase myInternalDatabase = gdb.getWritableDatabase();
            Log.d("sql::","INSERT INTO table_parent_content SELECT * FROM "
                    + db.getPath() + ".table_parent_content;");
            myInternalDatabase.execSQL("INSERT INTO table_parent_content SELECT * FROM PrathamDB.table_parent_content");
            Log.d("db_path::", dbf.getAbsolutePath());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class GetLatestVersion extends AsyncTask<String, String, String> {
        String latestVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=com.pratham.prathamdigital";
                // Document doc = w3cDom.fromJsoup(Jsoup.connect(urlOfAppFromPlayStore).get());
                //Log.d(TAG,"playstore doc "+getStringFromDoc(doc));
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.pratham.prathamdigital" + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                Log.d("latest::", latestVersion);
                //latestVersion = doc.getElementsByTagName("softwareVersion").first().text();
                //latestVersion = "1.5";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return latestVersion;
        }
    }
}
