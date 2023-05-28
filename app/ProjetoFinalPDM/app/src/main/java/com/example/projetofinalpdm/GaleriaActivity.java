package com.example.projetofinalpdm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Objects;

public class GaleriaActivity extends AppCompatActivity {

    private ImageView imageView;

    private TextView textViewImgProcessada;
    private FloatingActionButton btSalvar, btCarregar;
    private Button btOtimizar;
    private static final int PEGAR_IMAGEM = 100;
    Uri imageUri;

    BitmapDrawable bmOriginal, bmNormal;

    private String imgPath;


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        ActivityCompat.requestPermissions(GaleriaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(GaleriaActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

        textViewImgProcessada = findViewById(R.id.textViewImgProcessada);

        imageView = findViewById(R.id.imageView3);
        bmNormal = (BitmapDrawable) imageView.getDrawable();
        bmOriginal = (BitmapDrawable) imageView.getDrawable();

        btCarregar = findViewById(R.id.btCarregar);
        btSalvar = findViewById(R.id.btSalvar);
        btOtimizar = findViewById(R.id.btOtimizar);
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

        btOtimizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackendService backendService = new BackendService();
                Bitmap imagemProcessada = backendService.doPostImage(imgPath);
                imageView.setImageBitmap(imagemProcessada);
                textViewImgProcessada.setText("Imagem processada com sucesso.");
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

                imgPath = getPath(getApplicationContext(), imageUri);
            }
    }

    // Métodos auxiliares
    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}