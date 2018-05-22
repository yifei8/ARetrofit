package com.sjtu.yifei.test_module2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sjtu.yifei.annotation.Route;
import com.sjtu.yifei.route.RouteImpl;

@Route(path = "/test-module2/Test2Activity")
public class Test2Activity extends AppCompatActivity {

    private static final String TAG = "Test2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            String para1 = intent.getStringExtra("para1");
//            int para2 = intent.getIntExtra("para2", -1);
            int[] para2 = intent.getIntArrayExtra("para2");
            Log.e(TAG, "para1:" + para1 + ",para2:" + para2[0] + ", " + para2[1]);
        }


        TextView textView = findViewById(R.id.tv_test1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteImpl.launchTest1Activity("from Test2Activity", 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
