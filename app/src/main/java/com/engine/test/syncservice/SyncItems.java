package com.engine.test.syncservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.engine.test.adapter.Item;
import com.engine.test.itemdetail.ItemDetail;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SyncItems extends IntentService {

    private final static String TAG = "SyncItems";
    public final static String EXTRA_RESULT = "result";
    public final static String EXTRA_ITEMS = "items";
    public final static String EXTRA_ITEMS_DETAIL = "items_detail";
    public final static String ACTION_SYNC = "com.engine.test.syncservice.SyncItems.ACTION_SYNC";
    public final static int RESULT_SUCCESS = 11;
    public final static int RESULT_FAIL = 22;

    public SyncItems() {
        super(TAG);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SyncItems.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int serviceResult = RESULT_FAIL;
        ArrayList<Item> items = new ArrayList<>();

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("https://engine.free.beeceptor.com/api/getServices");
        httpGet.setHeader("Accept", "application/json");
        HttpResponse response = null;
        String responseString = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                response.getEntity().writeTo(out);
                responseString = out.toString();
                JSONArray jArray = new JSONArray(responseString);
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        int id = oneObject.getInt("id");
                        String name = oneObject.getString("name");
                        items.add(new Item(id, name));
                    } catch (JSONException e) {
                        // Oops
                    }
                }
                serviceResult = SyncItems.RESULT_SUCCESS;
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

        ArrayList<ItemDetail> itemDetails = getItemDetails(items);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_SYNC);
        broadcastIntent.putExtra(EXTRA_RESULT, serviceResult);
        broadcastIntent.putExtra(EXTRA_ITEMS, items);
        broadcastIntent.putExtra(EXTRA_ITEMS_DETAIL, itemDetails);
        sendBroadcast(broadcastIntent);

    }

    private ArrayList<ItemDetail> getItemDetails(ArrayList<Item> items) {
        ArrayList<ItemDetail> itemDetails = new ArrayList<>();

        for(Item item : items) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("https://engine.free.beeceptor.com/api/getSportDetails?sportId=" + item.getId());
            httpGet.setHeader("Accept", "application/json");
            HttpResponse response = null;
            String responseString = null;
            try {
                response = httpclient.execute(httpGet);
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
                    String name = jObject.getString("name");
                    String address = jObject.getString("address");
                    String phone = jObject.getString("phone");

                    String price = "";
                    try {
                        price = jObject.getString("price") + " " + jObject.getString("currency");
                    }
                    catch (JSONException e)
                    {}

                    itemDetails.add(new ItemDetail(name, address, phone, price));

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
        }

        return itemDetails;
    }
}
