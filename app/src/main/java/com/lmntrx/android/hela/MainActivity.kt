package com.lmntrx.android.hela

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var conversationList: ArrayList<ChatBubble>

    var mode = 0

    val typingMode = 1
    private val idleMode = 0

    private var account: GoogleSignInAccount? = null
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login()

        // Setup RecyclerView
        setupRecyclerView()

        // Click listener on send/mic button
        actionButton.setOnClickListener {
            if (mode == typingMode){
                sendMessage(chatBox.text.toString())
            }else{
                startMic()
            }
        }

        // Typing listener on chat box
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

    private fun setupRecyclerView() {
        conversationList = ArrayList()
        conversationListRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationListRecyclerView.adapter = ConversationListAdapter(conversationList)
    }

    private fun startMic() {


    }

    private fun sendMessage(message: String) {
        conversationList.add(UserBubble(message))
        conversationListRecyclerView.swapAdapter(ConversationListAdapter(conversationList), true)
        chatBox.setText("")
        mode = idleMode
    }

    @SuppressLint("RestrictedApi")
    fun login(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 0)
        } else updateUser(account)
    }


    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 0) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUser(account)
        } catch (e: ApiException) {
            Log.e("Login Error","Login Failed\n${e.message}")
        }


    }

    private fun updateUser(account: GoogleSignInAccount?) {

        this.account = account

    }
}
