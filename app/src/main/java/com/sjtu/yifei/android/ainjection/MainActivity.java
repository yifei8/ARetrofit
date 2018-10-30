package com.sjtu.yifei.android.ainjection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.sjtu.yifei.route.RouteService;
import com.sjtu.yifei.route.Routerfit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_TEST1 = 0x1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv_test1 = findViewById(R.id.tv_test1);
        TextView tv_test2 = findViewById(R.id.tv_test2);
        TextView tv_test1_for_result = findViewById(R.id.tv_test1_for_result);
        TextView tv_fragment = findViewById(R.id.tv_fragment);
        TextView tv_scheme = findViewById(R.id.tv_scheme);
        TextView tv_kotlin = findViewById(R.id.tv_kotlin);
        int[] i = new int[3];
        i[0]=1;
        i[1]=2;
        i[2]=3;

        tv_test1.setOnClickListener(v ->
                Routerfit.register(RouteService.class)
                        .launchTest1Activity("MainActivity ", 100,
                                (i1, o) ->
                                        Toast.makeText(MainActivity.this, "i:" + i1, Toast.LENGTH_LONG).show()
                        ));

        tv_test1_for_result.setOnClickListener(v -> Routerfit.register(RouteService.class).launchTest1ActivityForResult("MainActivity for result", 100, REQUEST_CODE_TEST1));
        tv_test2.setOnClickListener(v -> Routerfit.register(RouteService.class).launchTest2Activity("xxxx2", i));
        tv_fragment.setOnClickListener(v -> Routerfit.register(RouteService.class).launchFragmentActivity());
        tv_scheme.setOnClickListener(v -> Routerfit.register(RouteService.class).launchSchemeActivity("protocol://yifei.sjtu.com:8080/aretrofit?id=10011002"));
        tv_kotlin.setOnClickListener(v -> Routerfit.register(RouteService.class).launchBasicActivity("from mainactivity", 1002));
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
