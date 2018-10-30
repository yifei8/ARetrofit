package com.sjtu.yifei.test_module2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sjtu.yifei.annotation.Route;
import com.sjtu.yifei.route.ActivityCallback;
import com.sjtu.yifei.route.RouteService;
import com.sjtu.yifei.route.Routerfit;

@Route(path = "/login-module/Test2Activity")
public class Test2Activity extends AppCompatActivity {

    private static final String TAG = "Test2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Test2Activity");

        String intentExtra = "";
        Intent intent = getIntent();
        if (intent != null) {
            String para1 = intent.getStringExtra("para1");
            int[] para2 = intent.getIntArrayExtra("para2");
            intentExtra = "intentExtra para1:" + para1 + ",para2:[" + para2[0] + ", " + para2[1] + ", " + para2[2] + "]";
            Log.e(TAG, "para1:" + para1 + ",para2:" + para2[0] + ", " + para2[1]);
        }

        TextView tv_intent_desc = findViewById(R.id.tv_intent_desc);
        tv_intent_desc.setText(intentExtra);

        TextView tv_provider_desc = findViewById(R.id.tv_provider_desc);

        Button textView = findViewById(R.id.tv_test1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Routerfit.register(RouteService.class).launchTest1Activity("from Test2Activity", 100, new ActivityCallback() {
                    @Override
                    public void onActivityResult(int i, Object o) {
                        Toast.makeText(Test2Activity.this, "i:" + i + ", data:" + o, Toast.LENGTH_LONG).show();
                    }
                });
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
