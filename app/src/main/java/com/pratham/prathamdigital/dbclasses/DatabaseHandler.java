package com.pratham.prathamdigital.dbclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pratham.prathamdigital.models.GoogleCredentials;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 12-08-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PrathamDB";

    // Contacts table name
    private static final String TABLE_DOWNLOADED = "table_downloaded";
    private static final String TABLE_SCORE = "table_score";
    private static final String TABLE_PARENT = "table_parent_content";
    private static final String TABLE_CHILD = "table_child_content";
    private static final String TABLE_GOOGLEDATA = "table_googleData";

    // Downloaded Contents Table Columns names
    private static final String KEY_RESOURCEID = "resourceid";
    private static final String KEY_NODEID = "nodeid";
    private static final String KEY_NODETITLE = "nodetitle";

    // Modal_Score Table Columns names
    private static final String RES_ID = "resourceId";
    private static final String SESSION_ID = "sessionId";
    private static final String QUESTION_ID = "questionId";
    private static final String SCORED_MARKS = "scoredMarks";
    private static final String TOTAL_MARKS = "totalMarks";
    private static final String LEVEL = "level";
    private static final String START_TIME = "startDateTime";
    private static final String END_TIME = "endDateTime";
    private static final String DEVICE_ID = "deviceId";
    private static final String LOCATION = "location";
    private static final String SENT_FLAG = "sentFlag";

    // Contents Table Parent names
    public static final String CONTENT_NODEDESC = "nodedesc";
    public static final String CONTENT_NODEAGE = "nodeeage";
    public static final String CONTENT_NODEIMAGE = "nodeimage";
    public static final String CONTENT_NODEKEYWORD = "nodekeywords";
    public static final String CONTENT_NODESERVERIMAGE = "nodeserverimage";
    public static final String CONTENT_NODETITLE = "nodetitle";
    public static final String CONTENT_NODETYPE = "nodetype";
    public static final String CONTENT_RESOURCEID = "resourceid";
    public static final String CONTENT_RESOURCEPATH = "resourcepath";
    public static final String CONTENT_RESOURCETYPE = "resourcetype";
    public static final String CONTENT_LEVEL = "level";
    public static final String CONTENT_NODEID = "nodeid";
    public static final String CONTENT_PARENTID = "parentid";

    // Google Data names
    public static final String GOOGLE_ID = "GoogleID";
    public static final String GOOGLE_EMAIL = "Email";
    public static final String GOOGLE_PERSONALNAME = "PersonName";
    public static final String GOOGLE_INTROSHOWN = "IntroShown";
    public static final String GOOGLE_LANGUAGE = "languageSelected";
    public SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DOWNLOADED_TABLE = "CREATE TABLE " + TABLE_DOWNLOADED + "("
                + KEY_RESOURCEID + " TEXT,"
                + KEY_NODEID + " TEXT PRIMARY KEY,"
                + KEY_NODETITLE + " TEXT" + ")";
        String CREATE_SCORE_TABLE = "CREATE TABLE " + TABLE_SCORE + "("
                + RES_ID + " TEXT,"
                + SESSION_ID + " TEXT,"
                + SCORED_MARKS + " INTEGER,"
                + TOTAL_MARKS + " INTEGER,"
                + START_TIME + " DATETIME,"
                + END_TIME + " DATETIME,"
                + LOCATION + " TEXT,"
                + SENT_FLAG + " INTEGER,"
                + DEVICE_ID + " TEXT" + ")";
        String CREATE_PARENT_TABLE = "CREATE TABLE " + TABLE_PARENT + "("
                + CONTENT_NODEID + " TEXT PRIMARY KEY,"
                + CONTENT_NODETYPE + " TEXT,"
                + CONTENT_NODETITLE + " TEXT,"
                + CONTENT_NODEKEYWORD + " TEXT,"
                + CONTENT_NODEAGE + " TEXT,"
                + CONTENT_NODEDESC + " TEXT,"
                + CONTENT_NODEIMAGE + " TEXT,"
                + CONTENT_NODESERVERIMAGE + " TEXT,"
                + CONTENT_RESOURCEID + " TEXT,"
                + CONTENT_RESOURCETYPE + " TEXT,"
                + CONTENT_RESOURCEPATH + " TEXT,"
                + CONTENT_LEVEL + " TEXT,"
                + CONTENT_PARENTID + " TEXT" + ")";
        String CREATE_CHILD_TABLE = "CREATE TABLE " + TABLE_CHILD + "("
                + CONTENT_NODEID + " TEXT PRIMARY KEY,"
                + CONTENT_NODETYPE + " TEXT,"
                + CONTENT_NODETITLE + " TEXT,"
                + CONTENT_NODEKEYWORD + " TEXT,"
                + CONTENT_NODEAGE + " TEXT,"
                + CONTENT_NODEDESC + " TEXT,"
                + CONTENT_NODEIMAGE + " TEXT,"
                + CONTENT_NODESERVERIMAGE + " TEXT,"
                + CONTENT_RESOURCEID + " TEXT,"
                + CONTENT_RESOURCETYPE + " TEXT,"
                + CONTENT_RESOURCEPATH + " TEXT,"
                + CONTENT_LEVEL + " TEXT,"
                + CONTENT_PARENTID + " TEXT" + ")";
        String CREATE_GOOGLEDATA_TABLE = "CREATE TABLE " + TABLE_GOOGLEDATA + "("
                + GOOGLE_ID + " TEXT,"
                + GOOGLE_EMAIL + " TEXT,"
                + GOOGLE_PERSONALNAME + " TEXT,"
                + GOOGLE_INTROSHOWN + " INTEGER,"
                + GOOGLE_LANGUAGE + " TEXT" + ")";
        db.execSQL(CREATE_DOWNLOADED_TABLE);
        db.execSQL(CREATE_SCORE_TABLE);
        db.execSQL(CREATE_PARENT_TABLE);
        db.execSQL(CREATE_CHILD_TABLE);
        db.execSQL(CREATE_GOOGLEDATA_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOOGLEDATA);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // set Flag to true i.e 1 if sent
    public void updateSentFlag() {
        try {
            db = getWritableDatabase();
            Cursor cursor = db.rawQuery("update table_score set sentFlag = 1 WHERE sentFlag = 0", null);
            if (cursor.equals(null)) {

            } else if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                }
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Get Local Scores which are not sent to server yet
    public List<Modal_Score> getNewScores() {
        try {
            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from table_score where sentFlag = 0", null);
            return _PopulateListFromCursor(cursor);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<Modal_Score> _PopulateListFromCursor(Cursor cursor) {
        try {
            List<Modal_Score> scoreList = new ArrayList<Modal_Score>();
            Modal_Score score;
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                score = new Modal_Score();
                score.SessionId = cursor.getString(cursor.getColumnIndex("sessionId"));
                score.ResourceId = cursor.getString(cursor.getColumnIndex("resourceId"));
                score.DeviceId = cursor.getString(cursor.getColumnIndex("deviceId"));
                score.Location = cursor.getString(cursor.getColumnIndex("location"));
                score.ScoredMarks = cursor.getInt(cursor.getColumnIndex("scoredMarks"));
                score.TotalMarks = cursor.getInt(cursor.getColumnIndex("totalMarks"));
                score.StartTime = cursor.getString(cursor.getColumnIndex("startDateTime"));
                score.EndTime = cursor.getString(cursor.getColumnIndex("endDateTime"));
                scoreList.add(score);
                cursor.moveToNext();
                db.close();
            }
            cursor.close();
            return scoreList;
        } catch (Exception ex) {
            return null;
        }
    }

    // Adding new contact
    public void Add_Content(String tableName, Modal_ContentDetail contentDetail) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("nodeid", contentDetail.getNodeid());
            contentValues.put("nodetype", contentDetail.getNodetype());
            contentValues.put("nodetitle", contentDetail.getNodetitle());
            contentValues.put("nodekeywords", contentDetail.getNodekeywords());
            contentValues.put("nodeeage", contentDetail.getNodeeage());
            contentValues.put("nodedesc", contentDetail.getNodedesc());
            contentValues.put("nodeimage", contentDetail.getNodeimage());
            contentValues.put("nodeserverimage", contentDetail.getNodeserverimage());
            contentValues.put("resourceid", contentDetail.getResourceid());
            contentValues.put("resourcetype", contentDetail.getResourcetype());
            contentValues.put("resourcepath", contentDetail.getResourcepath());
            contentValues.put("level", contentDetail.getLevel());
            contentValues.put("parentid", contentDetail.getParentid());

            database.replace(tableName, null, contentValues);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adding new downloaded file detail
    public void Add_DOownloadedFileDetail(Modal_ContentDetail content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RESOURCEID, content.getResourceid());
        values.put(KEY_NODEID, content.getNodeid());
        values.put(KEY_NODETITLE, content.getNodetitle());
        // Inserting Row
        db.replace(TABLE_DOWNLOADED, null, values);
        db.close(); // Closing database connection
    }

    public void addScore(Modal_Score modalScore) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("resourceId", modalScore.getResourceId());
            contentValues.put("sessionId", modalScore.getSessionId());
            contentValues.put("scoredMarks", modalScore.getScoredMarks());
            contentValues.put("totalMarks", modalScore.getTotalMarks());
            contentValues.put("startDateTime", modalScore.getStartTime());
            contentValues.put("endDateTime", modalScore.getEndTime());
            contentValues.put("deviceId", modalScore.getDeviceId());
            contentValues.put("location", modalScore.getLocation());
            contentValues.put("sentFlag", modalScore.getSentFlag());

            database.insert(TABLE_SCORE, null, contentValues);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Insert New User
    public void insertNewGoogleUser(GoogleCredentials obj) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(GOOGLE_ID, obj.GoogleID);
            contentValues.put(GOOGLE_EMAIL, obj.Email);
            contentValues.put(GOOGLE_PERSONALNAME, obj.PersonName);
            contentValues.put(GOOGLE_INTROSHOWN, obj.IntroShown);
            contentValues.put(GOOGLE_LANGUAGE, obj.languageSelected);
            database.insert(TABLE_GOOGLEDATA, null, contentValues);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getting All Parent Contents
    public ArrayList<Modal_ContentDetail> Get_Contents(String table_name, String id) {
        ArrayList<Modal_ContentDetail> contents = new ArrayList<>();
        try {
            contents.clear();
            // Select All Query
            String selectQuery = null;
            if (table_name.equalsIgnoreCase(PD_Constant.TABLE_PARENT)) {
                selectQuery = "SELECT  * FROM " + TABLE_PARENT;
            } else {
                selectQuery = "SELECT  * FROM " + TABLE_CHILD + " WHERE parentid =\"" + id + "\"";
            }
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Modal_ContentDetail contentDetail = new Modal_ContentDetail();
                    contentDetail.setNodeid(cursor.getString(0));
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
                    contentDetail.setParentid(cursor.getString(12));
                    // Adding contact to list
                    contents.add(contentDetail);
                } while (cursor.moveToNext());
            }
            // return contact list
            cursor.close();
            db.close();
            return contents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }

    // Getting All Downloaded Contents Resource IDS
    public ArrayList<String> getDownloadContentID(String tablename) {
        ArrayList<String> ids = new ArrayList<>();
        try {
            // Select All Query
            String selectQuery = "SELECT  * FROM " + tablename;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    // Adding contact to list
                    ids.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }

            // return contact list
            cursor.close();
            db.close();
            return ids;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    // Login Status of User
    public boolean CheckGoogleLogin(String id) {
        Cursor cursor;
        SQLiteDatabase database = this.getWritableDatabase();
        cursor = database.rawQuery("SELECT * FROM " + TABLE_GOOGLEDATA + " WHERE GoogleID = \"" + id + "\"", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cursor.close();
                database.close();
                return true;
            }
        }
        return false;
    }

    // Get Intro info for User
    public boolean CheckIntroShownStatus(String userID) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + GOOGLE_INTROSHOWN + " FROM " + TABLE_GOOGLEDATA, null);
        cursor.moveToFirst();
        int status = Integer.parseInt(cursor.getString(0));
        Log.d("status::", status + "");
        if (status == 1) {
            cursor.close();
            database.close();
            return true;
        } else if (status == 0) {
            cursor.close();
            database.close();
            return false;
        }
        return false;
    }

    // Update Intro to true after showing Intro
    public void SetIntroFlagTrue(int i, String userID) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(GOOGLE_INTROSHOWN, i);
            database.update(TABLE_GOOGLEDATA, values, GOOGLE_ID + " = ?",
                    new String[]{userID});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Update Intro to true after showing Intro

    public void SetUserLanguage(String userLangage, String id) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(GOOGLE_LANGUAGE, userLangage);
            database.update(TABLE_GOOGLEDATA, values, GOOGLE_ID + " = ?",
                    new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetUserLanguage() {
        String selectedLang;
        try {
            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT " + GOOGLE_LANGUAGE + " from " + TABLE_GOOGLEDATA, null);
            cursor.moveToFirst();
            selectedLang = cursor.getString(0);
            cursor.close();
            database.close();
            if (selectedLang != null) {
                return selectedLang;
            } else {
                return "Hindi";
            }
        } catch (Exception e) {
            return "Hindi";
        }
    }

    public String getGoogleID() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT " + GOOGLE_ID + " from " + TABLE_GOOGLEDATA, null);
            cursor.moveToFirst();
            String id = cursor.getString(0);
            cursor.close();
            database.close();
            return id;
        } catch (Exception e) {
            return "";
        }
    }

    public String getParentID(String id) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT " + CONTENT_PARENTID + " from " + TABLE_CHILD
                    + " where " + CONTENT_NODEID + " = \"" + id + "\"", null);
            cursor.moveToFirst();
            String t_id = cursor.getString(0);
            cursor.close();
            database.close();
            return t_id;
        } catch (Exception e) {
            return "-1";
        }
    }
    // Updating single contact
//    public int Update_Contact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//        values.put(KEY_EMAIL, contact.getEmail());
//
//        // updating row
//        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
//                new String[]{String.valueOf(contact.getID())});
//    }

    //     Deleting single content
    public void deleteContentFromChild(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CHILD, CONTENT_NODEID + " = ?",
                    new String[]{id});
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteContentFromParent(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_PARENT, CONTENT_NODEID + " = ?",
                    new String[]{id});
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
//    // Getting contacts Count
    public int Get_Total_Contents(String id) {
        String countQuery = "SELECT  * FROM " + TABLE_CHILD + " WHERE " + CONTENT_PARENTID + " = \""
                + id + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

}