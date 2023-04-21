package com.example.tinkoff_chat_app.utils

import android.content.Context
import android.util.TypedValue
import androidx.fragment.app.Fragment
import com.example.tinkoff_chat_app.App
import com.example.tinkoff_chat_app.di.ApplicationComponent
import java.time.ZoneId
import java.util.*

fun Fragment.getAppComponent(): ApplicationComponent = (requireContext().applicationContext as App).appComponent

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

fun Int.toDate(): String =
    Date(this * 1000L).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()

fun String.parseDate(): String {
    val day = this.drop(8)
    val month = when (this.drop(5).dropLast(3)) {
        "01" -> "Jan"
        "02" -> "Feb"
        "03" -> "Mar"
        "04" -> "Apr"
        "05" -> "May"
        "06" -> "Jun"
        "07" -> "Jul"
        "08" -> "Aug"
        "09" -> "Sep"
        "10" -> "Oct"
        "11" -> "Nov"
        "12" -> "Dec"
        else -> "what is this month?"
    }
    return "$day $month"
}