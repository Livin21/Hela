package com.lmntrx.android.hela

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*


/***
 * Created by Livin Mathew <mail@livinmathew.me> on 23/3/18.
 */

class ConversationListAdapter(private val conversationList: ArrayList<ChatBubble>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val user = 0
    private val bot = 1
    private val invalid = -1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {


        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent?.context)

        viewHolder = when(viewType){
            user -> {
                val userChatBubbleView = inflater.inflate(R.layout.chat_dialog_user, parent, false)
                UserBubbleViewHolder(userChatBubbleView)
            }
            else -> {
                val userChatBubbleView = inflater.inflate(R.layout.chat_dialog_bot, parent, false)
                BotBubbleViewHolder(userChatBubbleView)
            }
        }

        return viewHolder

    }

    override fun getItemCount(): Int = conversationList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when(holder?.itemViewType){
            user -> {
                (holder as UserBubbleViewHolder).chatTextView.text = conversationList[position].message
            }
            else -> {
                (holder as BotBubbleViewHolder).chatTextView.text = conversationList[position].message
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (conversationList[position] is UserBubble) user else if(conversationList[position] is BotBubble) bot else invalid
    }


    class UserBubbleViewHolder(userChatBubbleView: View) : RecyclerView.ViewHolder(userChatBubbleView) {
        val chatTextView: TextView = userChatBubbleView.findViewById(R.id.chatDialogUser)
    }


    class BotBubbleViewHolder(botChatBubbleView: View) : RecyclerView.ViewHolder(botChatBubbleView) {
        val chatTextView: TextView = botChatBubbleView.findViewById(R.id.chatDialogBot)
    }

}