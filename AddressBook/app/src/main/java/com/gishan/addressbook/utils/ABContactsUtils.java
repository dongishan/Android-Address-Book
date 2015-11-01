package com.gishan.addressbook.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.gishan.addressbook.objects.ABContact;
import  android.provider.ContactsContract.CommonDataKinds.*;

import java.util.ArrayList;

/**
 * Created by Gishan Don Ranasinghe on 28/04/15.
 */

/*Utility class to get phone contact*/
public class ABContactsUtils {
    private final String TAG = getClass().getName();
    //Define the all contacts uris and names
    private final Uri QUERY_URI = ContactsContract.Contacts.CONTENT_URI;

    //Contact
    private final String CONTACT_ID = ContactsContract.Contacts._ID;
    private final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private final String STARRED_CONTACT = ContactsContract.Contacts.STARRED;
    //Email
    private final String EMAIL_CONTACT_ID = Email.CONTACT_ID;
    private final Uri EMAIL_CONTENT_URI = Email.CONTENT_URI;
    private final String EMAIL_DATA = Email.DATA;
    //Number
    private final String PHONE_CONTACT_ID = Phone.CONTACT_ID;
    private final String PHONE_NUMBER = Phone.NUMBER;
    private final Uri PHONE_CONTENT_URI = Phone.CONTENT_URI;
    private final String PHONE_CONTACT_TYPE = Phone.TYPE;

    private final String CONTRACT_READ_COLUMNS[] = new String[]{CONTACT_ID, DISPLAY_NAME,ContactsContract.Contacts.STARRED};

    public  ArrayList<ABContact> getContractContacts(Context context) {
        ArrayList<ABContact> contactsList = new ArrayList<>();
        //Loading data from the android contacts
        ContentResolver contentResolver = context.getContentResolver();
        //Get all the contacts with passing 0
        Cursor contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, CONTRACT_READ_COLUMNS, STARRED_CONTACT + "=0", null, null);
        Log.d(TAG, "Contacts Count - " + contactsCursor.getCount());

        if (contactsCursor.getCount() > 0) {
            while (contactsCursor.moveToNext()) {
                String contactId, name, email, number;
                ABContact abContact = new ABContact();
                //Mark the contact as a phone contact
                abContact.setPhoneContact(true);
                //Getting contact id and name
                contactId = contactsCursor.getString(contactsCursor.getColumnIndex(CONTACT_ID));
                name = contactsCursor.getString(contactsCursor.getColumnIndex(DISPLAY_NAME));
                //Updating the object
                abContact.setContactId(contactId);
                abContact.setName(name);

                //Obtaining email address
                Cursor emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null, EMAIL_CONTACT_ID + " = ?", new String[]{contactId}, null);
                while (emailCursor.moveToNext()) {
                    email = emailCursor.getString(emailCursor.getColumnIndex(EMAIL_DATA));
                    if (!TextUtils.isEmpty(email)) {
                        abContact.setEmail(email);
                    }
                }
                //Closing the email cursor
                emailCursor.close();

                //Obtaining phone number and type
                Cursor phones = contentResolver.query(PHONE_CONTENT_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{contactId}, null, null);
                while (phones.moveToNext()) {
                    number = phones.getString(phones.getColumnIndex(PHONE_NUMBER));

                    int numberType = phones.getInt(phones.getColumnIndex(PHONE_CONTACT_TYPE));
                    switch (numberType) {
                        case Phone.TYPE_HOME:
                            abContact.setHomeNum(number);
                            break;
                        case Phone.TYPE_MOBILE:
                            abContact.setMobileNum(number);
                            break;
                        case Phone.TYPE_WORK:
                            abContact.setWorkNum(number);
                            break;
                    }
                }
                //Closing the phone cursor
                phones.close();

                //Storing the process contact
                contactsList.add(abContact);
            }
        }
        //Closing the contacts cursor
        contactsCursor.close();
        //returning the phone contact list
        return contactsList;
    }
}
