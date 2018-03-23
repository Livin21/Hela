package com.lmntrx.android.hela

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
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


        // Click listener on send/mic button
        chatBox.setOnTouchListener({ view: View, motionEvent: MotionEvent ->

            val textView = view as EditText

            val rightDrawable = 2

            if(motionEvent.action == MotionEvent.ACTION_UP) {
                if(motionEvent.rawX >= (textView.right - textView.compoundDrawables[rightDrawable].bounds.width())) {
                    if (mode == typingMode){
                        sendMessage(textView.text.toString())
                        textView.setText("")
                    }else{
                        startMic()
                    }
                    true
                }else false
            }else false

        })

        chatBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()){
                    chatBox.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_action_send,
                            0
                    )
                }else{
                    chatBox.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_action_mic,
                            0
                    )
                }
            }
        })


    }

    private fun startMic() {


    }

    private fun sendMessage(message: String) {

    }
}
