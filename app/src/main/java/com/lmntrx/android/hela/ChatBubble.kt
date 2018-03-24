package com.lmntrx.android.hela


/***
 * Created by Livin Mathew <mail@livinmathew.me> on 23/3/18.
 */

sealed class ChatBubble(open val message: String)
data class BotBubble(override val message: String): ChatBubble(message)
data class UserBubble(override val message: String): ChatBubble(message)