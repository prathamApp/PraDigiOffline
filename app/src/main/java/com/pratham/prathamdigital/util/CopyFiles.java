package com.pratham.prathamdigital.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.ExtractInterface;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.pratham.prathamdigital.activities.Activity_Main.hasRealRemovableSdCard;


public class CopyFiles extends AsyncTask<Void, Integer, String> {

    Context context;
    File sourcePath;
    File targetPath;
    ProgressDialog dialog;
    String zipPath;
    private ExtractInterface extractInterface;
    private static final String TABLE_PARENT = "table_parent_content";
    private static final String TABLE_CHILD = "table_child_content";
    DatabaseHandler databaseHandler;

    public CopyFiles(DatabaseHandler db, String zipPath, Context context, ExtractInterface extractInterface) {
        this.context = context;
        this.zipPath = zipPath;
        this.extractInterface = extractInterface;
        this.databaseHandler = db;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Copying...");
//        dialog.setMax(100);
//        dialog.setIndeterminate(false);
//        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    @Override
    protected String doInBackground(Void... voids) {


        String path = "";
        // TODO Path Change
        // Check folder exists on Internal
        File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
        if (intPradigi.exists()) {
            // Data found on Internal Storage
            path = Environment.getExternalStorageDirectory() + "/PraDigi/databases/PrathamDB";
        }
        // Check extSDCard present or not
        else if (hasRealRemovableSdCard(context)) {
            // SD Card Available
            // SD Card Path
            String uri = PreferenceManager.getDefaultSharedPreferences(context).getString("URI", "");
            DocumentFile pickedDir = DocumentFile.fromTreeUri(context, Uri.parse(uri));
            DocumentFile tmp = pickedDir.findFile("PraDigi");

            Log.d("tmp:::", tmp.toString());

                DocumentFile tmp1 = tmp.findFile("databases");
                DocumentFile tmp2 = tmp1.findFile("PrathamDB");

                path = SDCardUtil.getRealPathFromURI(context, tmp2.getUri());
                if (path == null) {
                    path = SDCardUtil.getFullPathFromTreeUri(pickedDir.getUri(), context) + "/PraDigi/databases/PrathamDB";
                }

        } else {
            // Data found no where
            Toast.makeText(context, "Sorry !!!\nPraDigi Content not Available !!!", Toast.LENGTH_SHORT).show();
//            // SD Card Not Available
//            path = Environment.getExternalStorageDirectory() + "/PraDigi/databases/PrathamDB";
        }


        Log.d("path::", path);
//        if (tmp != null) {
//            tmp.delete();
//        }
        File mydir = context.getDir("databases", Context.MODE_PRIVATE); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
        String destString = mydir.getAbsolutePath().replace("app_databases", "databases");
        Log.d("internal_file", destString);
        File mfile = new File(destString, "PrathamDB");
        if (mfile == null) {
            try {
//                mfile = new File(destString, "PrathamDB");
                mfile.createNewFile();
                destString = mfile.getAbsolutePath().replace("app_databases", "databases");
                Log.d("sd_path:::", destString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        destString=mfile.getAbsolutePath().replace("app_databases", "databases");
        try {
            //TODO copy database tables row by row
            File sd_db = new File(path);
            SQLiteDatabase db = SQLiteDatabase.openDatabase(sd_db.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            ArrayList<Modal_ContentDetail> contents = getContent(db, TABLE_PARENT);
            if (contents != null && contents.size() > 0) {
                for (int i = 0; i < contents.size(); i++) {
                    databaseHandler.Add_Content(PD_Constant.TABLE_PARENT, contents.get(i));
                }
            }
            ArrayList<Modal_ContentDetail> c_contents = getContent(db, TABLE_CHILD);
            if (c_contents != null && c_contents.size() > 0) {
                for (int i = 0; i < c_contents.size(); i++) {
                    databaseHandler.Add_Content(PD_Constant.TABLE_CHILD, c_contents.get(i));
                }
            }
            db.close();
//            IOUtils.copyLarge(new FileInputStream(new File(path)), new FileOutputStream(new File(destString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    protected void onProgressUpdate(Integer... values) {
//        if (dialog != null) {
//            dialog.setProgress(values[0]);
//        }
//
//    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
            // if(!zipPath.equals(null))
            extractInterface.onExtractDone(zipPath);
            //Log.d("ZipPath:::",zipPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Modal_ContentDetail> getContent(SQLiteDatabase db, String table_name) {
        ArrayList<Modal_ContentDetail> contents = new ArrayList<>();
        try {
            contents.clear();
            String selectQuery = "SELECT  * FROM " + table_name;
            Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Modal_ContentDetail contentDetail = new Modal_ContentDetail();
                    contentDetail.setNodeid(Integer.parseInt(cursor.getString(0)));
                    contentDetail.setNodetype(cursor.getString(1));
                    contentDetail.setNodetitle(cursor.getString(2));
                    contentDetail.setNodekeywords(cursor.getString(3));
                    contentDetail.setNodeeage(cursor.getString(4));
                    contentDetail.setNodedesc(cursor.getString(5));
                    contentDetail.setNodeimage(cursor.getString(6));
                    contentDetail.setNodeserverimage(cursor.getString(7));
                    contentDetail.setResourceid(cursor.getString(8));
                    contentDetail.setResourcetype(cursor.getString(9));
                    contentDetail.setResourcepath(cursor.getString(10));
                    contentDetail.setLevel(Integer.parseInt(cursor.getString(11)));
                    contentDetail.setParentid(Integer.parseInt(cursor.getString(12)));
                    // Adding contact to list
                    contents.add(contentDetail);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return contents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }
}