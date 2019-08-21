package com.vision.weather.widget

import android.view.View
import com.vision.weather.WeatherApplication

fun dp2px(dpValue: Float): Int{
    val scale = WeatherApplication.getContext().resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun getWidth(widthMeasureSpec: Int): Int{
    var result = 0
    val specMode = View.MeasureSpec.getMode(widthMeasureSpec)
    val specSize = View.MeasureSpec.getSize(widthMeasureSpec)
    when(specMode){
        View.MeasureSpec.UNSPECIFIED -> {
            result = specSize
        }
        View.MeasureSpec.AT_MOST -> {
            result = getContentWidth()
        }
        View.MeasureSpec.EXACTLY -> {
            result = Math.max(getContentWidth(), specSize)
        }
    }
    return result
}

fun getHeight(heightMeasureSpec: Int): Int{
    var result = 0
    val specMode = View.MeasureSpec.getMode(heightMeasureSpec)
    val specSize = View.MeasureSpec.getSize(heightMeasureSpec)
    when(specMode){
        View.MeasureSpec.UNSPECIFIED -> {
            result = specSize
        }
        View.MeasureSpec.AT_MOST -> {
            result = getContentHeight()
        }
        View.MeasureSpec.EXACTLY -> {
            result = Math.max(getContentHeight(), specSize)
        }
    }
    return result
}

fun getContentWidth(): Int{
    return weatherViewWidth
}

fun getContentHeight(): Int{
    return weatherViewHeight
}