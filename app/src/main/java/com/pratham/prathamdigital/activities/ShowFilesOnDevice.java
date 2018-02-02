package com.pratham.prathamdigital.activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.File_Adapter;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.FolderClick;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.SDCardUtil;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import mayurmhm.mayur.ftpmodule.AndroidApplication;
//import mayurmhm.mayur.ftpmodule.R;

public class ShowFilesOnDevice extends AppCompatActivity implements FolderClick {

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private static final String TABLE_PARENT = "table_parent_content";
    private static final String TABLE_CHILD = "table_child_content";
    DatabaseHandler databaseHandler;
    DocumentFile parenturi;
    DocumentFile tempUri;
    ProgressDialog pd;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<File_Model> data = new ArrayList<File_Model>();
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems = new ArrayList<Integer>();
    FTPClient client1;
    String treeUri;
    ArrayList<Integer> level = new ArrayList<>();
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_files_on_device);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewExplorer);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        treeUri = PreferenceManager.getDefaultSharedPreferences(ShowFilesOnDevice.this).getString("URI", "");
        client1 = PrathamApplication.client1;

        if (!client1.equals(null)) {
            listFtpFiles(false, null, false);
        }
    }

    private void listFtpFiles(boolean isExpand, String name, boolean changeToParent) {
        new AsyncTask<Void, Void, FTPFile[]>() {
            @Override
            protected FTPFile[] doInBackground(Void... voids) {
                // todo get FTP Server's file's paths & details
                try {
                    if (isExpand) {
                        level.add(1);
                        client1.changeWorkingDirectory(name);
                    }
                    if (changeToParent) {
                        level.remove(level.size() - 1);
                        client1.changeToParentDirectory();
                    }
                    FTPFile[] files = client1.listFiles();
                    return files;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(FTPFile[] files) {
                super.onPostExecute(files);
                data.clear();
                for (FTPFile mfile : files) {
                    File_Model model = new File_Model();
                    model.setMfile(mfile);
                    model.setFileName(mfile.getName());
                    model.setFile(mfile.isFile());
                    data.add(model);
                }
                Log.d("data_size::", data.size() + "");
                adapter = new File_Adapter(data, ShowFilesOnDevice.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        if (level.size() > 0) {
            listFtpFiles(false, null, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDownload(int position, FTPFile name) {
        File final_file = null;
        DocumentFile finalDocumentFile = null;
        boolean isSdCard = PreferenceManager.getDefaultSharedPreferences(ShowFilesOnDevice.this).getBoolean("IS_SDCARD", false);
        if (!isSdCard) {
            String path = PreferenceManager.getDefaultSharedPreferences(ShowFilesOnDevice.this).getString("PATH", "");
            if (name.getName().equalsIgnoreCase("app_PrathamImages")) {
                File file = new File(path , "/app_PrathamImages");
                if (!file.exists())
                    file.mkdir();
                final_file=file;
            } else {
                File file = new File(path , "/app_PrathamGame");
                if (!file.exists())
                    file.mkdir();
                File child_file = new File(file,name.getName());
                if (!child_file.exists())
                    child_file.mkdir();
                final_file = child_file;
            }
        } else {
            DocumentFile documentFile = DocumentFile.fromTreeUri(ShowFilesOnDevice.this, Uri.parse(treeUri));
            if (name.getName().equalsIgnoreCase("app_PrathamImages")) {
                //check whether root folder "PraDigi" exists or not
                DocumentFile documentFile1 = documentFile.findFile("PraDigi");
                if (documentFile1 == null)
                    documentFile = documentFile.createDirectory("PraDigi");
                else
                    documentFile = documentFile1;

                //check whether sub folder folder "app_PrathamGame" exists or not
                DocumentFile documentFile2 = documentFile.findFile("app_PrathamImages");
                if (documentFile2 == null)
                    documentFile = documentFile.createDirectory("app_PrathamImages");
                else
                    documentFile = documentFile2;
            } else {
                DocumentFile documentFile1 = documentFile.findFile("PraDigi");
                if (documentFile1 == null)
                    documentFile = documentFile.createDirectory("PraDigi");
                else
                    documentFile = documentFile1;
                //check whether sub folder folder "app_PrathamGame" exists or not
                DocumentFile documentFile2 = documentFile.findFile("app_PrathamGame");
                if (documentFile2 == null)
                    documentFile = documentFile.createDirectory("app_PrathamGame");
                else
                    documentFile = documentFile2;
                //check whether downloading file exists or not
                DocumentFile documentFile3 = documentFile.findFile(name.getName());
                if (documentFile3 == null)
                    documentFile = documentFile.createDirectory(name.getName());
                else
                    documentFile = documentFile3;
            }
            finalDocumentFile = documentFile;
        }
        DocumentFile finalDocumentFile1 = finalDocumentFile;
        File final_file1 = final_file;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(ShowFilesOnDevice.this);
                pd.setMessage("Please Wait !!! Downloading ...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (name.isDirectory()) {
                    if (isSdCard)
                        downloadDirectoryToSdCard(client1, finalDocumentFile1, name);
                    else
                        downloadDirectoryToInternal(client1, final_file1, name);
                } else {
                    if (isSdCard)
                        downloadFile(client1, name, finalDocumentFile1);
                    else
                        downloadFileToInternal(client1, name, final_file1);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (pd != null)
                    pd.dismiss();
            }
        }.execute();
    }

    private void downloadDirectoryToInternal(FTPClient client1, File final_file1, FTPFile name) {
        try {
            tempFile = final_file1;
            FTPClient tempClient = client1;
            if (name != null)
                tempClient.changeWorkingDirectory(name.getName());
            FTPFile[] subFiles = tempClient.listFiles();
            Log.d("file_size::", subFiles.length + "");
            if (subFiles != null && subFiles.length > 0) {
                for (FTPFile aFile : subFiles) {
                    Log.d("name::", aFile.getName() + "");
                    String currentFileName = aFile.getName();
                    if (currentFileName.equals(".") || currentFileName.equals("..")) {
                        // skip parent directory and the directory itself
                        continue;
                    }
                    if (aFile.isDirectory()) {
                        // create the directory in saveDir
                        File file=new File(tempFile.getAbsolutePath(),currentFileName);
                        if (!file.exists())
                            file.mkdir();
                        tempClient.changeWorkingDirectory(currentFileName);
                        downloadDirectoryToInternal(client1, file, null);
                    } else {
                        downloadFileToInternal(client1, aFile, tempFile);
                    }
                }
                tempFile = tempFile.getParentFile();
                tempClient.changeToParentDirectory();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFileToInternal(FTPClient client1, FTPFile aFile, File tempFile) {
        try {
            tempFile = new File(tempFile,aFile.getName());
            Log.d("tempFile::",tempFile.getAbsolutePath());
            OutputStream outputStream = ShowFilesOnDevice.this.getContentResolver().openOutputStream(Uri.fromFile(tempFile));
            client1.setFileType(FTP.BINARY_FILE_TYPE);
            client1.retrieveFile(aFile.getName(), outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Todo copy json to database
            if (aFile.getName().endsWith(".json")) {
                Log.d("json_path:::", tempFile.getAbsolutePath() + "");
                Log.d("json_path:::", aFile.getName() + "");
                //Todo read json from file
                String response = loadJSONFromAsset(tempFile.getAbsolutePath());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Modal_DownloadContent download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                addContentToDatabase(download_content);
            }
        }
    }

    @Override
    public void onFolderClicked(int position, String name) {
        listFtpFiles(true, name, false);
    }

    String json;

    // todo put in async task downdir and file
    private void downloadDirectoryToSdCard(FTPClient ftpClient, DocumentFile documentFile, FTPFile name) {
        try {
            tempUri = documentFile;
            FTPClient tempClient = ftpClient;
            if (name != null)
                tempClient.changeWorkingDirectory(name.getName());
            FTPFile[] subFiles = tempClient.listFiles();
            Log.d("file_size::", subFiles.length + "");
            if (subFiles != null && subFiles.length > 0) {
                for (FTPFile aFile : subFiles) {
                    Log.d("name::", aFile.getName() + "");
                    String currentFileName = aFile.getName();
                    if (currentFileName.equals(".") || currentFileName.equals("..")) {
                        // skip parent directory and the directory itself
                        continue;
                    }
                    if (aFile.isDirectory()) {
                        // create the directory in saveDir
                        if (tempUri.findFile(currentFileName) == null) {
                            tempUri = tempUri.createDirectory(currentFileName);
                        }
                        tempClient.changeWorkingDirectory(currentFileName);
                        downloadDirectoryToSdCard(ftpClient, tempUri, null);
                    } else {
                        downloadFile(ftpClient, aFile, tempUri);
                    }
                }
                tempUri = tempUri.getParentFile();
                tempClient.changeToParentDirectory();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(FTPClient ftpClient, FTPFile ftpFile, DocumentFile tempFile) {
        try {
            tempFile = tempFile.createFile("image", ftpFile.getName());
            OutputStream outputStream = ShowFilesOnDevice.this.getContentResolver().openOutputStream(tempFile.getUri());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.retrieveFile(ftpFile.getName(), outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Todo copy json to database
            if (ftpFile.getName().endsWith(".json")) {
                Log.d("json_path:::", tempFile.getUri() + "");
                String path = SDCardUtil.getRealPathFromURI_API19(ShowFilesOnDevice.this, tempFile.getUri());
                Log.d("json_path:::", path + "");
                Log.d("json_path:::", ftpFile.getName() + "");
                //Todo read json from file
                String response = loadJSONFromAsset(path);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                Modal_DownloadContent download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                addContentToDatabase(download_content);
            }
        }
    }

    // Reading Json From Internal Storage
    public String loadJSONFromAsset(String path) {
        String JsonStr = null;

        try {
            File queJsonSDCard = new File(path);
            FileInputStream stream = new FileInputStream(queJsonSDCard);
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                JsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }

        } catch (Exception e) {
        }

        return JsonStr;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }


    private void addContentToDatabase(Modal_DownloadContent download_content) {
        DatabaseHandler db = new DatabaseHandler(ShowFilesOnDevice.this);
        ArrayList<String> p_ids = db.getDownloadContentID(PD_Constant.TABLE_PARENT);
        ArrayList<String> c_ids = db.getDownloadContentID(PD_Constant.TABLE_CHILD);
        for (int i = 0; i < download_content.getNodelist().size(); i++) {
            if (i == 0) {
                if (!p_ids.contains(String.valueOf(download_content.getNodelist().get(i).getNodeid())))
                    db.Add_Content(PD_Constant.TABLE_PARENT, download_content.getNodelist().get(i));
                Log.d("addContentToDB :::", download_content.getNodelist().get(i) + "");

            } else {
                if (!c_ids.contains(String.valueOf(download_content.getNodelist().get(i).getNodeid())))
                    db.Add_Content(PD_Constant.TABLE_CHILD, download_content.getNodelist().get(i));
                Log.d("addContentToDB :::", download_content.getNodelist().get(i) + "");

            }
        }
//        db.Add_DOownloadedFileDetail(download_content.getNodelist().get(download_content.getNodelist().size() - 1));
    }
}