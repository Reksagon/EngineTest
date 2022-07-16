package com.engine.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.engine.test.adapter.Item;
import com.engine.test.adapter.RecyclerAdapter;
import com.engine.test.databinding.MainActivityBinding;
import com.engine.test.itemdetail.ItemDetail;
import com.engine.test.syncservice.SyncItems;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MainActivityBinding binding;
    private final IntentFilter broadcastFilter = new IntentFilter(SyncItems.ACTION_SYNC);
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int result = intent.getIntExtra(SyncItems.EXTRA_RESULT, SyncItems.RESULT_FAIL);
                if (result == SyncItems.RESULT_SUCCESS) {
                    ArrayList<Item> items = (ArrayList<Item>) intent.getSerializableExtra(SyncItems.EXTRA_ITEMS);
                    ArrayList<ItemDetail> itemDetails = (ArrayList<ItemDetail>) intent.getSerializableExtra(SyncItems.EXTRA_ITEMS_DETAIL);

                    RecyclerAdapter recyclerAdapter = new RecyclerAdapter(MainActivity.this, items, itemDetails, getSupportFragmentManager());
                    LinearLayoutManager linearLayout = new LinearLayoutManager(MainActivity.this);
                    linearLayout.setOrientation(RecyclerView.VERTICAL);
                    binding.recyclerItems.setLayoutManager(linearLayout);
                    binding.recyclerItems.setAdapter(recyclerAdapter);
                    binding.recyclerItems.addItemDecoration(new SpacesItemDecoration((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics())));
                    unregisterReceiver(this);
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        }
    };


    class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private int space;

        public SpacesItemDecoration(int space)
        {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            //добавить переданное кол-во пикселей отступа снизу
            outRect.bottom = space;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SyncItems.start(this);
        registerReceiver(broadcastReceiver, broadcastFilter);

    }
}