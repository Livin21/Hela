package com.lmntrx.android.hela

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


/***
 * Created by Livin Mathew <mail@livinmathew.me> on 24/3/18.
 */

interface OnLoadCompleteListener {
    fun onComplete(conversationList: List<ChatBubble>)
}

class FirebaseHandler {

    fun saveChat(conversationList: MutableList<ChatBubble>) {
        FirebaseFirestore.getInstance().collection("conversations").document(
                FirebaseAuth.getInstance().currentUser!!.uid
        ).collection("conversation").add(conversationList.last())
    }

    fun getConversationList(onLoadCompleteListener: OnLoadCompleteListener) {
        val conversationList: MutableList<ChatBubble> = ArrayList()
        FirebaseFirestore.getInstance().collection("conversations").document(
                FirebaseAuth.getInstance().currentUser!!.uid
        ).collection("conversation").get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.documents.forEach {
                    conversationList.add(
                            if (it["type"].toString() == "0")
                                UserBubble(it["message"] as String, (it["order"] as Long).toInt())
                            else
                                BotBubble(it["message"] as String, (it["order"] as Long).toInt())
                    )
                }
                if (!conversationList.isEmpty())
                    onLoadCompleteListener.onComplete(conversationList.sortedWith(compareBy { it.order }))
                else
                    onLoadCompleteListener.onComplete(conversationList)
            }
        }
    }

}