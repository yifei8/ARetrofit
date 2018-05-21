package com.sjtu.yifei.test_module1;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.sjtu.yifei.annotation.Route;
import com.sjtu.yifei.route.RouteImpl;

@Route(path = "/test-module1/FragmentActivity")
public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment = RouteImpl.getTestFragment("from FragmentActivity", "xxxxaaaaa");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .commitAllowingStateLoss();
    }
}
