package com.example.myapplication.lama;

import android.content.Context;
import android.os.Build;

import com.deepl.api.DeepLException;
import com.deepl.api.Translator;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.deepl.api.TextResult;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.deepl.DataResult;
import com.example.myapplication.deepl.DeepLInteraction;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class LamaInteraction {
    private Context context;
    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;
    private final RequestUser requestLama;

    private Prompt prompt;
    private final float temperature;
    private final int max_tokens;
    private final boolean stream;

    private Translator tr;

    private interface RequestUser {
        @GET("/v1/models")
        Call<LamaDataList> getLamaInfo();

        @POST("/v1/chat/completions")
        Call<ResponseData> postMessage(@Body RequestPost requestPost);
    }

    public LamaInteraction(Context context, Translator transl, float temperature, int max_tokens, boolean stream, int timeout) {
        this.tr = transl;
        this.context = context;

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MINUTES)
                .writeTimeout(timeout, TimeUnit.MINUTES)
                .readTimeout(timeout, TimeUnit.MINUTES)
                .build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.137.1:1334/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        requestLama = retrofit.create(RequestUser.class);

        this.temperature = temperature;
        this.max_tokens = max_tokens;
        this.stream = stream;
    }

    public void Write(String message, TextView targetOut) throws IOException, InterruptedException {
        DeepLInteraction deepl = new DeepLInteraction();

        DataResult res1 = new DataResult();
        DataResult res2 = new DataResult();

        Thread t1 = new Thread() {
            public void run() {
                try {
                    deepl.translate(message.toString(), "EN", res1);
                    deepl.translate(prompt.toString(), "EN", res2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        };
        t1.start();
        t1.join();

//        targetOut.setText(res2.result);


        StaticPrompt en_prompt = new StaticPrompt(res1.result);
        String en_message = res2.result;

        RequestPost data = new RequestPost(temperature, max_tokens, stream, en_prompt, en_message);

        requestLama.postMessage(data).enqueue(new Callback<ResponseData>() {
            final TextView out = targetOut;
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                String lamaSay = response.body().choices.get(0).message.content;

                Thread t1 = new Thread() {
                    public void run() {
                        try {
                            deepl.translate(lamaSay, "RU", res1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                t1.start();
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


                out.setText(res1.result);
////
//////                NotificationCompat.Builder builder =  new NotificationCompat.Builder(context.getApplicationContext(), "channelID")
//////                                .setSmallIcon(R.mipmap.ic_launcher)
//////                                .setContentTitle("Описание составлено")
//////                                .setContentText("Описание составлено2");
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                out.setText("Error: " + t.getMessage());
            }
        });
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public void setPrompt(Prompt prompt) {
        this.prompt = prompt;
    }
}
