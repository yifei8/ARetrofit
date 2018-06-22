package com.sjtu.yifei.kotlin_module

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sjtu.yifei.annotation.Route

@Route(path = "/kotlin-module/KotlinActivity")
class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
    }
}
