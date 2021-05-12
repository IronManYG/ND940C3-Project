package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    private val textSize = resources.getDimension(R.dimen.textSize)

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClippedRectangle(canvas)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawClippedRectangle(canvas: Canvas) {
        canvas.clipRect(
            0F,0F,
            widthSize.toFloat(),heightSize.toFloat()
        )

        canvas.drawColor(resources.getColor(R.color.colorPrimary))

        paint.color = Color.WHITE
        // Align the RIGHT side of the text with the origin.
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            context.getString(R.string.button_loading),
                (widthSize/2).toFloat(),(heightSize/2).toFloat(),paint
        )

        paint.color = resources.getColor(R.color.colorAccent)
        canvas.drawCircle(
                (widthSize/1.4).toFloat(),(heightSize/2).toFloat(),
                30F,paint
        )
    }

}