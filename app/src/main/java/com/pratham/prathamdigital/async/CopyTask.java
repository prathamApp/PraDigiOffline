package com.pratham.prathamdigital.async;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.pratham.prathamdigital.interfaces.Interface_Level;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.UnzipUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by User on 16/11/15.
 */
public class CopyTask {

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private final File mydir;
    ProgressUpdate progressUpdate;
    Context context;
    private String destString;
    private String source;
    private File fileWithinMyDir;
    ProgressDialog dialog;
    Interface_Level level;

    public CopyTask(Context context, String foldername, Interface_Level level) {
        this.context = context;
        this.progressUpdate = progressUpdate;
        this.level=level;
        source=foldername;
        mydir = context.getDir("databases", Context.MODE_PRIVATE); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
        destString=mydir.getAbsolutePath().replace("app_databases","");
        Log.d("internal_file", destString);
        new DownloadZipfile().execute();

    }

    class DownloadZipfile extends AsyncTask<String, String, String> {
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            wakeLock.acquire();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Copying...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                File destinationFile = new File(destString,"content.zip");
                Log.d("destinationFile::", destinationFile.getAbsolutePath());
                FileInputStream fileInputStream = new FileInputStream(source);
                FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);

                int bufferSize;
                byte[] bufffer = new byte[512];
                while ((bufferSize = fileInputStream.read(bufffer)) > 0) {
                    fileOutputStream.write(bufffer, 0, bufferSize);
                    Log.d("copying::","....");
                }
                fileInputStream.close();
                fileOutputStream.close();
                System.out.println("download done");
                result = "true";
            } catch (Exception e) {
                e.printStackTrace();
                result = "false";
            }
            return result;
        }

//        protected void onProgressUpdate(String... progress) {
//            Log.d("ANDRO_ASYNC", progress[0]);
//            progressUpdate.onProgressUpdate(Integer.parseInt(progress[0]));
////            builder.setProgress(100, Integer.parseInt(progress[0]), false);
////            notificationManager.notify(1000, builder.build());
//        }

        @Override
        protected void onPostExecute(String unused) {
            if (result.equalsIgnoreCase("true")) {
                try {
//                    builder.setContentText("Download complete");
//                    builder.setProgress(0, 0, false);
//                    notificationManager.notify(1000, builder.build());
//                    progressUpdate.onZipDownloaded(true);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    System.out.println("lucy download unzip");
                    unzip();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//                progressUpdate.onZipDownloaded(false);
            }
        }

    }

    public void unzip() throws IOException {
        Log.d("internal_file", source);
        new UnZipTask().execute(destString+"/content.zip", destString);
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            wakeLock.acquire();
            dialog = new ProgressDialog(context);
            dialog.setMessage("unzipping...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @SuppressWarnings("rawtypes")
        @Override
        protected Boolean doInBackground(String... params) {
            String filePath = params[0];
            String destinationPath = params[1];

            File archive = new File(filePath);
            try {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, destinationPath);
                }
                UnzipUtil d = new UnzipUtil(filePath, destinationPath);
                d.unzip();
                System.out.println("lucy download UnZipTask util done");

            } catch (Exception e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            try {
                if (dialog != null) {
                    dialog.dismiss();
                }
//                File file = new File(destString+"/content.zip");
//                boolean deleted = file.delete();
//                if (deleted) Log.d("file:::", "deleted");
//                level.levelClicked(0);
//                wakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {
            if (entry.isDirectory()) {
                createDir(new File(outputDir, "/" + entry.getName()));
                return;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists()) {
                createDir(outputFile.getParentFile());
            }

            // Log.v("", "Extracting: " + entry);
            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

            try {

            } finally {
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        }

        private void createDir(File dir) {
            if (dir.exists()) {
                return;
            }
            if (!dir.mkdirs()) {
                throw new RuntimeException("Can not create dir " + dir);
            }
        }
    }
}





