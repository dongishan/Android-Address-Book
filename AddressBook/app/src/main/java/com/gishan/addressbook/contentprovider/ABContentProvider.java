package com.gishan.addressbook.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.gishan.addressbook.database.ABDatabaseHelper;

/**
 * Created by Gishan Don Ranasinghe on 24/04/15.
 */

//Address book Content provider
public class ABContentProvider extends ContentProvider {
    private final String TAG = getClass().getName();
    private ABDatabaseHelper dbHelper = null;

    @Override
    public boolean onCreate() {
        //Create the database
        this.dbHelper = new ABDatabaseHelper(this.getContext());
        return true;
    }

    //Returning the content type
    @Override
    public String getType(Uri uri) {
        String contentType;

        if (uri.getLastPathSegment() == null) {
            contentType = ABProviderContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = ABProviderContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }


      //Inserting a contact to the database table contact
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = "contacts";
        long id = db.insert(tableName, null, values);
        db.close();
        Uri appendIduri = ContentUris.withAppendedId(uri, id);
        return appendIduri;

    }

    //Querying the database using given projection and selection
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = "contacts";
        Cursor c = db.query(tableName, projection, null, null, null, null, null);
        return c;
    }

    //Udating a contact in the database contacts table
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = "contacts";
        long id = db.update(tableName, values, selection,null);
        db.close();
        return  0;

    }

    //Deleting a contact from the contacts table.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deleteCount = db.delete("contacts", "_id" + "="
                + Integer.valueOf(selection), null);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

}
