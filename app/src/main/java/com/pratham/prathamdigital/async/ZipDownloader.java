package com.pratham.prathamdigital.async;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.UnzipUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
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
public class ZipDownloader {

    private NotificationManager notificationManager;
    private File mydir;
    String filename;
    String foldername;
    ProgressUpdate progressUpdate;
    Context context;
    private File fileWithinMyDir;
    public PowerManager.WakeLock wakeLock;

    public ZipDownloader(Context context, ProgressUpdate progressUpdate, String url, String foldername,
                         String filename, PowerManager.WakeLock wl) {
        this.context = context;
        PD_Utility.DEBUG_LOG(1, "url:::", url);
        this.filename = filename;
        this.foldername = foldername;
        this.progressUpdate = progressUpdate;
        this.wakeLock = wl;
        mydir = context.getDir("Pratham" + foldername, Context.MODE_PRIVATE); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
        if (foldername.equalsIgnoreCase("Game")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
            File temp_dir = new File(mydir.getAbsolutePath() + "/" + filename);
            if (!temp_dir.exists()) temp_dir.mkdirs();
            mydir = temp_dir;
        }
        Log.d("internal_file", mydir.getAbsolutePath());
        DownloadZipfile mew = new DownloadZipfile();
        mew.execute(url);
    }

    class DownloadZipfile extends AsyncTask<String, String, String> {
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wakeLock.acquire();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                System.out.println("download");
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.setDoInput(true);
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                progressUpdate.lengthOfTheFile(lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                fileWithinMyDir = new File(mydir, filename); //Getting a file within the dir.
                OutputStream output = new FileOutputStream(fileWithinMyDir);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                System.out.println("download done");
                result = "true";
            } catch (Exception e) {
                e.printStackTrace();
                result = "false";
            }
            return result;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            progressUpdate.onProgressUpdate(Integer.parseInt(progress[0]));
//            builder.setProgress(100, Integer.parseInt(progress[0]), false);
//            notificationManager.notify(1000, builder.build());
        }

        @Override
        protected void onPostExecute(String unused) {
            if (result.equalsIgnoreCase("true")) {
                try {
                    progressUpdate.onZipDownloaded(true);
                    System.out.println("lucy download unzip");
                    if (foldername.equalsIgnoreCase("Game")) {
                        unzip();
                    } else {
                        progressUpdate.onZipExtracted(true);
                        wakeLock.release();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                progressUpdate.onZipDownloaded(false);
            }
        }
    }

    public void unzip() throws IOException {
        Log.d("internal_file", fileWithinMyDir.getAbsolutePath());
        new UnZipTask().execute(fileWithinMyDir.getAbsolutePath(), mydir.getAbsolutePath());
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean> {
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
                UnzipUtil d = new UnzipUtil(fileWithinMyDir.getAbsolutePath(), mydir.getAbsolutePath());
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
                File file = new File(fileWithinMyDir.getAbsolutePath());
                boolean deleted = file.delete();
                progressUpdate.onZipExtracted(true);
                if (deleted) Log.d("file:::", "deleted");
                wakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
                progressUpdate.onZipExtracted(false);
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





