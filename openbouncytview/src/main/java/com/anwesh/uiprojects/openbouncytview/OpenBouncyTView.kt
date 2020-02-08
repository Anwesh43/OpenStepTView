package com.anwesh.uiprojects.openbouncytview

/**
 * Created by anweshmishra on 08/02/20.
 */

import android.view.MotionEvent
import android.view.View
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val lines : Int = 2
val scGap : Float = 0.02f / lines
val delay : Long = 20
val strokeFactor : Int = 90
val foreColor : Int = Color.parseColor("#4CAF50")
val backColor : Int = Color.parseColor("#BDBDBD")
val deg : Float = 90f
val tSizeFactor : Float = 3f
val sizeFactor : Float = 2.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawOpenBouncyLine(i : Int, scale : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify().divideScale(i, lines)
    val tSize : Float = size / tSizeFactor
    save()
    translate(0f, -size)
    rotate(deg * sf * (1f - 2 * i))
    drawLine(0f, 0f, 0f, tSize, paint)
    restore()
}

fun Canvas.drawOpenBouncyT(scale : Float, size : Float, paint : Paint) {
    drawLine(0f, 0f, 0f, -size, paint)
    for (j in 0..(lines - 1)) {
        drawOpenBouncyLine(j, scale, size, paint)
    }
}

fun Canvas.drawOBTNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawOpenBouncyT(scale, size, paint)
    restore()
}

