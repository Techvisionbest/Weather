package com.vision.weather.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class SunnyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        // 禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    // 内圆paint
    private val innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(centerX - innerRadius, centerY + innerRadius, centerX, centerY - innerRadius,
            Color.parseColor("#fc5830"), Color.parseColor("#f98c24"), Shader.TileMode.CLAMP)
    }
    // 外圆paint
    private val outCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(centerX, centerY, outRadius.toFloat(), Color.parseColor("#e6e8db"),
            Color.parseColor("#c9e8de"), Shader.TileMode.CLAMP)
    }
    // 阴影paint
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#f98c24")
        maskFilter = BlurMaskFilter(shadowRadius.toFloat(), BlurMaskFilter.Blur.SOLID)
    }

    // 黄色圆的圆心横坐标
    private val opacityX = centerX + dp2px(60f)
    private val opacityY = centerY - dp2px(60f)
    private val opacityRadius = dp2px(30f)
    private val opacityPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ffeb3b")
    }

    // 光晕的圆心、半径信息
    private var sunshineX = opacityX
    private var sunshineY = opacityY
    private val sunshineRadius = dp2px(50f)
    private val sunshinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#19ffffff")
    }


    // 太阳的光晕动画
    private val animatorBig = ObjectAnimator.ofFloat(this, "sunshineY",
            opacityY, opacityY - dp2px(30f)).apply {
        repeatCount = -1
        repeatMode = ValueAnimator.REVERSE
        duration = 5000
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animatorBig.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorBig.cancel()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getWidth(widthMeasureSpec), getHeight(heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null){
            canvas.drawCircle(centerX, centerY, outRadius.toFloat(), shadowPaint)
            canvas.drawCircle(centerX, centerY, outRadius.toFloat(), outCirclePaint)
            canvas.drawCircle(centerX, centerY, innerRadius.toFloat(), innerCirclePaint)

            canvas.save()
            // 动画部分
            sunshinePaint.color = Color.parseColor("#19ffffff")
            canvas.drawCircle(sunshineX, sunshineY, sunshineRadius.toFloat(), sunshinePaint )
            sunshinePaint.color = Color.parseColor("#55ffffff")
            canvas.drawCircle(sunshineX + dp2px(20f), sunshineY + dp2px(18f), 30f, sunshinePaint)

            canvas.drawCircle(opacityX, opacityY, opacityRadius.toFloat() + dp2px(5f), outCirclePaint)
            canvas.drawCircle(opacityX, opacityY, opacityRadius.toFloat(), opacityPaint)
        }
    }

    private fun setSunshineY(y: Float){
        this.sunshineY = y
        sunshineX = calculateShadowX(dp2px(60f).toFloat(), sunshineY)
        invalidate()
    }

    // 计算光晕圆心纵坐标
    private fun calculateShadowX(moveRadius: Float ,y: Float): Float{
        val lengthY = opacityY - y
        val lengthX =  Math.sqrt(Math.pow(moveRadius.toDouble(),2.0) - Math.pow(lengthY.toDouble(),2.0))
        return (opacityX - lengthX).toFloat()
    }

}