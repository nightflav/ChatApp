package com.example.homework_2

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.children

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
    private var spaceBetween = 24
    private var maxWidth = context.resources.displayMetrics.widthPixels
    private lateinit var childViews: MutableList<View>

    init {
        context.withStyledAttributes(attrs, R.styleable.ReactionsViewGroup) {
            spaceBetween = this.getInt(R.styleable.ReactionsViewGroup_spaceBetween, spaceBetween)
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
                    if (currWidth + child.measuredWidth > maxWidth) 0 else spaceBetween

            if (currWidth > maxWidth) {
                currWidth = child.measuredWidth
                currHeight += currMaxHeight + spaceBetween / 2
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
            if (offsetX + child.measuredWidth < maxWidth) {
                layoutChild(
                    child,
                    offsetX,
                    offsetY + (maxHeightByRow[rowNumber] - child.measuredHeight) / 2
                )
                offsetX += child.measuredWidth + if (offsetX + child.measuredWidth >= maxWidth) 0 else spaceBetween
                maxHeight =
                    if (child.measuredHeight > maxHeight) child.measuredHeight else maxHeight
            } else {
                rowNumber++
                offsetX = paddingStart
                offsetY += maxHeight + spaceBetween / 2
                maxHeight = 0
                layoutChild(child, offsetX, offsetY)
                offsetX += child.measuredWidth + if (offsetX + child.measuredWidth >= maxWidth) 0 else spaceBetween
                maxHeight =
                    if (child.measuredHeight > maxHeight) child.measuredHeight else maxHeight
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

    private fun Float.px(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this,
        context.resources.displayMetrics
    )

    fun setMaxSpace(size: Int) {
        maxWidth = size
    }
}