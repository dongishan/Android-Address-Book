package com.gishan.addressbook;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gishan.addressbook.contentprovider.ABContentProvider;
import com.gishan.addressbook.contentprovider.ABProviderContract;
import com.gishan.addressbook.database.ABDatabaseHelper;
import com.gishan.addressbook.objects.ABContact;
import com.gishan.addressbook.utils.ABUtils;
import com.gishan.addressbook.utils.Constants;

import java.io.ByteArrayOutputStream;

/**
 * Created by Gishan Don Ranasinghe on 19/04/15.
 */
public class CreateContactActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText edName,edMobileNum,edHomeNum,edWorkNum,edEmail,edWebsite;
    private Button btnCreateContact;
    private Context context;
    private String name, mobileNum, homeNum, workNum, email, website,imageUrl;
    private final String TAG = getClass().getName();
    private String contactId;
    private boolean updatingContact;
    private final int REQUEST_LIBRARY = 1;
    private ImageButton btnAddPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        context = this;
        //Locating the views and setting actions
        locateViews();
        setActions();

        //Receiving the contactId if the user wants to edit a contact
        contactId = getIntent().getStringExtra(Constants.CONTACT_ID);

        //If the contactId is not null that means the user is editing a contact.
        // So change the layout to match this functionality. Such as Create Contact button will now show Update Contact
        if(contactId != null){
            Log.d(TAG,"Contact id - "+contactId);
            updatingContact = true;
            btnCreateContact.setText("Update Contact");
            //Filling the edit data using the contactId received from the intent
            fillDataForEdit();
        }
    }

    /*Locating all the views from the layout files*/
    public void locateViews(){
        edName = (EditText) findViewById(R.id.edName);
        edMobileNum = (EditText) findViewById(R.id.edMobileNum);
        edHomeNum = (EditText) findViewById(R.id.edHomeNum);
        edWorkNum = (EditText) findViewById(R.id.edWorkNum);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edWebsite = (EditText) findViewById(R.id.edWebsite);
        btnCreateContact = (Button)findViewById(R.id.btnCreate);
        btnAddPhoto = (ImageButton)findViewById(R.id.btnAddPhoto);

    }

    /*Setting actions*/
    public void setActions(){
        btnCreateContact.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
    }

    /*reading the edit text values to variables and validating the name*/
    public boolean validateFields(){
        //Storing the values to variables
        name = edName.getText().toString();
        mobileNum = edMobileNum.getText().toString();
        homeNum = edHomeNum.getText().toString();
        workNum = edWorkNum.getText().toString();
        email = edEmail.getText().toString();
        website = edWebsite.getText().toString();

        //Validating name, email and website
        if(name.length() == 0){
            ABUtils.showAlert("Please enter name!",context);
            return false;
        }else  if(email.length() == 0 || !ABUtils.validateEmailAddress(email)) {
            ABUtils.showAlert("Please enter valid email!", context);
            return false;
        }else if(website.length() == 0){
            ABUtils.showAlert("Please enter valid website!", context);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == btnCreateContact){
            //Once user pressed this button 1st validate the vields
            if(validateFields()){
                //If the validation passed store the data in the content values
                ContentValues values = new ContentValues();
                values.put(ABProviderContract.NAME, name);
                values.put(ABProviderContract.MOBILE_NUMBER, mobileNum);
                values.put(ABProviderContract.HOME_NUMBER, homeNum);
                values.put(ABProviderContract.WORK_NUMBER, workNum);
                values.put(ABProviderContract.EMAIL_ADDRESS, email);
                values.put(ABProviderContract.WEBSITE, website);
                values.put(ABProviderContract.IMAGE_URL, imageUrl);

                //Updating an existing contact
                if(updatingContact){
                    getContentResolver().update(ABProviderContract.CONTACTS_URI,values,"_id="+contactId,null);
                }else{
                    //Adding a new contact
                    getContentResolver().insert(ABProviderContract.CONTACTS_URI, values);
                }
                finish();
            }
        }else if(v.getId() == R.id.btnAddPhoto){
            addPhoto();
        }
    }

    //Filling the data for the edit contact
    public void fillDataForEdit(){
        //Add columns to read to an array
        String columns[] = new String[]
                {
                        ABProviderContract._ID,
                        ABProviderContract.NAME,
                        ABProviderContract.MOBILE_NUMBER,
                        ABProviderContract.HOME_NUMBER,
                        ABProviderContract.WORK_NUMBER,
                        ABProviderContract.EMAIL_ADDRESS,
                        ABProviderContract.WEBSITE,
                        ABProviderContract.IMAGE_URL
                };

        //ContactId is used to get the contact editing.
        Cursor cursor = getContentResolver().query(ABProviderContract.CONTACTS_URI, columns, null, null, null);
        if(cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
             if(cursor.getString(0).equals(contactId)){
                 //Adding text to the edit texts using the editing contact
                 edName.setText(cursor.getString(1));
                 edMobileNum.setText(cursor.getString(2));
                 edWorkNum.setText(cursor.getString(3));
                 edHomeNum.setText(cursor.getString(4));
                 edEmail.setText(cursor.getString(5));
                 edWebsite.setText(cursor.getString(6));
                 btnAddPhoto.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(7)));

             }
           }
        }
    }

    //Adding photo to a contact.
    public void addPhoto(){
        //Button for the alert dialog
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
           //If editing the add photo will be edit photo
        if(updatingContact) {
            builder.setTitle("Edit Photo");
        }else{
            builder.setTitle("Add Photo");
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int item) {
           if (items[item].equals("Choose from Library")) {
                //Open the library and filter the content only to images
                 Intent intent = new Intent(
                 Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                 ((CreateContactActivity) context).startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            REQUEST_LIBRARY);
            } else if (items[item].equals("Cancel")) {
                    //Dismiss the dialog when a user presses cancel.
                    dialog.dismiss();
                }
            }
        });
        //Showing the alert dialog
        builder.show();
    }

    //Called when the user selected an image from the photo library
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_LIBRARY) {
                //gettting the absolute path of the image
                imageUrl = getAbsolutePath(data.getData());
                //Updating the add photo button with the image which is converted to a bitmap
                btnAddPhoto.setImageBitmap(BitmapFactory.decodeFile(imageUrl));
            }
        }
    }

    //usind the meadia store to get the absolute path using the temp uri
    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return null;
        }
    }

    //Navigation drawer methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
