    package com.engine.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.engine.test.databinding.LoginActivityBinding;
import com.engine.test.syncservice.SyncLogin;

public class LoginActivity extends AppCompatActivity {

    LoginActivityBinding binding;
    ProgressDialog progressDialog;
    private final IntentFilter broadcastFilter = new IntentFilter(SyncLogin.ACTION_SYNC);
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int result = intent.getIntExtra(SyncLogin.EXTRA_RESULT, SyncLogin.RESULT_FAIL);
                if (result == SyncLogin.RESULT_SUCCESS) {
                    progressDialog.dismiss();
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bttnLogin.setOnClickListener(v ->
        {
            if (binding.password.getText().length() > 0 && binding.userName.getText().length() > 0) {
                SyncLogin.start(this, binding.userName.getText().toString(), binding.password.getText().toString());
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Loading. Wait...");
                progressDialog.show();
            }
            else
                Toast.makeText(LoginActivity.this, "Enter username and password", Toast.LENGTH_SHORT).show();
        });

        registerReceiver(broadcastReceiver, broadcastFilter);
    }
}