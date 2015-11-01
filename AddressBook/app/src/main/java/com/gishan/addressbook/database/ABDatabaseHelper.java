package com.gishan.addressbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gishan.addressbook.objects.ABContact;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gishan Don Ranasinghe on 24/04/15.
 */
public class ABDatabaseHelper extends SQLiteOpenHelper{

    private String TAG = getClass().getName();

    /*Database name and version for upgrading*/
    private static final String DATABASE_NAME = "addressbook.db";
    private static final int DATABASE_VERSION = 1;

    /*Table names and column names*/
    private static final String TABLE_CONTACTS = "contacts";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_MOBILE_NUM = "mobile_number";
    private static final String COLUMN_HOME_NUM = "home_number";
    private static final String COLUMN_WORK_NUM = "work_number";
    private static final String COLUMN_EMAIL_ADDRESS = "email_address";
    private static final String COLUMN_WEBSITE = "website";
    private static final String COLUMN_IMAGE_URL = "image_url";

    private SQLiteDatabase database;
    private ABDatabaseHelper dbHelper;

    /*Database create sql query with id and the primary key*/
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_CONTACTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + COLUMN_NAME+ " VARCHAR(128) , "
            + COLUMN_MOBILE_NUM+" VARCHAR(128) , "
            + COLUMN_HOME_NUM+" VARCHAR(128) , "
            + COLUMN_WORK_NUM+" VARCHAR(128) , "
            + COLUMN_EMAIL_ADDRESS+" VARCHAR(128) , "
            + COLUMN_WEBSITE+" VARCHAR(128) , "
            + COLUMN_IMAGE_URL+" VARCHAR(128)"
            +");";

    /*Database constructor*/
    public ABDatabaseHelper(Context context){
        super (context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //Creating the table
            db.execSQL(DATABASE_CREATE);
        }catch(Exception e){
            Log.d(TAG,"Database not created - "+e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the table of exists when incrementing the version.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        //Call onCreate to create a new version of the database
        onCreate(db);
    }
}
