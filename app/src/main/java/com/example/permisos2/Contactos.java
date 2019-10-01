package com.example.permisos2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

public class Contactos extends AppCompatActivity {
    private ListView listaC;
    private final static int CONTACTS_PERMISSION = 0;
    private String[] mProjection;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
        listaC = findViewById(R.id.list);

        //Pedir permisos
        requestPermission(this, Manifest.permission.READ_CONTACTS, "Se necesita acceder a los contactos", CONTACTS_PERMISSION);
        //Iniciar vista
        initView();

    }

    private void initView() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            Toast toast2 = Toast.makeText(getApplicationContext(),
                    "Permiso aprobado", Toast.LENGTH_SHORT);
            toast2.setGravity(Gravity.TOP,0,150);
            toast2.show();

            mProjection = new String[]{
                    ContactsContract.Profile._ID,
                    ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
            };
            ContactsAdapter mContactsAdapter = new ContactsAdapter(this, null, 0);
            listaC.setAdapter(mContactsAdapter);
            mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null);
            mContactsAdapter.changeCursor(mCursor);
            loadListContacts();

        } else {
            Toast toast2 = Toast.makeText(getApplicationContext(),
                    "Permiso NO aprobado", Toast.LENGTH_SHORT);
            toast2.setGravity(Gravity.TOP,0,150);
            toast2.show();
        }
    }


    private void requestPermission(Activity context, String permission, String explanation, int requestId) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestId);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_PERMISSION: {
                initView();
                break;
            }
        }
    }


    private void loadListContacts() {

    }
}
