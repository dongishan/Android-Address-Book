package com.gishan.addressbook.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gishan Don Ranasinghe on 30/04/15.
 */

/*utility class to process common app methods*/
public class ABUtils {

    private static final String EMAIL_REGEX = "^[\\w\\-.$/|?:;\\[\\]~&^`#{} *+=%<>'!,\"]+@[\\w.-]+[.][a-zA-Z0-9]+$";

    //Static method to show alert
    public static void showAlert(String msg,Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //Static method validate an email address using a regex
    public static boolean validateEmailAddress(String email){
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
