package com.example.projetofinalpdm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackendService {

    private static final String SERVICE_URL = "http://10.0.2.2:5000/";

    public Bitmap doPostImage(String imagePath) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        RequestBody postImageBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "imagem.jpg", RequestBody.create( MediaType.parse("image/jpg"), byteArray ))
                .build();

        return postRequest(postImageBody);
    }

    private Bitmap postRequest(RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SERVICE_URL)
                .post(postBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            Bitmap imagemProcessada = BitmapFactory.decodeStream(inputStream);
            return imagemProcessada;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                call.cancel();
//                Log.i("Erro na comunicação com o Serviço", e.getMessage());
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                InputStream inputStream = response.body().byteStream();
//                Bitmap imagemProcessada = BitmapFactory.decodeStream(inputStream);
//                Log.i("IMG:", "Chegou aqui");
//            }
//        });
    }

}
