package com.mordecai.blurmemorytest

import android.R.attr.height
import android.R.attr.paddingEnd
import android.R.attr.paddingStart
import android.R.attr.width
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.util.Log.w
import android.view.Choreographer
import android.view.View

class ProgressView : View, Choreographer.FrameCallback {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, style: Int) : super(context, attrs, style)

    private var mLineMaxWidth = 0f
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mChoreographer = Choreographer.getInstance()
    private var mProgress = 0f

    // 用于切换测试模式
    private var mTestMode = TestMode.MEMORY_LEAK_MODE
    private var mFrameCount = 0

    enum class TestMode {
        MEMORY_LEAK_MODE,  // 复现内存暴涨问题（重复绘制）
        NORMAL_MODE        // 正常模式（单次绘制）
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        mGlowPaint.maskFilter = BlurMaskFilter(4f.dp2pxf, BlurMaskFilter.Blur.OUTER)
        initPaint(mPaint)
        initPaint(mGlowPaint)
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(p0: Long) {
                demoMode()
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    private fun initPaint(paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f.dp2pxf
        paint.strokeCap = Paint.Cap.ROUND
        paint.isDither = true
        paint.isFilterBitmap = true
    }

    fun demoMode() {
        mProgress = (1..360).random().toFloat()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mLineMaxWidth = width.toFloat() - paddingEnd - paddingStart - mPaint.strokeWidth

        val start = w / 2f
        val gradient = LinearGradient(
            start, 0f,
            start, h.toFloat(),
            intArrayOf(Color.parseColor("#3EC4FF"), Color.parseColor("#2D72FF")),
            floatArrayOf(0.35f, 0.65f),
            Shader.TileMode.CLAMP
        )

        mPaint.shader = gradient
        mGlowPaint.shader = gradient
        mGlowPaint.strokeWidth = 6f.dp2pxf

        // 关键：在onSizeChanged中设置BlurMaskFilter（复现原问题场景）

        Log.d("ProgressView", "onSizeChanged called - BlurMaskFilter created")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val heightFloat = height.toFloat()
        val strokeWidth = mPaint.strokeWidth
        val centerY = heightFloat / 2f
        val linePositionY = centerY
        val linePositionStartX = strokeWidth + paddingStart
        val lineEndX = linePositionStartX + mProgress

        when (mTestMode) {
            TestMode.MEMORY_LEAK_MODE -> {
                // 复现ProgressHorizontalView的问题：重复绘制相同的线条
                //canvas.drawArc(0f,0f, 100f, 100f, 0f, mProgress, false, mPaint)
                //canvas.drawArc(0f,0f, 100f, 100f, 0f, mProgress, false, mGlowPaint)
                canvas.drawLine(linePositionStartX, linePositionY, lineEndX, linePositionY, mPaint)
                canvas.drawLine(linePositionStartX, linePositionY, lineEndX, linePositionY, mGlowPaint)

                // 记录每1000帧输出一次日志
                if (mFrameCount % 1000 == 0) {
                    Log.w("ProgressView", "MEMORY_LEAK_MODE - 重复绘制模式，帧数: $mFrameCount")
                }
            }

            TestMode.NORMAL_MODE -> {
                // 正常模式：只绘制一次
                canvas.drawLine(linePositionStartX, linePositionY, lineEndX, linePositionY, mGlowPaint)

                if (mFrameCount % 1000 == 0) {
                    Log.i("ProgressView", "NORMAL_MODE - 正常绘制模式，帧数: $mFrameCount")
                }
            }
        }

        mFrameCount++
    }

    override fun doFrame(frameTimeNanos: Long) {
        // 模拟进度条动画
        if (mProgress >= mLineMaxWidth) {
            mProgress = 0f
        }
        mProgress += 2f // 每帧增加2像素

        invalidate()
        mChoreographer.postFrameCallback(this)
    }

    // 用于切换测试模式的公共方法
    fun switchTestMode() {
        mTestMode = when (mTestMode) {
            TestMode.MEMORY_LEAK_MODE -> TestMode.NORMAL_MODE
            TestMode.NORMAL_MODE -> TestMode.MEMORY_LEAK_MODE
        }
        Log.d("ProgressView", "切换到测试模式: ${mTestMode.name}")
        invalidate()
    }

    fun getCurrentTestMode(): TestMode = mTestMode

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mChoreographer.removeFrameCallback(this)
        Log.d("ProgressView", "View已从窗口分离，停止动画")
    }
}