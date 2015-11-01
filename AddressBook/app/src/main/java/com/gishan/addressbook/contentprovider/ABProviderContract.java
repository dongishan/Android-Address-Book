package com.gishan.addressbook.contentprovider;

import android.net.Uri;
/**
 * Created by Gishan Don Ranasinghe on 30/04/15.
 */
public class ABProviderContract {

    public static final String AUTHORITY = "com.gishan.addressbook.contentprovider.ABContentProvider";

    public static final Uri CONTACTS_URI = Uri.parse("content://"+AUTHORITY+"/contacts");

    public static final String _ID = "_id";

    public static final String NAME = "name";

    public static final String MOBILE_NUMBER = "mobile_number";

    public static final String HOME_NUMBER = "home_number";

    public static final String WORK_NUMBER = "work_number";

    public static final String EMAIL_ADDRESS = "email_address";

    public static final String WEBSITE = "website";

    public static final String IMAGE_URL = "image_url";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/ABContentProvider.data.text";

    public static final String CONTENT_TYPE_MULTIPLE = "nd.android.cursor.dir/ABContentProvider.data.text";

}
