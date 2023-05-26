package com.example.projetofinalpdm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btIniciar = findViewById(R.id.btIniciar);

        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGaleria = new Intent(MainActivity.this, GaleriaActivity.class);
                startActivity(intentGaleria);
                //connectServer();
            }
        });
    }

    public void connectServer() {
        String postUrl = "http://10.0.2.2:5000/"; //Usando o IP do Host do Android Emulator

        String postBodyText = "teste";
        MediaType mediaType = MediaType.parse("text/html; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, postBodyText);

        this.postRequest(postUrl, requestBody);
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.i("Erro na API", e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("Teste API", response.body().string());
            }
        });
    }
}