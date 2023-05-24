package com.example.projetofinalpdm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Objects;

public class GaleriaActivity extends AppCompatActivity {

    private ImageView imageView;
    private FloatingActionButton btSalvar, btCarregar;
    private Button btOtimizar;
    private static final int PEGAR_IMAGEM = 100;
    Uri imageUri;

    BitmapDrawable bmOriginal, bmNormal;


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        ActivityCompat.requestPermissions(GaleriaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(GaleriaActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

        imageView = findViewById(R.id.imageView3);
        bmNormal = (BitmapDrawable) imageView.getDrawable();
        bmOriginal = (BitmapDrawable) imageView.getDrawable();

        btCarregar = findViewById(R.id.btCarregar);
        btSalvar = findViewById(R.id.btSalvar);
        btCarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarImagem();
            }

            private void selecionarImagem() {
                Intent galeria = new Intent(Intent.ACTION_PICK);
                galeria.setType("image/*");
                startActivityForResult(galeria, PEGAR_IMAGEM);
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar();
            }

            private void salvar(){
                Uri imagem;
                ContentResolver contentResolver = getContentResolver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    imagem = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                }else {
                    imagem = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
                Uri uri = contentResolver.insert(imagem, contentValues);

                try {
                    bmOriginal = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = bmOriginal.getBitmap();

                    OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    Objects.requireNonNull(outputStream);
                    Toast.makeText(GaleriaActivity.this, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    Toast.makeText(GaleriaActivity.this, "Impossivel salvar a imagem", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == PEGAR_IMAGEM) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
                bmOriginal = (BitmapDrawable) imageView.getDrawable();
                bmNormal = (BitmapDrawable) imageView.getDrawable();
         }
    }
}