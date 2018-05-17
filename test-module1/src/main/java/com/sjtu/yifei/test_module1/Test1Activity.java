package com.sjtu.yifei.test_module1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.sjtu.yifei.annotation.Route;

@Route(path = "/test-module1/Test1Activity")
public class Test1Activity extends AppCompatActivity {

    private static final String TAG = "Test1Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            String para1 = intent.getStringExtra("para1");
            int para2 = intent.getIntExtra("para2", -1);
            Log.e(TAG, "para1:" + para1 + ",para2:" + para2);
        }

        TextView textView = findViewById(R.id.tv_finish);
        textView.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("result_para", "this is test set result ok");
            setResult(RESULT_OK, result);
            finish();
        });
    }

}
