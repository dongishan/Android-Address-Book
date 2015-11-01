package com.gishan.addressbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gishan.addressbook.CreateContactActivity;
import com.gishan.addressbook.R;
import com.gishan.addressbook.objects.ABContact;
import com.gishan.addressbook.utils.Constants;

import java.util.ArrayList;

import static android.view.View.OnClickListener;

/**
 * Created by Gishan Don Ranasinghe on 19/04/15.
 */

/*Adapter for the list view*/
public class ABContactAdapter extends ArrayAdapter<ABContact> implements OnClickListener{
    private final Context context;
    private ArrayList<ABContact> contactList;

    public ABContactAdapter(Context context, int resource, ArrayList<ABContact> contactList) {
        super(context, resource);
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    //Initializing the row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.contact_adapter, parent, false);

        ABContact currContact = contactList.get(position);

        Button btnDelete = (Button) rowView.findViewById(R.id.btnDelete);
        Button btnEdit = (Button)rowView.findViewById(R.id.btnEdit);
        btnDelete.setTag(position);
        btnEdit.setTag(position);

        TextView tvFstName = (TextView) rowView.findViewById(R.id.tvFirstName);
        TextView tvPhoneNumber = (TextView) rowView.findViewById(R.id.tvPhoneNum);
        tvPhoneNumber.setTag(position);

        tvFstName.setText(currContact.getName());
        //Lock a contact which do not have a phone number
        if(currContact.getPhoneNumber() != null){
            tvPhoneNumber.setText("Call : "+currContact.getPhoneNumber());
        }else{
            tvPhoneNumber.setEnabled(false);
            tvPhoneNumber.setText("Call: No number");
        }

        tvPhoneNumber.setOnClickListener(this);
        if(currContact.isPhoneContact()){
            btnEdit.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);
        }else{
            btnDelete.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
        }
        return rowView;

    }

    //Make a phone call, delete and view.sit actions
    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
            ABContact contact = contactList.get(position);
        if(v.getId() == R.id.tvPhoneNum) {
            contact.makeCall(context);
        }else if(v.getId() == R.id.btnEdit){
            Intent intent = new Intent(context,CreateContactActivity.class);
            intent.putExtra(Constants.CONTACT_ID,contact.getContactId());
            context.startActivity(intent);
        }else{
            contact.delete(context);
        }
    }
}
