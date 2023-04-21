package com.example.tinkoff_chat_app.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import com.example.tinkoff_chat_app.R
import com.example.tinkoff_chat_app.utils.dp
import com.example.tinkoff_chat_app.utils.px

class ReactionsViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(
    context, attrs, defStyleAttr
) {
    private var maxHeightByRow = mutableListOf(-1)
    private var totalWidth = 0
    private var totalHeight = 0
    private var spaceBetweenHorizontal = 4f.dp(context).toInt()
    private var spaceBetweenVertical = 4f.dp(context).toInt()
    private var maxWidth = context.resources.displayMetrics.widthPixels
    private lateinit var childViews: MutableList<View>
    init {
        context.withStyledAttributes(attrs, R.styleable.ReactionsViewGroup) {
            spaceBetweenHorizontal = this.getInt(R.styleable.ReactionsViewGroup_spaceBetween, spaceBetweenHorizontal)
            maxWidth = this.getDimension(
                R.styleable.ReactionsViewGroup_maxSpace,
                maxWidth.toFloat()
            ).px(context).toInt()
        }

        inflate(context, R.layout.reactions_view_group_content, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val tmpChildViews = children.toMutableList()
        val addBtn = tmpChildViews.removeFirstOrNull()
        if (addBtn != null) {
            tmpChildViews.add(addBtn)
        }
        childViews = tmpChildViews

        var currMaxWidth = 0
        var currWidth = 0
        var currMaxHeight = 0
        var currHeight = 0
        for (child in childViews) {
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            currWidth += child.measuredWidth +
                    if (currWidth + child.measuredWidth > maxWidth) 0 else spaceBetweenHorizontal

            if (currWidth > maxWidth) {
                currWidth = child.measuredWidth
                currHeight += currMaxHeight + spaceBetweenVertical / 2
                maxHeightByRow.add(currMaxHeight)
                currMaxHeight = child.measuredHeight
            }

            if (child.measuredHeight > currMaxHeight) {
                currMaxHeight = child.measuredHeight
                if (maxHeightByRow.last() < currMaxHeight)
                    maxHeightByRow[maxHeightByRow.size - 1] = currMaxHeight
            }

            if (currWidth in currMaxWidth until maxWidth) {
                currMaxWidth = currWidth
            }

            if (maxHeightByRow.firstOrNull() == -1)
                maxHeightByRow = mutableListOf(currMaxHeight)
        }

        if (maxHeightByRow.firstOrNull() == -1)
            maxHeightByRow = mutableListOf(currMaxHeight)
        totalWidth = currMaxWidth + paddingStart + paddingEnd
        totalHeight = currHeight + paddingBottom + paddingTop + currMaxHeight
        setMeasuredDimension(totalWidth, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        if (childViews.size > 1)
            this.visibility = VISIBLE
        else
            this.visibility = GONE

        var rowNumber = 0
        var offsetY = paddingTop
        var offsetX = paddingStart
        var maxHeight = 0
        for (child in childViews) {
            if (offsetX + child.measuredWidth < maxWidth - paddingEnd - spaceBetweenHorizontal) {
                layoutChild(
                    child,
                    offsetX,
                    offsetY + (maxHeightByRow[rowNumber] - child.measuredHeight) / 2
                )
                offsetX += child.measuredWidth + spaceBetweenHorizontal
                maxHeight =
                    if (child.measuredHeight > maxHeight) child.measuredHeight else maxHeight
            } else {
                rowNumber++
                offsetX = paddingStart
                offsetY += maxHeight + spaceBetweenVertical / 2
                layoutChild(child, offsetX, offsetY)
                offsetX += child.measuredWidth + spaceBetweenHorizontal
                maxHeight = child.measuredHeight
            }
        }
    }

    private fun layoutChild(child: View, x: Int, y: Int) {
        child.layout(
            x,
            y,
            x + child.measuredWidth,
            y + child.measuredHeight
        )
    }

    fun setMaxSpace(size: Int) {
        maxWidth = size
    }
}