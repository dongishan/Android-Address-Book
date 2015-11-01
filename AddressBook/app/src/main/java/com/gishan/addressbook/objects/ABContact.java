package com.gishan.addressbook.objects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.gishan.addressbook.ContactsActivity;
import com.gishan.addressbook.contentprovider.ABProviderContract;
import com.gishan.addressbook.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Gishan Don Ranasinghe on 19/04/15.
 */

/*Object class for a contact*/
public class ABContact {
    private boolean phoneContact;
    private String contactId;
    private String name;
    private String mobileNum;
    private String homeNum;
    private String workNum;
    private String email;
    private String website;
    private String address;
    private String imageUrl;


    /*Making a phone call to the contact*/
    public void makeCall(final Context context){
        //Filtering the alert dialog button list using the available number
        ArrayList<String> callButtons = new ArrayList<>();
        if(this.getMobileNum() != null){
            callButtons.add("Mobile - "+this.getMobileNum());
        }
        if(this.getHomeNum() != null){
            callButtons.add("Home - "+this.getHomeNum());
        }
        if(this.getWorkNum() != null){
            callButtons.add("Work - "+this.getWorkNum());
        }

        final CharSequence[] items = new CharSequence[callButtons.size()+1];
        for(int i = 0; i < callButtons.size();i++){
            items[i] = callButtons.get(i);
        }
        items[items.length-1] = "Cancel";
        //Making the phone call when a user presses the humber
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Call "+this.getName()+"?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                if (String.valueOf(items[item]).contains("Mobile")) {
                    intent.setData(Uri.parse("tel:" +getMobileNum()));
                    context.startActivity(intent);
                }else if(String.valueOf(items[item]).contains("Home")) {
                    intent.setData(Uri.parse("tel:" + getHomeNum()));
                    context.startActivity(intent);
                }else if(String.valueOf(items[item]).contains("Work")) {
                    intent.setData(Uri.parse("tel:" + getWorkNum()));
                    context.startActivity(intent);
                }else{
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /*Deleting the contact*/
    public void delete(final Context context){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Delete Contact");
        alert.setMessage("Are you sure you want to delete "+this.getName()+" from your address book?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.getContentResolver().delete(ABProviderContract.CONTACTS_URI,getContactId(),null);
                final Intent disconnectedIntent = new Intent(
                        Constants.REFRESH_CONTACTS_LIST);
                context.sendBroadcast(disconnectedIntent);
           }
        });
        alert.setNegativeButton("No",null);
        alert.show();
    }

    /*Opening the website for the contact*/
    public void openWebsite(){

    }

    //Custom getter to return the phone number
    public String getPhoneNumber(){
        if(mobileNum != null){
            return  mobileNum;
        }else if(homeNum != null){
            return homeNum;
        }else{
            return workNum;
        }
    }

    /*Getters and setters*/
    public void setPhoneContact(boolean phoneContact) {
        this.phoneContact = phoneContact;
    }

    public boolean isPhoneContact() {
        return phoneContact;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactId() {
        return contactId;
    }

    public String getWorkNum() {
        return workNum;
    }

    public String getHomeNum() {
        return homeNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public void setHomeNum(String homeNum) {
        this.homeNum = homeNum;
    }

    public void setWorkNum(String workNum) {
        this.workNum = workNum;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public String getWebsite() {
        return website;
    }

}

