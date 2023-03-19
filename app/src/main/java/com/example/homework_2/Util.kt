package com.example.homework_2

import android.content.Context
import android.util.TypedValue
import java.time.LocalDate


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

fun LocalDate.parseDate(): String {
    val currDayOfMonth = this.dayOfMonth
    val currMonth = when(this.month.value) {
        0 -> "Jan"
        1 -> "Feb"
        2 -> "Mar"
        3 -> "Apr"
        4 -> "May"
        5 -> "Jun"
        6 -> "Jul"
        7 -> "Aug"
        8 -> "Spt"
        9 -> "Oct"
        10 -> "Nov"
        else -> "Dec"
    }
    return "$currDayOfMonth $currMonth"
}