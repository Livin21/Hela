package com.lmntrx.android.hela

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


/***
 * Created by Livin Mathew <mail@livinmathew.me> on 24/3/18.
 */

interface OnLoadCompleteListener{
    fun onComplete(conversationList: ArrayList<ChatBubble>)
}

class FirebaseHandler{

    fun saveChat(conversationList: ArrayList<ChatBubble>){
        conversationList.forEach {
            FirebaseFirestore.getInstance().collection("conversations").document(
                    FirebaseAuth.getInstance().currentUser!!.uid
            ).collection("conversation").add(it)
        }
    }

    fun getConversationList(onLoadCompleteListener: OnLoadCompleteListener){
        val conversationList: ArrayList<ChatBubble> = ArrayList()
        FirebaseFirestore.getInstance().collection("conversations").document(
                FirebaseAuth.getInstance().currentUser!!.uid
        ).collection("conversation").get().addOnCompleteListener {
            if (it.isSuccessful){
                it.result.documents.forEach {
                    conversationList.add(
                            if (it["type"].toString() == "0") UserBubble(it["message"] as String) else BotBubble(it["message"] as String)
                    )
                }
                conversationList.reverse()
                onLoadCompleteListener.onComplete(conversationList)
            }
        }
    }

}