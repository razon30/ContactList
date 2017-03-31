package razon.contactlist.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import razon.contactlist.Adapters.AdapterContact;
import razon.contactlist.Data.DatabaseHandler;
import razon.contactlist.Model.Contact;
import razon.contactlist.R;

public class ContactListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    AdapterContact adapter;
    Cursor cursor;
    String name, phonenumber;
    ArrayList<Contact> listContacts;

    //database class instance
    DatabaseHandler databaseHandler;

    //code for requesting permission for contact list
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // user interface layout for this Activity
        // The layout file is defined in the project res/layout/main_activity.xml file
        setContentView(R.layout.activity_contact_list);

        //initializing all the attributes and instances to object
        initialization();

        //setting layout maanger to recyclerview
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //checking the SDK is getter than Mashmallow or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // if equal or getter than marshmallow, checking the permission
            enableRuntimePermission();

        }else {

            // if less than marshmallow, getting contact list
            getContactsIntoArrayList();
        }


    }

    //initializing all the attributes and instances to object
    private void initialization() {

        listContacts = new ArrayList<Contact>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mLinearLayoutManager = new LinearLayoutManager(ContactListActivity.this, LinearLayoutManager.VERTICAL, false);
        databaseHandler = new DatabaseHandler(ContactListActivity.this);


    }

    public void getContactsIntoArrayList() {

        //getting phone content uri in cursor
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        //loop will execute if cursor items ends
        while (cursor.moveToNext()) {

            //getting name of the contact's name of the corresponding index
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            //getting name of the contact's phone number of the corresponding index
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            //getting object of Contact model class
            Contact contact = new Contact();

            //setting name to contact
            contact.setName(name);

            //setting phone number to contact
            contact.setNumber(phonenumber);

            //adding the contact object to contact array list
            listContacts.add(contact);

        }

        //closing the cursor
        cursor.close();

        // sending list to dataabse class setContact method. The method saves the data.
        databaseHandler.setContact(listContacts);

        //getting adapter object
        adapter = new AdapterContact(ContactListActivity.this);

        //setting object to recyclerview
        mRecyclerView.setAdapter(adapter);

    }


    // Checking prmission and getting permission
    public void enableRuntimePermission() {

            // checking permission
            if (ContextCompat.checkSelfPermission(ContactListActivity.this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation if user reject for the first time
                if (ActivityCompat.shouldShowRequestPermissionRationale(ContactListActivity.this,
                        Manifest.permission.READ_CONTACTS)) {

                    //showing explaination
                    showExplainationAlert();

                } else {

                    //asking permission
                    askPermission();

                }
            } else {

                //getting contact list
                getContactsIntoArrayList();
            }


    }

    //asking permission
    public void askPermission() {
        ActivityCompat.requestPermissions(ContactListActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS},
                RequestPermissionCode);
    }

    //explaination alert message
    private void showExplainationAlert() {

        //initializing alert dialouge
        AlertDialog.Builder builderAlertDialog = new AlertDialog.Builder(
                ContactListActivity.this);

        //showing explaination and asking permission again
        builderAlertDialog.setTitle("Permission Explaination")
                .setMessage("We need Contact permission to populate the Recyclerview")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If user accepts, asks permission again
                        askPermission();

                    }
                })
                .setNegativeButton("Ignor", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If user rejects, finishes the alert dialouge and shows a message to user
                        Toast.makeText(ContactListActivity.this, "Permission declined, RecyclerView won't populate", Toast.LENGTH_LONG).show();
                    }
                })
                .show();

    }


    //calls when user react to permission dialouge
    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission granted, getting contact list
                    getContactsIntoArrayList();

                } else {

                    //Permission not granted, finishes the alert dialouge and shows a message to user
                    Toast.makeText(ContactListActivity.this, "Permission Canceled, RecyclerView won't populate", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


}
