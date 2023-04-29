package com.example.motion_shuffle

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import kotlin.math.absoluteValue
import kotlin.random.Random

class ShuffleDynamicActivity : AppCompatActivity() {

    private lateinit var shuffleContainer: FrameLayout
    private lateinit var shuffleLayout: ShuffleLayout
    private lateinit var numberInput: EditText
    private lateinit var switchAnimate: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shuffle_dynamic)
        shuffleLayout = findViewById(R.id.motion_layout)
        numberInput = findViewById(R.id.input_number)
        shuffleContainer = findViewById(R.id.motion_container)
        switchAnimate = findViewById(R.id.switch_animate)
        findViewById<Button>(R.id.shuffle).setOnClickListener {
            val number = numberInput.text.toString().toIntOrNull() ?: 30
            shuffle(number)
        }

        shuffleLayout.setTransitionListener(object : TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                Log.d(TAG,"onTransitionStarted")
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                Log.d(TAG,"onTransitionChange:progress=$progress")
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                Log.d(TAG,"onTransitionStarted")
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
                Log.d(TAG,"onTransitionTrigger")
            }
        })
    }

    private fun shuffle(number: Int) {
        val newDataList = mutableListOf<ShuffleData>()
        for (index in 0 until number){
            val left = Random.nextInt(shuffleContainer.width)
            val top = Random.nextInt(shuffleContainer.height)
            val width = Random.nextInt(shuffleContainer.width - left).coerceAtMost(600)
            val height = Random.nextInt(shuffleContainer.height - top).coerceAtMost(600)
            val data = ShuffleData(
                text = index.toString(),
                bgColor = randomColor(),
                left = left,
                top = top,
                right = left+width,
                bottom = top+height,
            )
            Log.d(TAG,"shuffle:number=$number;data=$data")
            newDataList.add(data)
        }
        shuffleLayout.shuffle(newDataList,switchAnimate.isChecked)
    }

    @SuppressLint("NewApi")
    fun randomColor():Int{
        return Color.argb(
            1f,
            Random.nextFloat().absoluteValue,
            Random.nextFloat().absoluteValue,
            Random.nextFloat().absoluteValue,

        )
    }

    companion object{
        private const val TAG = "ShuffleDynamicActivity"
    }

}