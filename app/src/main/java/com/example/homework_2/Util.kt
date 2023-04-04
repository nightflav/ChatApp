package com.example.homework_2

import android.content.Context
import android.util.TypedValue
import com.example.homework_2.models.UserProfile
import com.example.homework_2.network.RetrofitInstance
import com.example.homework_2.network.networkModels.users.Member
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.time.ZoneId
import java.util.*

suspend fun <R> runCatchingNonCancellation(block: suspend () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
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
    val month = when(this.drop(5).dropLast(3)) {
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

suspend fun Response<Member>.toUserProfile(): UserProfile? {
    val member = body()
    return if (this.isSuccessful && member != null) {
        val currUserPresence = RetrofitInstance.chatApi.getUserPresence(member.email).body()
        UserProfile(
            fullName = member.full_name,
            status = currUserPresence!!.presence.aggregated.status,
            avatarSource = member.avatar_url
                ?: "https://www.freeiconspng.com/thumbs/no-image-icon/no-image-icon-6.png",
            email = member.email
        )
    } else null
}