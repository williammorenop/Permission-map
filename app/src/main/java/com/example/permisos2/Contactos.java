package com.example.permisos2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

public class Contactos extends AppCompatActivity {
private ListView listaC;
    String[] mProjection;
    Cursor mCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        listaC=findViewById(R.id.list);

        mProjection = new String[]{
                ContactsContract.Profile._ID,
                ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
        };

        
        ContactsAdapter mContactsAdapter = new ContactsAdapter(this, null, 0);
        listaC.setAdapter(mContactsAdapter);

        mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null);
        mContactsAdapter.changeCursor(mCursor);

        loadListContacts();
    }

    private void loadListContacts() {

    }
}
