package com.sjtu.yifei.kotlin_module

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sjtu.yifei.annotation.Route
import kotlinx.android.synthetic.main.activity_basic.*
import kotlinx.android.synthetic.main.content_basic.*

@Route(path = "/kotlin-module/BasicActivity")
class BasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
        setSupportActionBar(toolbar)
        val param1 = intent.getStringExtra("param1")
        val param2 = intent.getIntExtra("param2", -1)
        val intentExtra = "intentExtra para1:$param1 ,param2:$param2"

        tv_params.text = intentExtra
    }

}
