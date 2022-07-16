package com.engine.test.syncservice;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.engine.test.adapter.Item;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SyncLogin extends IntentService {

    private final static String TAG = "SyncGetData";
    public final static String EXTRA_RESULT = "result";
    public final static String ACTION_SYNC = "com.engine.test.syncservice.SyncLogin.ACTION_SYNC";
    public final static int RESULT_SUCCESS = 1;
    public final static int RESULT_FAIL = 2;
    private static String username, password;

    public SyncLogin() {
        super(TAG);
    }

    public static void start(Context context, String username, String password) {
        Intent intent = new Intent(context, SyncLogin.class);
        context.startService(intent);
        SyncLogin.username = username;
        SyncLogin.password = password;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int serviceResult = RESULT_FAIL;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://engine.free.beeceptor.com/api/login");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("username", username);
        httpPost.setHeader("password", password);
        HttpResponse response = null;
        String responseString = null;
        try {
            response = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                response.getEntity().writeTo(out);
                responseString = out.toString();
                JSONObject jObject = new JSONObject(responseString);
                boolean success = jObject.getBoolean("success");
                if(success)
                    serviceResult = RESULT_SUCCESS;
                out.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC);
        broadcastIntent.putExtra(EXTRA_RESULT, serviceResult);
        sendBroadcast(broadcastIntent);

    }
}
