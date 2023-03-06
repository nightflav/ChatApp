package com.example.homework_2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context, attrs, defStyleAttr
) {

    private val defaultPaddingHorizontal = 36
    private val defaultPaddingVertical = 24

    private var reactToShow = ""

    private val reactBounds = Rect()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 24f.sp(context)
        color = Color.DKGRAY
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.EmojiView) {
            val emoji = this.getString(R.styleable.EmojiView_emoji) ?: ""
            val count = this.getInt(R.styleable.EmojiView_count, 1)
            reactToShow = "$emoji $count"
            Log.d("MyTag", reactToShow)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(reactToShow, 0, reactToShow.length, reactBounds)
        val reactWidth = reactBounds.width()
        val reactHeight = reactBounds.height()

        val measuredWidth =
            resolveSize(
                reactWidth + paddingLeft + paddingRight + defaultPaddingHorizontal,
                widthMeasureSpec
            )
        val measuredHeight =
            resolveSize(
                reactHeight + paddingTop + paddingBottom + defaultPaddingVertical,
                heightMeasureSpec
            )
        setMeasuredDimension(
            measuredWidth, measuredHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        val originX = (-reactBounds.left + paddingLeft + defaultPaddingHorizontal / 2).toFloat()
        val baselineY = (-reactBounds.top + paddingTop + defaultPaddingVertical / 2).toFloat()
        canvas.drawText(reactToShow, originX, baselineY, textPaint)
    }

    private fun Float.sp(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    )

    fun setEmoji(newEmoji: String) {
        reactToShow = "$newEmoji ${reactToShow.split(" ").last()}"
    }

    fun setCount(newCount: Int = reactToShow.split(" ").last().toInt()) {
        reactToShow = "${reactToShow.split(" ").first()} $newCount"
    }

    fun increaseCount() {
        reactToShow =
            "${reactToShow.split(" ").first()} ${reactToShow.split(" ").last().toInt() + 1}"
    }

    fun decreaseCount() {
        reactToShow =
            "${reactToShow.split(" ").first()} ${reactToShow.split(" ").last().toInt() - 1}"
    }

    fun setEmojiBackground(@DrawableRes path: Int = R.drawable.bg_emoji_reaction_view) {
        background = ResourcesCompat.getDrawable(resources, path, context.theme)
    }
}