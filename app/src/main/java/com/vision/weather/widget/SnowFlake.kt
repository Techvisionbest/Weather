package com.vision.weather.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import kotlin.random.Random

/**
 *  snowyView 中飘落的雪花实体
 */
class SnowFlake(
        var radius: Float?,
        val speed: Float?,
        val angle: Float?,
        val moveScopeX: Float?,
        val moveScopeY: Float?
        ) {
    private val TAG = "SnowFlake"
    private val random = java.util.Random()
    private var presentX = random.nextInt(moveScopeX?.toInt() ?: 0).toFloat()
    private var presentY = random.nextInt(moveScopeY?.toInt() ?: 0).toFloat()
    private var presentSpeed = getSpeed()
    private var presentAngle = getAngle()
    private var presentRadius = getRadius()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#e6e8db")
        alpha = 100
    }

    // 绘制雪花
    fun draw(canvas: Canvas){
        moveX()
        moveY()
        if (moveScopeX != null && moveScopeY != null){
            if (presentX > moveScopeX || presentY > moveScopeY || presentX < 0 || presentY < 0){
                reset()
            }
        }
        canvas.drawCircle(presentX, presentY, presentRadius, paint)
    }
    // 移动雪花（x轴方向）
    fun moveX(){
        presentX += getSpeedX()
    }
    // 移动雪花（Y轴方向）
    fun moveY(){
        presentY += getSpeedY()
    }

    fun getSpeed(): Float{
        var result: Float
        speed.let {
            result = it ?: (random.nextFloat() + 1)
        }
        Log.e(TAG, "speed: $result")
        return result
    }

    fun getRadius(): Float{
        var size: Float
        radius.let {
            size = it ?: random.nextInt(15).toFloat()
        }
        return size
    }

    fun getAngle(): Float{
        angle.let {
            if (it != null){
                if (it > 30){
                    return 30f
                }
                if (it < 0){
                    return 0f
                }
                return it
            }else{
                return random.nextInt(30).toFloat()
            }
        }
    }

    fun getSpeedX(): Float{
        return (presentSpeed * Math.sin(presentAngle.toDouble())).toFloat()
    }

    fun getSpeedY(): Float{
        return (presentSpeed * Math.cos(presentAngle.toDouble())).toFloat()
    }

    fun reset(){
        presentSpeed = getSpeed()
        presentAngle = getAngle()
        presentRadius = getRadius()
        presentX = random.nextInt(moveScopeX?.toInt()?:0).toFloat()
        presentY = 0f
    }

    data class Builder(
            var mRadius: Float? = null,
            var mSpeed: Float? = null,
            var mAngle: Float? = null,
            var moveScopeX: Float? = null,
            var moveScopeY: Float? = null
            ){
        fun radius(radius: Float) = apply { this.mRadius = radius }
        fun speed(speed: Float) = apply { this.mSpeed = speed }
        fun angle(angle: Float) = apply { this.mAngle = angle }
        fun scopeX(scope: Float) = apply { this.moveScopeX = scope }
        fun scopeY(scope: Float) = apply { this.moveScopeY = scope }
        fun build() = SnowFlake(mRadius, mSpeed, mAngle, moveScopeX, moveScopeY)
    }
}