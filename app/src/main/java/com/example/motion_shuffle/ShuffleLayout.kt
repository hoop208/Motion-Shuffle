/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.motion_shuffle

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.constraintlayout.motion.widget.TransitionBuilder
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams

class ShuffleLayout : MotionLayout {
    companion object {
        private const val TAG = "ShuffleLayout"
    }

    private var shuffleTransition: MotionScene.Transition? = null

    private val idCache:MutableMap<Int,Int> = mutableMapOf()

    private val startSetId = View.generateViewId()
    private val endSetId = View.generateViewId()

    private val startSet:ConstraintSet =  ConstraintSet()
    private val endSet:ConstraintSet =  ConstraintSet()

    private var preData = emptyList<ShuffleData>()
    private var currentData = emptyList<ShuffleData>()

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context, attrs, defStyleAttr)

    private fun initScene() {
        if(scene!=null){
            return
        }
        val scene = MotionScene(this)
        shuffleTransition = createTransition(scene)
        shuffleTransition?.duration = 3000

        scene.addTransition(shuffleTransition)
        scene.setTransition(shuffleTransition)
        setScene(scene)
    }

    private fun createTransition(scene: MotionScene): MotionScene.Transition {
        val transitionId = View.generateViewId()
//        startSet.clone(this)
//        endSet.clone(this)
        return TransitionBuilder.buildTransition(
            scene,
            transitionId,
            startSetId, startSet,
            endSetId, endSet)
    }

    fun shuffle(newDataList:List<ShuffleData>,animate:Boolean){
//        startSet.clone(endSet)
        currentData = newDataList
        initShuffleItems(this,newDataList)
        val maxCount = maxOf(preData.size, currentData.size).coerceAtLeast(childCount)
        updateConstraintSet(maxCount,preData,currentData, startSet)
        updateConstraintSet(maxCount,currentData,preData, endSet)
        if(preData.isNotEmpty()&&animate){
            initScene()
            animateWidget()
        }else{
            endSet.applyTo(this)
        }
        preData = newDataList
    }

    private fun initShuffleItems(layout: MotionLayout, newDataList:List<ShuffleData>) {
        //移除删除的view TODO
        val count = newDataList.size
        if (count == 0) {
            return
        }

        newDataList.forEachIndexed { index, data ->
            val shuffleView = getViewByPosition(index)
            shuffleView.id = getIdByPosition(index)
            shuffleView.text = data.text
            val nodeWidth = data.right - data.left
            val nodeHeight = data.bottom - data.top
            if(shuffleView.parent==null){
                Log.d(TAG,"width=$nodeWidth;height=$nodeHeight")
                val layoutParams = LayoutParams(nodeWidth, nodeHeight)
                layout.addView(shuffleView, layoutParams)
            }else{
                shuffleView.updateLayoutParams<android.view.ViewGroup.LayoutParams> {
                    width = nodeWidth
                    height = nodeHeight
                }
            }
        }
    }

    private fun updateConstraintSet(maxCount:Int,dataList: List<ShuffleData>,deltaList:List<ShuffleData>, constraintSet: ConstraintSet) {
        constraintSet.clone(this)
        val count = dataList.size
        if (count == 0) {
            return
        }
        for (index in 0 until maxCount){
            val data = dataList.getOrNull(index)
            val shuffleId = getIdByPosition(index)
            if(data==null){
                val deltaData = deltaList.getOrNull(index)
                if(deltaData==null){
                    constraintSet.setColorValue(shuffleId, "BackgroundColor", Color.TRANSPARENT)
                    constraintSet.connect(
                        shuffleId,
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START)
                    constraintSet.connect(
                        shuffleId,
                        ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP)
                    constraintSet.constrainWidth(shuffleId,0)
                    constraintSet.constrainHeight(shuffleId,0)
                    constraintSet.setMargin(shuffleId, ConstraintSet.START, 0)
                    constraintSet.setMargin(shuffleId, ConstraintSet.TOP, 0)
                }else{
                    val nodeWidth = deltaData.right - deltaData.left
                    val nodeHeight = deltaData.bottom - deltaData.top
                    constraintSet.setColorValue(shuffleId, "BackgroundColor", deltaData.bgColor)
                    constraintSet.connect(
                        shuffleId,
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START)
                    constraintSet.connect(
                        shuffleId,
                        ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP)
                    constraintSet.constrainWidth(shuffleId,0)
                    constraintSet.constrainHeight(shuffleId,0)
                    constraintSet.setMargin(shuffleId, ConstraintSet.START, deltaData.left+nodeWidth/2)
                    constraintSet.setMargin(shuffleId, ConstraintSet.TOP, deltaData.top+nodeHeight/2)
                }
            }else{
                val nodeWidth = data.right - data.left
                val nodeHeight = data.bottom - data.top
                constraintSet.setColorValue(shuffleId, "BackgroundColor", data.bgColor)
                constraintSet.connect(
                    shuffleId,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START)
                constraintSet.connect(
                    shuffleId,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP)
                constraintSet.constrainWidth(shuffleId,nodeWidth)
                constraintSet.constrainHeight(shuffleId,nodeHeight)
                constraintSet.setMargin(shuffleId, ConstraintSet.START, data.left)
                constraintSet.setMargin(shuffleId, ConstraintSet.TOP, data.top)
            }
        }

    }

    private fun createShuffleItem(context: Context): TextView {
        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.WHITE)
        return tv
    }

    private fun getIdByPosition(position:Int):Int{
        val id = idCache[position]
        if(id!=null){
            return id
        }
        val generatedId = View.generateViewId()
        idCache[position] = generatedId
        return generatedId
    }

    private fun getViewByPosition(position: Int): TextView {
        val child = getChildAt(position)
        if (child != null) {
            return child as TextView
        }
        return createShuffleItem(context)
    }

    private fun animateWidget() {
        setTransition(startSetId, endSetId)
        transitionToEnd()
    }

}

data class ShuffleData(
    val text:String,
    val bgColor:Int,
    val left:Int,
    val top:Int,
    val right:Int,
    val bottom:Int,
)
