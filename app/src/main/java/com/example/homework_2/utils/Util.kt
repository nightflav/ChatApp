package com.example.homework_2.utils

import android.content.Context
import android.util.TypedValue
import androidx.fragment.app.Fragment
import com.example.homework_2.App
import com.example.homework_2.di.ApplicationComponent
import com.example.homework_2.models.SingleMessage
import java.time.ZoneId
import java.util.*

fun Fragment.getAppComponent(): ApplicationComponent = (requireContext().applicationContext as App).appComponent

fun List<SingleMessage>?.addDateSeparators(): List<SingleMessage>? {
    if (this == null || this.isEmpty()) return null

    val resultMessages = mutableListOf<SingleMessage>()

    var prevDate = this.first().date
    resultMessages.add(
        SingleMessage(
            date = prevDate.parseDate(),
            isDataSeparator = true
        )
    )

    for (msg in this) {
        val thisMsgDate = msg.date
        if (thisMsgDate > prevDate) {
            resultMessages.add(
                SingleMessage(
                    date = thisMsgDate.parseDate(),
                    isDataSeparator = true
                )
            )
            prevDate = thisMsgDate
        }
        resultMessages.add(msg)
    }

    return resultMessages
}

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