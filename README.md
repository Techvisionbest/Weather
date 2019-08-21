> 本篇文章参考自[【不可思议的CSS】天气不可能那么可爱](https://juejin.im/post/5d2f3f3351882556c3186f57)，使用Android中的自定义View实现类似的效果。   

## 前言
这段时间一直在研究自定义View，恰好看到使用CSS实现的天气效果很不错，遂尝试使用自定义View实现一发。  
不要脸的套用原作者的一句话，希望原作者不要揍我～ 
> 只有你想不到，没有**自定义View**实现不了的。今日分享由**自定义View**实现的效果 - **Weather**  

## 效果

![](https://user-gold-cdn.xitu.io/2019/8/21/16cb31c5baadb645?w=800&h=387&f=gif&s=291787)
*今我来思，雨雪霏霏*
***   
![](https://user-gold-cdn.xitu.io/2019/8/21/16cb31d7fade93cf?w=800&h=387&f=gif&s=319101)

*晴空一鹤排云上，便引诗情到碧霄。*  
***   
由于不可抗力原因（懒）,这里只实现了晴、雪两种天气效果。原作者文章里实现了晴、雪、云、雨、超级月亮？(原文Supermoon,本人水平有限，实在不知道怎么翻译)，有兴趣的读者可以自行实现。
## 源码
两种天气效果的实现源码均已上传至[Github - Weather](https://github.com/Techvisionbest/Weather),客官请自取，如果恰好赶上铁汁您心情好，不妨点个**star**。
## 分析
接下来按照惯例分析一波项目里使用的重要API：
### BlurMaskFilter
首先我们来看看源码中的注释是怎么描述的：
```
/**
 * This takes a mask, and blurs its edge by the specified radius. Whether or
 * or not to include the original mask, and whether the blur goes outside,
 * inside, or straddles, the original mask's border, is controlled by the
 * Blur enum.
 */
 /* 翻译成大白话的意思就是BlurMaskFilter可以在原本的View上添加一层指定模糊半径的蒙层，具体模糊的方式，由Blur枚举类型控制 */
```
这里我们用BlurMaskFilter实现阴影效果～  
### LinearGradient 线性渐变
Android系统里的LinearGradient是paint的一种shader（着色器）方案。LinearGradient指的就是线性渐变：设置两个点和两种颜色，以这两个点作为端点，使用两种颜色的渐变来绘制颜色，大概像下面这样：  
![](https://user-gold-cdn.xitu.io/2019/8/21/16cb31e2b0a9b93d?w=237&h=224&f=png&s=53133)
*辐射渐变（图片源自抛物线Hencoder）*
*** 
我们这里星球的颜色全部都是通过指定paint的shader为LinearGradient实现的。
## 一起画
上面介绍了部分重要的API，接下来我们来一步一步实现 **Snowy** 的效果
### step1:绘制光晕
做一个黑色背景，因为黑色视觉反差大视觉效果杠杠的，这里先画一个圆使其位于Canvas画布中心位置，再使用**BlurMaskFilter**作出阴影，达成光晕的效果：

![](https://user-gold-cdn.xitu.io/2019/8/21/16cb31fd9dae73c5?w=2232&h=1080&f=jpeg&s=54500)

```
// 光晕的paint
private val outPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // 光晕的颜色
        color = Color.parseColor("#e6e8db")
        // 使用BlurMaskFilter制作阴影效果
        maskFilter = BlurMaskFilter(shadowRadius.toFloat(), BlurMaskFilter.Blur.SOLID)
    }
/** 以下代码是在onDraw()方法中 */
canvas.drawColor(Color.BLACK)
canvas.drawCircle(centerX, centerY, outRadius.toFloat(), outPaint)
```
### step2:画一个圆
这里使用**LinearGradient**做一个渐变的圆，并位于Canvas画布中心位置，与step1中的光晕形成同心圆，这样立刻就有一个不灵不灵的效果了～

![](https://user-gold-cdn.xitu.io/2019/8/21/16cb3227c6ff679e?w=2232&h=1080&f=jpeg&s=58745)
```
private val innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(centerX - innerRadius, centerY + innerRadius, centerX, centerY - innerRadius,
                Color.parseColor("#e0e2e5"), Color.parseColor("#758595"), Shader.TileMode.CLAMP)
}
/** 以下代码是在onDraw()方法中 */
if (canvas != null){
// 绘制黑色背景     
canvas.drawColor(Color.BLACK)
// 绘制渐变圆
canvas.drawCircle(centerX, centerY, innerRadius.toFloat(), innerCirclePaint)
}
```

> 注意，当设置了paint的**shader**属性后，paint的**color**属性就会失效，也就是说，当设置了 Shader 之后，Paint 在绘制图形和文字时就不使用 setColor/ARGB() 设置的颜色了，而是使用 Shader 的方案中的颜色。


### step3:画雪人的手臂
我们这里使用drawArc绘制一段圆弧：
![](https://user-gold-cdn.xitu.io/2019/8/21/16cb3238b87c3633?w=2232&h=1080&f=jpeg&s=59491)
```
// 确认雪人手臂位置的Rect
private val snowyManHandRect = RectF(centerX - dp2px(40f), centerY, centerX + dp2px(40f), snowyManBodyY - dp2px(8f))
// 雪人手臂的paint
private val snowyManHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = dp2px(5f).toFloat()
        alpha = 120
    }
 /** 以下代码是在onDraw()方法中 */
 if (canvas != null){
        canvas.drawColor(Color.BLACK)
        canvas.drawCircle(centerX, centerY, outRadius.toFloat(), outPaint)
        canvas.drawCircle(centerX, centerY, innerRadius.toFloat(), innerCirclePaint)
        canvas.drawArc(snowyManHandRect, 155f,-120f,false, snowyManHandPaint)
}
```
看到这里有人会说，这是什么鬼啊，哪里像雪人的手臂啦，别急，我们“走着瞧”
### step4:画雪人身体
雪人的身体是由两个相切（感谢我的数学老师，我竟然还记得这么专业的数学名词）的大小不同的圆组成：
![](https://user-gold-cdn.xitu.io/2019/8/21/16cb338ff0703bda?w=2232&h=1080&f=jpeg&s=59610)
```
private val snowyManHeaderRadius = dp2px(12f)
private val snowyManBodyRadius = dp2px(25f)
private val snowyManHeaderX = centerX
private val snowyManHeaderY = centerY + outRadius - snowyManHeaderRadius - snowyManBodyRadius * 2
    private val snowyManBodyX = centerX - dp2px(3f)
    private val snowyManBodyY = centerY + outRadius - snowyManBodyRadius - dp2px(5f)
    private val snowyManPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#e6e8db")
    }
     /** 以下代码是在onDraw()方法中 */
    canvas.drawCircle(snowyManHeaderX, snowyManHeaderY, snowyManHeaderRadius.toFloat(), snowyManPaint)
            canvas.drawCircle(snowyManBodyX, snowyManBodyY, snowyManBodyRadius.toFloat(), snowyManPaint)
```
> 这亚子第三步中画的雪人手臂像手臂了吧，哼～

### step5:画一朵飘落的雪花
画雪花之前我们需要想象一下雪花在现实生活中的表现是什么样的：
1. 大小不一
2. 下落的速度不一
3. 受风力等的影响水平速度不一 
4. 下落的初始位置不同

再结合我们设备的信息，我们知道，雪花在设备上飘落时会有一个运动的范围，这个范围取决于它的父布局的宽和高。
综合以上信息，我们可以画出雪花的类图：
![](https://user-gold-cdn.xitu.io/2019/8/21/16cb339d6289307c?w=340&h=380&f=png&s=10599)
代码如下：
```
/**
 *  snowyView 中飘落的雪花实体
 *  使用Builder模式构造
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
        return result
    }
    // 获取雪花大小
    fun getRadius(): Float{
        var size: Float
        radius.let {
            size = it ?: random.nextInt(15).toFloat()
        }
        return size
    }
    // 获取雪花下落角度
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
    // 获取雪花x轴的速度
    fun getSpeedX(): Float{
        return (presentSpeed * Math.sin(presentAngle.toDouble())).toFloat()
    }
    // 获取雪花Y轴的速度
    fun getSpeedY(): Float{
        return (presentSpeed * Math.cos(presentAngle.toDouble())).toFloat()
    }
    // 充值雪花位置
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
```
### step6：让一群雪花动起来！
这里我们随机构造出30个雪花，他们的速度、大小、下落角度、初始位置都是随机生成的，然后在SnowyView的onDraw()方法中绘制出来，并每隔5ms就刷新一次View，由于雪花的位置是不停变换的，视觉上就形成了雪花纷纷扬扬的效果：
![](https://user-gold-cdn.xitu.io/2019/8/21/16cb33a5e71d57a6?w=800&h=387&f=gif&s=291787)
*雪花纷纷何所似？——未若柳絮因风起*
***
```
// snowFlakes 为包含30个雪花的数组
for (snow in snowFlakes){
                snow.draw(canvas)
            }
            handler.postDelayed({
                invalidate()
            },5)
```  
## 待优化
* 因不可抗力原因（还是懒），代码中许多变量命名略显随意
* 雪花纷纷扬扬实现遵循简单的原则，没有考虑重力等因素的影响，如果把这些都考虑进去，实现出来的效果应该会更优秀
* 还有4个天气效果没有实现
* 雪花实体类SnowyFlake使用kotlin实现Builder模式总感觉怪怪的，望能有大佬指点一二，不胜感激～  
## 总结
感谢原作者**D文斌**的文章：[【不可思议的CSS】天气不可能那么可爱](https://juejin.im/post/5d2f3f3351882556c3186f57)  
感谢扔物线大神的HenCoder系列文章（刚看到扔物线大佬的blog竟然更新了）  
