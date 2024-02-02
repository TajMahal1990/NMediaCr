package ru.netology.nmedia.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.TypedArrayUtils.getResourceId
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
        }
    }

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

//    private val paintCircle = Paint(
//        Paint.ANTI_ALIAS_FLAG
//    ).apply {
//        style = Paint.Style.FILL
//        strokeWidth = lineWidth
//        color = colors.getOrNull(0) ?: randomColor()
//    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private val listAngle = listOf(-90F, 0F, 90F, 180F)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var sumPercents = 0F
        var indexStart = 0
        for (i in data.indices) {
            if(data[i]==0F) {
                indexStart=i
                break
            }
        }
     //   var startFrom = (indexStart*90F) - 90F
        for (i  in data.indices) {
            val angle = 360F / data.size

            if(data[indexStart]==0F) {
                paint.color = resources.getColor(R.color.grey)
                sumPercents -= (1 / data.size.toFloat())
            } else {
                paint.color = colors.getOrNull(indexStart) ?: randomColor()
            }
                canvas.drawArc(oval, listAngle[indexStart], angle, false, paint)
           //     startFrom += angle
                indexStart++
                if(indexStart==data.size) {
                    indexStart=0
                }

            sumPercents += (1 / data.size.toFloat())
        }

    //    canvas.drawCircle(center.x- radius, center.y, paintCircle.strokeWidth / 2F, paintCircle)

        canvas.drawText(
            //   "%.2f%%".format(data.sum() * 100),
            "%.2f%%".format(sumPercents * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}