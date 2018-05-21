package com.sjtu.yifei.android.ainjection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.sjtu.yifei.route.RouteImpl;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_TEST1 = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv_test1 = findViewById(R.id.tv_test1);
        TextView tv_test2 = findViewById(R.id.tv_test2);
        TextView tv_test1_for_result = findViewById(R.id.tv_test1_for_result);
        TextView tv_fragment = findViewById(R.id.tv_fragment);
        tv_test1.setOnClickListener(v -> RouteImpl.launchTest1Activity("xxx1", 100));
        tv_test1_for_result.setOnClickListener(v -> RouteImpl.launchTest1ActivityForResult("xxx1", 100, REQUEST_CODE_TEST1));
        tv_test2.setOnClickListener(v -> RouteImpl.launchTest2Activity("xxxx2", 5000));
        tv_fragment.setOnClickListener(v -> RouteImpl.launchFragmentActivity());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TEST1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String result = data.getStringExtra("result_para");
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
