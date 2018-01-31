package com.pratham.prathamdigital.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.File_Adapter;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.FolderClick;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_files_on_device);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewExplorer);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // todo
        treeUri = getIntent().getExtras().getString("treeUri");
        client1 = PrathamApplication.client1;

        if (!client1.equals(null)) {
            listFtpFiles(false, null);
        }
    }

    private void listFtpFiles(boolean isExpand, String name) {
        new AsyncTask<Void, Void, FTPFile[]>() {
            @Override
            protected FTPFile[] doInBackground(Void... voids) {
                // todo get FTP Server's file's paths & details
                try {
                    if (isExpand)
                        client1.changeWorkingDirectory(name);
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
    public void onDownload(int position, FTPFile name) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(ShowFilesOnDevice.this, Uri.parse(treeUri));
        //check whether root folder "PraDigi" exists or not
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
        DocumentFile finalDocumentFile = documentFile;
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
                    downloadDirectory(client1, finalDocumentFile, name);
                } else {
                    downloadFile(client1, name, finalDocumentFile);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pd.dismiss();
            }
        }.execute();

    }

    @Override
    public void onFolderClicked(int position, String name) {
        listFtpFiles(true, name);
    }

    // todo put in async task downdir and file
    private void downloadDirectory(FTPClient ftpClient, DocumentFile documentFile, FTPFile name) {

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
                        downloadDirectory(ftpClient, tempUri, null);
                    } else {
                        downloadFile(ftpClient, aFile, tempUri);
                    }
                }
                tempUri = tempUri.getParentFile();
                tempClient.changeToParentDirectory();

                //todo copy json to database
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

    /*
     * Preparing the list data
     */
    private void prepareListData() {


        // todo copy db from internal to root
        String path = "";
        // Path Change
        // Check folder exists on Internal
        File intPradigi = new File(Environment.getExternalStorageDirectory() + "/PraDigi");
        if (intPradigi.exists()) {
            // Data found on Internal Storage
            path = Environment.getExternalStorageDirectory() + "/PraDigi/databases/PrathamDB";
        }
        Log.d("path::", path);

        File mydir = this.getDir("databases", Context.MODE_PRIVATE); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
        String destString = mydir.getAbsolutePath().replace("app_databases", "databases");
        Log.d("internal_file", destString);
        File mfile = new File(destString, "PrathamDB");
        if (mfile == null) {
            try {
                mfile.createNewFile();
                destString = mfile.getAbsolutePath().replace("app_databases", "databases");
                Log.d("sd_path:::", destString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

            Log.d("contents ::", contents.toString());
            Log.d("c_contents ::", c_contents.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Populating Data
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Parent Table
        ArrayList<Modal_ContentDetail> Parent_table = databaseHandler.Get_Contents(PD_Constant.TABLE_PARENT, 0);
        for (int i = 0; i < Parent_table.size(); i++) {
            Log.d("Parent_table ::", Parent_table.get(i).getNodetitle().toString());
            listDataHeader.add(Parent_table.get(i).getNodetitle().toString());

            // Child Table
            ArrayList<Modal_ContentDetail> Child_table = databaseHandler.Get_Contents(PD_Constant.TABLE_CHILD, Parent_table.get(i).getNodeid());
            List<String> subItem = new ArrayList<String>();

            for (int j = 0; j < Child_table.size(); j++) {
                Log.d("Child_table ::", Child_table.get(j).getNodetitle().toString());
                // Adding child data
                subItem.add(Child_table.get(j).getNodetitle().toString());
            }
            listDataChild.put(listDataHeader.get(i), subItem); // Header, Child data
        }

    }


}