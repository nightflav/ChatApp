package com.example.homework_2

import android.content.Context
import android.util.TypedValue
import com.example.homework_2.models.UserProfile
import com.example.homework_2.network.RetrofitInstance
import com.example.homework_2.network.networkModels.users.Member
import kotlinx.coroutines.CancellationException
import retrofit2.Response
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

fun Int.toDate(): String = Date(this*1000L).toInstant().toString()

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