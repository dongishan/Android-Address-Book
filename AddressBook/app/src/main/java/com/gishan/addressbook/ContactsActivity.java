package com.gishan.addressbook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gishan.addressbook.adapters.ABContactAdapter;
import com.gishan.addressbook.contentprovider.ABProviderContract;
import com.gishan.addressbook.objects.ABContact;
import com.gishan.addressbook.utils.ABContactsUtils;
import com.gishan.addressbook.utils.ABUtils;
import com.gishan.addressbook.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Gishan Don Ranasinghe on 19/04/15.
 */
public class ContactsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnTouchListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Context context;
    private TextView tvNoContacts;
    private ListView lyContacts;
    private  ArrayList<ABContact> contactsList;
    private ABContactAdapter contactAdapter;
    private final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //Storing the current activity context
        context = this;
        //Navigation drawer initialisations
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the navigation drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //Registering a receiver to receive the call backs after a contact is deleted. SO the list can be refresed.
        registerReceiver(refreshContactListReceiver,
            new IntentFilter(Constants.REFRESH_CONTACTS_LIST));

        //Locating views from the layout
        locateViews();
        //Setting actions for the neccessary views
        setUpActions();

        //Adding a show contact for functionality display
        addShowContact();
    }

    public void addShowContact(){
       //Add a show contact if it did not add before
        SharedPreferences prefs = getSharedPreferences("Contacts", MODE_PRIVATE);
        boolean dummyAdded = prefs.getBoolean(Constants.DUMMY_ADDED, false);
        if(!dummyAdded) {
            ContentValues values = new ContentValues();
            values.put(ABProviderContract.NAME, "Gishan");
            values.put(ABProviderContract.MOBILE_NUMBER, "08066541444");
            values.put(ABProviderContract.HOME_NUMBER, "0155223223");
            values.put(ABProviderContract.WORK_NUMBER, "0802232788");
            values.put(ABProviderContract.EMAIL_ADDRESS, "gishan@gmail.com");
            values.put(ABProviderContract.WEBSITE, "www.gishan.co.uk");
            values.put(ABProviderContract.IMAGE_URL, "");
            getContentResolver().insert(ABProviderContract.CONTACTS_URI, values);
            getContentResolver().insert(ABProviderContract.CONTACTS_URI, values);

            //Update the shredprefs to not to add dummy contact again
            SharedPreferences.Editor editor = getSharedPreferences("Contacts", MODE_PRIVATE).edit();
            editor.putBoolean(Constants.DUMMY_ADDED, true);
            editor.commit();
        }
    }

    //Refresing the list view onResume to keep the list upto date
    @Override
    protected void onResume() {
        super.onResume();
        //Load data from the phone contacts and content provider - database
        loadData();
    }

    /*Locating the views from the layouts*/
    public void locateViews(){
        tvNoContacts = (TextView)findViewById(R.id.tvNoContacts);
        lyContacts = (ListView) findViewById(R.id.lyContacts);
    }

    /*Setting actions for the required views*/
    public void setUpActions(){

    }

    //Boardcast receiver for the refresh notification
    private final BroadcastReceiver refreshContactListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };

    /*Loading data*/
    public void loadData(){
        ABContactsUtils contactsUtils = new ABContactsUtils();
        //Loading the user phone contacts
        contactsList = contactsUtils.getContractContacts(context);

        //Load the address book contacts using ABContentProvider
        //Columns to read
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

        ContentResolver contentResolver = getContentResolver();
        //Call the content provider query method to load the data in database
        Cursor cursor = contentResolver.query(ABProviderContract.CONTACTS_URI, columns, null, null, null);
        if(cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                //Create contact object with data
                ABContact contact = new ABContact();
                contact.setContactId(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setMobileNum(cursor.getString(2));
                contact.setWorkNum(cursor.getString(3));
                contact.setHomeNum(cursor.getString(4));
                contact.setEmail(cursor.getString(5));
                contact.setWebsite(cursor.getString(6));
                contact.setImageUrl(cursor.getString(7));
                //Add the contact to the contact list
                contactsList.add(contact);
            }
        }

        //If the contact list has any contacts display the list view and load the list view data using the adapter
        if(contactsList.size()>0) {
            //Reversing the contact list so the latest contact will be on top
            Collections.reverse(contactsList);
            tvNoContacts.setVisibility(View.GONE);
            lyContacts.setVisibility(View.VISIBLE);

            contactAdapter = new ABContactAdapter(context,android.R.layout.simple_list_item_1,contactsList);
            lyContacts.setAdapter(contactAdapter);
        }
    }

    //Unregister the receiver at onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refreshContactListReceiver);
    }

    /***********************Navigation drawer interface methods**************************************/
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
          default:
              break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.contacts, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            if (item.getItemId() == R.id.action_create) {
                Intent intent = new Intent(context, CreateContactActivity.class);
                startActivity(intent);
            }else if(item.getItemId() == R.id.action_refresh){
                loadData();
            }else if(item.getItemId() == R.id.action_info){
                ABUtils.showAlert("Address Book\n\nCredits\n\nNavigation Drawer Code - Automated Android Studio navigation drawer code\n\nImages - www.iconfinder.com",context);
            }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ContactsActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
