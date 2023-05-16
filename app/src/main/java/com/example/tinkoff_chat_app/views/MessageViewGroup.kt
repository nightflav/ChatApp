package com.example.tinkoff_chat_app.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.*
import com.example.tinkoff_chat_app.R
import com.example.tinkoff_chat_app.utils.dp

class MessageViewGroup
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(
    context, attributeSet, defStyleAttr, defStyleRes
) {
    val image: ImageView by lazy { findViewById(R.id.profile_image) }
    val message: TextView by lazy { findViewById(R.id.message_text) }
    val name: TextView by lazy { findViewById(R.id.sender_name) }
    val imageToLoad: ImageView by lazy { findViewById(R.id.iv_image_message_received) }
    val attachedImage: LinearLayout by lazy { findViewById(R.id.ll_message_image) }
    val attachedFileImage: LinearLayout by lazy { findViewById(R.id.ll_message_docs) }
    val reactions: ReactionsViewGroup by lazy { findViewById(R.id.reactions) }
    val linearLayout: LinearLayout by lazy { findViewById(R.id.msg_vg_text) }
    private val paddingHorizontal = paddingLeft + paddingRight
    private val paddingVertical = paddingTop + paddingBottom

    init {
        inflate(context, R.layout.message_view_group_content, this)

        name.setTextColor(resources.getColor(R.color.secondary_color, this.context.theme))
        linearLayout.background =
            AppCompatResources.getDrawable(this.context, R.drawable.bg_received_message)

        linearLayout.setPadding(
            12f.dp(context).toInt(),
            8f.dp(context).toInt(),
            12f.dp(context).toInt(),
            8f.dp(context).toInt()
        )

        reactions.setPadding(
            0f.dp(context).toInt(),
            4f.dp(context).toInt(),
            0f.dp(context).toInt(),
            4f.dp(context).toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (reactions.childCount == 1) reactions.isVisible = false

        reactions.setMaxSpace(277f.dp(context).toInt())
        message.includeFontPadding = false

        measureChildWithMargins(
            image, widthMeasureSpec, image.marginTop, heightMeasureSpec, image.marginStart
        )

        measureChildWithMargins(
            linearLayout,
            widthMeasureSpec,
            linearLayout.marginTop,
            heightMeasureSpec,
            image.maxWidth + linearLayout.marginStart + image.marginStart
        )

        measureChildWithMargins(
            reactions,
            widthMeasureSpec,
            image.measuredWidth + image.marginRight + image.marginLeft,
            heightMeasureSpec,
            linearLayout.measuredHeight + name.measuredHeight + name.marginTop + name.marginBottom
        )

        measureChildWithMargins(
            attachedImage,
            widthMeasureSpec,
            attachedImage.marginTop,
            heightMeasureSpec,
            image.maxWidth + attachedImage.marginStart + image.marginStart
        )

        measureChildWithMargins(
            attachedFileImage,
            widthMeasureSpec,
            attachedFileImage.marginTop,
            heightMeasureSpec,
            image.maxWidth + attachedFileImage.marginStart + image.marginStart
        )

        val totalWidth = paddingHorizontal + image.measuredWidth + maxOf(
            reactions.measuredWidth,
            attachedImage.measuredWidth,
            linearLayout.measuredWidth,
            linearLayout.measuredWidth,
        ) + 16f.dp(context).toInt()

        val totalHeight = paddingVertical + maxOf(
            image.measuredHeight,
            linearLayout.measuredHeight + reactions.measuredHeight + if (attachedImage.isVisible) attachedImage.measuredHeight else 0 + if (attachedFileImage.isVisible) attachedFileImage.measuredHeight else 0
        )

        setMeasuredDimension(
            totalWidth, totalHeight
        )
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
        offsetX += image.measuredWidth + image.marginRight + message.marginLeft + 8f.dp(context)
            .toInt()

        linearLayout.layout(
            offsetX,
            offsetY + linearLayout.marginTop,
            offsetX + linearLayout.measuredWidth,
            offsetY + linearLayout.marginTop + linearLayout.measuredHeight
        )
        offsetY += linearLayout.measuredHeight + linearLayout.marginTop + linearLayout.marginBottom

        if (attachedImage.isVisible) {
            attachedImage.layout(
                offsetX,
                offsetY + attachedImage.marginTop,
                offsetX + attachedImage.measuredWidth,
                offsetY + attachedImage.marginTop + attachedImage.measuredHeight
            )
            offsetY += attachedImage.measuredHeight + attachedImage.marginTop + attachedImage.marginBottom
        }

        if (attachedFileImage.isVisible) {
            attachedFileImage.layout(
                offsetX,
                offsetY + attachedFileImage.marginTop,
                offsetX + attachedFileImage.measuredWidth,
                offsetY + attachedFileImage.marginTop + attachedFileImage.measuredHeight
            )
            offsetY += attachedFileImage.measuredHeight + attachedFileImage.marginTop + attachedFileImage.marginBottom
        }

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