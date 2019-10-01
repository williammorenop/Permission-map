package com.example.permisos2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Camera extends AppCompatActivity {

    private TextView message;
    private final static int CAMERA_PERMISSION = 2;
    private final static int GALLERY_PERMISSION = 3;

    ImageView foto;
    Button galeria;
    Button camara;
    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        galeria = findViewById(R.id.seleimg);
        camara = findViewById(R.id.cam);
        foto = findViewById(R.id.imageView);

        verifyPermissions();

    }

    /**
     * private void requestPermission(Activity context, String permission, String explanation, int requestId) {
     * if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
     * // Should we show an explanation?
     * <p>
     * if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
     * Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
     * }
     * ActivityCompat.requestPermissions(context, new String[]{permission}, requestId);
     * }
     * }
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GALLERY_PERMISSION:
                if (resultCode == RESULT_OK) {

                    try {

                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        foto.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {


                        e.printStackTrace();

                    }

                }

            case CAMERA_PERMISSION:
                if (requestCode == CAMERA_PERMISSION && resultCode == RESULT_OK) {


                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    foto.setImageBitmap(imageBitmap);
                }


        }
    }

    private void verifyPermissions() {
        Log.d(TAG, "verifyPermissions: asking user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            galeria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, GALLERY_PERMISSION);

                }
            });

            camara.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


                        startActivityForResult(takePictureIntent, CAMERA_PERMISSION);

                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(Camera.this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}