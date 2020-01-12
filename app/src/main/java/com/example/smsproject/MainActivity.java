package com.example.smsproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_SEND_SMS = 123;
    private static final String TAG = MySmsReceiver.class.getSimpleName();
    EditText editURL,editNumber;
    Button toggle;
    SharedPreferences pref;
    LinearLayout layoutTextURL,layoutEditURL;
    TextView textURL,textNumber;
    String status = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editURL = (EditText) findViewById(R.id.editURL);
        editNumber = (EditText) findViewById(R.id.editNumber);
        toggle = (Button) findViewById(R.id.toggle);
        layoutTextURL = (LinearLayout) findViewById(R.id.layoutTextURL);
        layoutEditURL = (LinearLayout) findViewById(R.id.layoutEditURL);
        textURL = (TextView) findViewById(R.id.textURL);
        textNumber = (TextView) findViewById(R.id.textNumber);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String prefUrl = pref.getString("urlLINK", null);
        String prefNumber = pref.getString("numberLINK", null);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSION_SEND_SMS);
        }


        if (prefUrl == null || prefNumber == null) {
            layoutTextURL.setVisibility(View.GONE);
            layoutEditURL.setVisibility(View.VISIBLE);
            status = "SAVE";
            toggle.setText(status);
        } else {
            layoutTextURL.setVisibility(View.VISIBLE);
            layoutEditURL.setVisibility(View.GONE);
            String urlStorage = "", numberStorage = "";
            urlStorage = prefUrl + "";
            numberStorage = prefNumber + "";
            textURL.setText(urlStorage);
            textNumber.setText(numberStorage);
            status = "EDIT";
            toggle.setText(status);

        }


        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equals("SAVE")) {
                    if (editURL.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please input url first", Toast.LENGTH_SHORT).show();
                    } else if(editNumber.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please input number first", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String url = "",number;
                        url = editURL.getText().toString();
                        number = editNumber.getText().toString();
                        Log.d(TAG, "onReceive: " + url);
//                        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("urlLINK", url);
                        editor.putString("numberLINK", number);
                        editor.apply();

                        String urlNow = pref.getString("urlLINK", null);
                        String numberNow = pref.getString("numberLINK", null);

                        layoutTextURL.setVisibility(View.VISIBLE);
                        layoutEditURL.setVisibility(View.GONE);
                        status = "EDIT";
                        toggle.setText(status);
                        String urlSTorage = "", numberSTorage = "";
                        urlSTorage = urlNow  + "";
                        numberSTorage = numberNow + "";
                        textURL.setText(urlSTorage);
                        textNumber.setText(numberSTorage);

                    }
                }
                else {
                    layoutTextURL.setVisibility(View.GONE);
                    layoutEditURL.setVisibility(View.VISIBLE);
                    status = "SAVE";
                    toggle.setText(status);
                }
            }
        });

    }


}
