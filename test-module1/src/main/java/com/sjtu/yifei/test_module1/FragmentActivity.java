package com.sjtu.yifei.test_module1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sjtu.yifei.annotation.Route;
import com.sjtu.yifei.route.RouteService;
import com.sjtu.yifei.route.Routerfit;

@Route(path = "/test-module1/FragmentActivity")
public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int[] i = new int[3];
        i[0]=1;
        i[1]=2;
        i[2]=3;
        Fragment fragment = Routerfit.register(RouteService.class).getTestFragment("from FragmentActivity", i);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder, fragment)
                .commitAllowingStateLoss();
    }
}
