package com.lmntrx.android.hela

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.lmntrx.android.hela.R.id.actionButton
import com.lmntrx.android.hela.R.id.conversationListRecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var conversationList: ArrayList<ChatBubble>


    var mode = 0

    val typingMode = 1
    val idleMode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        conversationList = ArrayList()
        conversationListRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationListRecyclerView.adapter = ConversationListAdapter(conversationList)


        // Click listener on send/mic button
        actionButton.setOnClickListener {
            if (mode == typingMode){
                sendMessage(chatBox.text.toString())
                chatBox.setText("")
            }else{
                startMic()
            }
        }

        chatBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mode = typingMode
                if (!p0.isNullOrEmpty()){
                    actionButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_action_send))
                }else{
                    actionButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_action_mic))
                }
            }
        })




    }

    private fun startMic() {


    }

    private fun sendMessage(message: String) {
        conversationList.add(UserBubble(message))
        conversationListRecyclerView.swapAdapter(ConversationListAdapter(conversationList), true)
        mode = idleMode
    }
}
