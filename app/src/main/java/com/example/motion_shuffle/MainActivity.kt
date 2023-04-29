package com.example.motion_shuffle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.shuffle_static).setOnClickListener {
            val intent = Intent(this, ShuffleStaticActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.shuffle_dynamic).setOnClickListener {
            val intent = Intent(this, ShuffleDynamicActivity::class.java)
            startActivity(intent)
        }
    }
}