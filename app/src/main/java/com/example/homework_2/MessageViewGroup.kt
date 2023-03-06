package com.example.homework_2

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

class MessageViewGroup
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {
    private val image: ImageView
    private val message: TextView
    private val name: TextView
    private val reactions: ReactionsViewGroup
    private val paddingHorizontal = paddingLeft + paddingRight
    private val paddingVertical = paddingTop + paddingBottom

    init {
        inflate(context, R.layout.message_view_group_content, this)

        image = findViewById(R.id.image)
        message = findViewById(R.id.message)
        name = findViewById(R.id.name)
        reactions = findViewById(R.id.reactions)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildWithMargins(image, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChildWithMargins(
            message,
            widthMeasureSpec,
            image.measuredWidth + image.marginRight + image.marginLeft,
            heightMeasureSpec,
            image.measuredHeight + image.marginTop + image.marginBottom
        )
        measureChildWithMargins(
            name,
            widthMeasureSpec,
            image.measuredWidth + image.marginRight + image.marginLeft,
            heightMeasureSpec,
            image.measuredHeight + image.marginTop + image.marginBottom +
                    message.measuredHeight + message.marginTop + message.marginBottom
        )

        reactions.setMaxSpace(message.measuredWidth)
        measureChildWithMargins(
            reactions,
            widthMeasureSpec,
            image.measuredWidth + image.marginRight + image.marginLeft,
            heightMeasureSpec,
            image.measuredHeight + image.marginTop + image.marginBottom +
                    message.measuredHeight + message.marginTop + message.marginBottom +
                    name.measuredHeight + name.marginTop + name.marginBottom
        )

        val totalWidth =
            paddingHorizontal + image.measuredWidth + message.measuredWidth +
                    image.marginLeft + image.marginRight +
                    message.marginLeft + message.marginRight +
                    name.marginLeft + name.marginRight +
                    reactions.marginLeft + reactions.marginRight

        val totalHeight =
            paddingVertical + maxOf(
                image.measuredHeight,
                message.measuredHeight + name.measuredHeight + reactions.measuredHeight
            )
        setMeasuredDimension(totalWidth, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var offsetX = paddingLeft + image.marginLeft
        var offsetY = paddingTop
        image.layout(
            offsetX,
            offsetY + image.marginTop,
            offsetX + image.measuredWidth,
            offsetY + image.measuredHeight + image.marginTop
        )
        offsetX += image.measuredWidth + image.marginRight + message.marginLeft

        name.layout(
            offsetX,
            offsetY + name.marginTop,
            offsetX + name.measuredWidth,
            offsetY + name.marginTop + name.measuredHeight
        )
        offsetY += name.measuredHeight + name.marginTop + name.marginBottom

        message.layout(
            offsetX,
            offsetY + message.marginTop,
            offsetX + message.measuredWidth,
            offsetY + message.marginTop + message.measuredHeight
        )
        offsetY += message.measuredHeight + message.marginTop + message.marginBottom

        reactions.layout(
            offsetX,
            offsetY + reactions.marginTop,
            offsetX + reactions.measuredWidth,
            offsetY + reactions.measuredHeight + reactions.marginTop
        )
    }



    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }
}