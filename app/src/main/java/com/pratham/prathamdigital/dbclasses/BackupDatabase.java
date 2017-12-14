package com.pratham.prathamdigital.dbclasses;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by PEF-2 on 29/10/2015.
 */
public class BackupDatabase {

    public static void backup(Context mContext) {
        try {

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            //if (sd.canWrite()) {

//            /data/data/com.pratham.prathamdigital/databases/PrathamDB
            File file = mContext.getDir("databases" /*+ "PrathamDB.db"*/, Context.MODE_PRIVATE);
//                String currentDBPath = "//data//data//com.pratham.prathamdigital//databases//"+"PrathamDB.db";
            String currentDBPath = file.getAbsolutePath().replace("app_databases","databases");
            File file1=new File(currentDBPath,"PrathamDB");
            Log.d("db_path::", currentDBPath);
            Log.d("db_path::", file1.getAbsolutePath());
            String backupDBPath = "PrathamDB";
//            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);
            //File currentDB = new File(sd,backupDBPath);
//             File backupDB = new File(data,currentDBPath);
            if (file.exists()) {
                FileChannel src = new FileInputStream(file1).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }

            backupDB.renameTo(new File(sd,"PrathamDB.db"));

            //}
        } catch (Exception e) {
            //  ShowAlert showAlert=new ShowAlert();
            //  showAlert.showDialogue(mContext,"Problem occurred to database. Please contact your administrator.");
          /*  SyncActivityLogs syncActivityLogs=new SyncActivityLogs(mContext);
            syncActivityLogs.addToDB("backupDatabase-backupDatabase",e,"Error");*/
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            throw new Error("Copying Failed");
        }
    }

}
