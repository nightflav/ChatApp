package com.example.homework_2.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.example.homework_2.R
import com.example.homework_2.sp

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context, attrs, defStyleAttr
) {

    private val defaultPaddingHorizontal = 36
    private val defaultPaddingVertical = 24

    var emoji: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var count: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    private val reactToShow
        get() = "$emoji $count"

    private val reactBounds = Rect()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 18f.sp(context)
        color = Color.WHITE
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.EmojiView) {
            emoji = this.getString(R.styleable.EmojiView_emoji) ?: ""
            count = this.getInt(R.styleable.EmojiView_count, 1)
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

    fun setEmojiBackground(@DrawableRes path: Int = R.drawable.bg_emoji_reaction_view) {
        background = ResourcesCompat.getDrawable(resources, path, context.theme)
    }
}