package com.example.tinkoff_chat_app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.TypedValue
import androidx.fragment.app.Fragment
import com.example.tinkoff_chat_app.App
import com.example.tinkoff_chat_app.di.ApplicationComponent
import java.time.ZoneId
import java.util.*

@Suppress("deprecation")
fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

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