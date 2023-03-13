package com.example.homework_2

import android.content.Context
import android.util.TypedValue


fun Float.sp(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    context.resources.displayMetrics
)

fun Float.dp(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)

fun Float.px(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    this,
    context.resources.displayMetrics
)