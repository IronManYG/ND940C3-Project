package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_LOADING_CIRCLE_COLOR = R.color.colorAccent
        private const val DEFAULT_WAITING_RECT_COLOR = R.color.colorPrimary
        private const val DEFAULT_LOADING_RECT_COLOR = R.color.colorPrimaryDark
        private const val DEFAULT_TEXT_COLOR = R.color.white
    }

    private var isChecked = false

    // Progress of downloading
    private var progress = 0

    // LoadingButton dimensions
    private var widthSize = 0
    private var heightSize = 0

    // Shapes
    private val waitingRect: Rect = Rect()

    private val loadingRect: Rect = Rect()

    // Paints
    private val waitingPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val loadingPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textValuePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val loadingArcPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Colors
    var loadingCircleColor: Int = DEFAULT_LOADING_CIRCLE_COLOR
        set(@ColorInt color) {
            field = color
            loadingArcPaint.color = color
            invalidate()
        }

    var waitingRectColor: Int = DEFAULT_WAITING_RECT_COLOR
        set(@ColorInt color) {
            field = color
            waitingPaint.color = color
            invalidate()
        }

    var loadingRectColor: Int = DEFAULT_LOADING_RECT_COLOR
        set(@ColorInt color) {
            field = color
            loadingPaint.color = color
            invalidate()
        }

    var textColor: Int = DEFAULT_TEXT_COLOR
        set(@ColorInt color) {
            field = color
            textValuePaint.color = color
            invalidate()
        }


    private var valueAnimator = ValueAnimator.ofInt(0, 100).apply {
        addUpdateListener {
            progress = animatedValue as Int
            invalidate()
        }
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
    }


    init {
        parseAttr(attrs)

        // Made button clickable
        isClickable = true

        /*
        * Initialize all properties
        */
        loadingArcPaint.apply {
            style = Paint.Style.FILL
            color = loadingCircleColor
        }

        waitingPaint.apply {
            style = Paint.Style.FILL
            color = waitingRectColor
        }

        loadingPaint.apply {
            style = Paint.Style.FILL
            color = loadingRectColor
        }

        textValuePaint.apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = 50f
            typeface = Typeface.create("", Typeface.NORMAL)
            color = textColor
        }

    }

    private fun parseAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.LoadingButton, 0, 0
        )

        loadingCircleColor = typedArray.getColor(
            R.styleable.LoadingButton_loading_circle_color,
            ContextCompat.getColor(context,DEFAULT_LOADING_CIRCLE_COLOR)
        )

        waitingRectColor = typedArray.getColor(
            R.styleable.LoadingButton_waiting_rect_color,
            ContextCompat.getColor(context, DEFAULT_WAITING_RECT_COLOR)
        )

        loadingRectColor = typedArray.getColor(
            R.styleable.LoadingButton_loading_rect_color,
            ContextCompat.getColor(context, DEFAULT_LOADING_RECT_COLOR)
        )

        textColor = typedArray.getColor(
            R.styleable.LoadingButton_text_color,
            ContextCompat.getColor(context,DEFAULT_TEXT_COLOR)
        )


        typedArray.recycle()
    }

    override fun performClick(): Boolean {
        super.performClick()

        if (buttonState == ButtonState.Completed && isChecked){
            buttonState = ButtonState.Loading
            animation()
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawWaitingRect(canvas)

        val state =
            if (buttonState == ButtonState.Loading)
                context.getString(R.string.button_loading)
            else
                context.getString(R.string.download_image)

        if (buttonState == ButtonState.Loading) {

            drawLoadingRect(canvas)

            drawLoadingArc(canvas)
        }

        drawStateText(canvas,state)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawWaitingRect(canvas: Canvas?) {
        waitingRect.set(
            0,
            0,
            widthSize,
            heightSize)
        canvas?.drawRect(waitingRect,waitingPaint)
    }

    private fun drawLoadingRect(canvas: Canvas?) {
        loadingRect.set(
            0,
            0,
            (widthSize * (progress.toFloat()/100f)).toInt(),
            heightSize)
        canvas?.drawRect(loadingRect,loadingPaint)
    }

    private fun drawLoadingArc(canvas: Canvas?) {
        canvas?.drawArc(
            widthSize - 145f,
            heightSize / 2 - 35f,
            widthSize - 75f,
            heightSize / 2 + 35f,
            0F,
            360f * (progress.toFloat()/100f),
            true,
            loadingArcPaint
        )
    }

    private fun drawStateText(canvas: Canvas?,state: String) {

        canvas?.drawText(
            state,
            widthSize / 2f,
            heightSize / 2f + 18,
            textValuePaint)
    }

    private fun animation() {
        Log.i("LoadingButton",progress.toString())
        valueAnimator.duration = 3000
        valueAnimator.start()
    }

    fun hasCompletedDownload() {
        // cancel the animation when file is downloaded
        valueAnimator.duration = 300
        postDelayed({
            valueAnimator.cancel()
            buttonState = ButtonState.Completed
            invalidate()
            requestLayout()},
            300)
    }

    fun checked(Checked: Boolean){
        isChecked = Checked
    }

}