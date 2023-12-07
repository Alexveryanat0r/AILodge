package com.example.myapplication.deepl;

import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class DeepLInteraction {
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private RequestDeepL requestDeepL;

    public DeepLInteraction() {
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.MINUTES)
                .build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api-free.deepl.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        requestDeepL = retrofit.create(RequestDeepL.class);
    }

    public void translate(String text, String outLang, DataResult res) throws IOException {
        res.result = requestDeepL.getDeepL(text, outLang).execute().body().translations.get(0).text;
    }

    interface RequestDeepL {
        @GET("/v2/translate?auth_key=946828e6-db14-c27b-0f57-ac7f85f32365:fx")
        Call<DeepLData> getDeepL(@Query("text") String text, @Query("target_lang") String target_lang);
    }
}

class DeeplResult {
    public static String result = null;
}
