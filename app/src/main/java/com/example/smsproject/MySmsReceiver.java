package com.example.smsproject;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MySmsReceiver extends BroadcastReceiver {

    private static final String TAG = MySmsReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";
    boolean isVersionM;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.print(66666);

        System.out.print(777777);

        System.out.print(88888);
        System.out.print(99999);
        System.out.print(444444);


        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        String action = intent.getAction();
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        String prefUrl = pref.getString("urlLINK", null);
        String prefNumber = pref.getString("numberLINK", null);

        if (pdus != null) {
            // Check the Android version.
            isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        }

        // Fill the msgs array.
        msgs = new SmsMessage[pdus.length];


        for (int i = 0; i < msgs.length; i++) {
            // Check Android version and use appropriate createFromPdu.
            if (isVersionM) {
                // If Android version M or newer:
                msgs[i] =
                        SmsMessage.createFromPdu((byte[]) pdus[i], format);
            } else {
                // If Android version L or older:
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            strMessage += "SMS from " + msgs[i].getOriginatingAddress();
            strMessage += " :" + msgs[i].getMessageBody() + "\n";

            Log.d(TAG, "onReceive: " + strMessage);
            Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();

            Log.d(TAG, "URLpref: " + prefUrl);

            if (prefUrl != null && prefNumber != null) {

                if (msgs[i].getOriginatingAddress().equals(prefNumber)) {
                    Log.d(TAG, "NUMBER MATCH");
                    getUrlForPost(context, prefUrl, msgs[i].getMessageBody());
                }

            } else {
                Toast.makeText(context, "Please input DATA FIRST", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void getUrlForPost(final Context context, String url, String messageBody) {
        String URL = url + messageBody;

        //TODO link url example = https://example.com/messageBody

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(context, "POST URL FAIL", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Toast.makeText(context, "POST URL SUCCESS", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
