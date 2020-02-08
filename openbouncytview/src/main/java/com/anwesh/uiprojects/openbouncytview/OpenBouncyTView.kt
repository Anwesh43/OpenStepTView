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

class OpenBouncyTView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {


        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class OBTNode(var i : Int, val state : State = State()) {

        private var next : OBTNode? = null
        private var prev : OBTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = OBTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawOBTNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : OBTNode {
            var curr : OBTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class OpenBouncyT(var i : Int) {

        private val root : OBTNode = OBTNode(0)
        private var curr : OBTNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : OpenBouncyTView) {

        private val animator : Animator = Animator(view)
        private val obt : OpenBouncyT = OpenBouncyT(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            obt.draw(canvas, paint)
            animator.animate {
                obt.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            obt.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : OpenBouncyTView {
            val view : OpenBouncyTView = OpenBouncyTView(activity)
            activity.setContentView(view)
            return view
        }
    }
}