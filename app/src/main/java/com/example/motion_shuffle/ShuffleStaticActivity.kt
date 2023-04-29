package com.example.motion_shuffle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.constraintlayout.motion.widget.MotionLayout

class ShuffleStaticActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shuffle_static)
        val motionLayout = findViewById<MotionLayout>(R.id.motion_layout)
        findViewById<Button>(R.id.transition_start).setOnClickListener {
            motionLayout.transitionToStart()
        }

        findViewById<Button>(R.id.transition_end).setOnClickListener {
            motionLayout.transitionToEnd()
        }
    }
}