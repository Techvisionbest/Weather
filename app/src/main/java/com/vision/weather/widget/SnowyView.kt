package com.vision.weather.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver



class SnowyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = "SnowyView"
    private val snowFlakes = ArrayList<SnowFlake>()

    init {
        // 禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        for (i in 0..29){
            snowFlakes.add(SnowFlake.Builder().scopeX(weatherViewWidth.toFloat()).scopeY(weatherViewHeight.toFloat()).build())
        }
    }

    private val snowyManHeaderRadius = dp2px(12f)
    private val snowyManBodyRadius = dp2px(25f)
    private val snowyManHeaderX = centerX
    private val snowyManHeaderY = centerY + outRadius - snowyManHeaderRadius - snowyManBodyRadius * 2
    private val snowyManBodyX = centerX - dp2px(3f)
    private val snowyManBodyY = centerY + outRadius - snowyManBodyRadius - dp2px(5f)
    private val snowyManPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#e6e8db")
    }

    private val snowyManHandRect = RectF(centerX - dp2px(40f), centerY, centerX + dp2px(40f), snowyManBodyY - dp2px(8f))
    private val snowyManHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = dp2px(5f).toFloat()
        alpha = 120
    }



    private val outPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#e6e8db")
        maskFilter = BlurMaskFilter(shadowRadius.toFloat(), BlurMaskFilter.Blur.SOLID)
    }

    // 内圆paint
    private val innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(centerX - innerRadius, centerY + innerRadius, centerX, centerY - innerRadius,
                Color.parseColor("#e0e2e5"), Color.parseColor("#758595"), Shader.TileMode.CLAMP)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getWidth(widthMeasureSpec), getHeight(heightMeasureSpec))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null){
            canvas.drawCircle(centerX, centerY, outRadius.toFloat(), outPaint)
            canvas.drawCircle(centerX, centerY, innerRadius.toFloat(), innerCirclePaint)
            canvas.drawArc(snowyManHandRect, 155f,-120f,false, snowyManHandPaint)
            canvas.drawCircle(snowyManHeaderX, snowyManHeaderY, snowyManHeaderRadius.toFloat(), snowyManPaint)
            canvas.drawCircle(snowyManBodyX, snowyManBodyY, snowyManBodyRadius.toFloat(), snowyManPaint)
            for (snow in snowFlakes){
                snow.draw(canvas)
            }
            handler.postDelayed({
                invalidate()
            },5)
        }
    }
}