package com.example.permisos2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton contact;
    ImageButton camera;
    ImageButton locali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contact = findViewById(R.id.imageButton);
        camera = findViewById(R.id.imageButton2);
        locali = findViewById(R.id.imageButton3);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Contactos.class);

                startActivity(intent);


            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Camera.class);
                startActivity(intent);
            }
        });

        locali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Localizacion.class);
                startActivity(intent);
            }
        });
    }
}
