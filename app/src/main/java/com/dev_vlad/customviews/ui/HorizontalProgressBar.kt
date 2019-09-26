package com.dev_vlad.customviews.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.dev_vlad.customviews.R










/**
 * TODO: document your custom view class.
 */
class HorizontalProgressBar : View {

    private var progressSteps: List<Rect>? = null
    private var colorPBBg :Int? = null
    private var colorPBStart :Int? =  null
    private var colorPBMidLeft :Int? = null
    private var colorPBMidRight:Int? = null
    private var colorPBEnd:Int? = null

    private var outlineWidth: Float = 0f
    private var stepWidth: Float = 0f
    private var barHeight: Float = 0f

    // the progress
    var progress : Int? = null
    private var animatedProgress : Int = 1

    private var clearCanvasFlag = false


    fun incrementProgress(){
        progress = progress!! + 1
        invalidate()
    }


    constructor(context: Context) : super(context) { init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.HorizontalProgressBar, defStyle, 0
        )


        progress = a.getInteger(R.styleable.HorizontalProgressBar_progress, 0)

        a.recycle()

        outlineWidth  = 6f
        stepWidth = 25f
        barHeight = 24f


        setupColors()


    }

    private fun getFillColor(forStep : Int) : Paint{
        val paintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFill.style = Paint.Style.FILL

        when {
            forStep < 2 -> paintFill.color = colorPBStart!!
            forStep < 5 -> paintFill.color = colorPBMidLeft!!
            forStep < 7 -> paintFill.color = colorPBMidRight!!
            else -> paintFill.color = colorPBEnd!! //default color
        }


        return paintFill

    }


    private fun setupColors() {
        colorPBBg = ContextCompat.getColor(context, R.color.colorPBBg)
        colorPBStart  = ContextCompat.getColor(context, R.color.colorPBStart)
        colorPBMidLeft  = ContextCompat.getColor(context, R.color.colorPBMidLeft)
        colorPBMidRight = ContextCompat.getColor(context, R.color.colorPBMidRight)
        colorPBEnd = ContextCompat.getColor(context, R.color.colorPBEnd)

    }

    private fun setupHorizontalBar() {
        progressSteps = List(animatedProgress) { Rect() }
        progressSteps!!.forEachIndexed{ index, stepBar ->

            val leftCorner : Int = (index * stepWidth).toInt()
            stepBar.set(leftCorner,0,(leftCorner + stepWidth).toInt(), barHeight.toInt())

        }


    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldw: Int, oldh: Int) {

        stepWidth = (newWidth / 10).toFloat()
        barHeight = newHeight.toFloat()

        setupHorizontalBar()
        animateWithTimer()

    }

    private fun animateWithTimer(){

        val handler = Handler()
        val delay : Long = 1000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {
                animatedProgress ++
                if(animatedProgress > progress!!) {
                    animatedProgress = 1
                    clearCanvasFlag = true
                }else
                    clearCanvasFlag = false
                setupHorizontalBar()
                invalidate()
                Log.d(animatedProgress.toString(), progress.toString() + " progress")
                handler.postDelayed(this, delay)
            }
        }, delay)

    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(clearCanvasFlag)
            canvas.drawColor(colorPBBg!!)

        else {
            progressSteps!!.forEachIndexed { index, stepBar ->

                val fillPaint = getFillColor(index)
                canvas.drawRect(stepBar, fillPaint)


            }
        }

    }
}
