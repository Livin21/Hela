package com.lmntrx.android.hela

import java.io.Serializable


/***
 * Created by Livin Mathew <mail@livinmathew.me> on 23/3/18.
 */

sealed class ChatBubble(open val message: String, open val type: Int, open val order: Int): Serializable
data class BotBubble(override val message: String, override val order: Int): ChatBubble(message, 1, order)
data class UserBubble(override val message: String, override val order: Int): ChatBubble(message, 0, order)