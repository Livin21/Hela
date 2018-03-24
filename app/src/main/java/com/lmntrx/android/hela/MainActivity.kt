package com.lmntrx.android.hela

import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIDataService
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity(), AIListener {

    private lateinit var conversationList: MutableList<ChatBubble>

    var mode = 0

    val typingMode = 1
    private val idleMode = 0

    private lateinit var mGoogleSignInClient: GoogleSignInClient


    private lateinit var mAuth: FirebaseAuth

    private var aiService: AIService? = null
    private var aiDataService: AIDataService? = null
    private var aiRequest: AIRequest? = null

    override fun onResult(result: AIResponse?) {
        conversationList.add(UserBubble(result?.result?.resolvedQuery!!))
        val botResponse = result.result.fulfillment.speech
        if (!botResponse.isNullOrEmpty())
            conversationList.add(BotBubble(botResponse))
        else
            conversationList.add(BotBubble("Sorry. I am not programmed for that. :("))
        conversationListRecyclerView.swapAdapter(ConversationListAdapter(conversationList), true)

        mode = idleMode

        FirebaseHandler().saveChat(conversationList)
    }

    override fun onListeningStarted() {

    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onError(error: AIError?) {

    }

    override fun onListeningCanceled() {

    }

    override fun onListeningFinished() {

    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn()

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
                    imageViewAnimatedChange(this@MainActivity, actionButton, R.drawable.ic_action_send)
                }else{
                    imageViewAnimatedChange(this@MainActivity, actionButton, R.drawable.ic_action_mic)
                }
            }
        })


        // API.ai setup
        val config = AIConfiguration(
                "bbdbf6a9df0a463a9f604821bb5f5ff1",
                ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System
        )

        aiService = AIService.getService(this, config)
        aiService?.setListener(this)

        aiDataService = AIDataService(this, config)
        aiRequest = AIRequest()


    }

    private fun setupRecyclerView() {
        conversationList = ArrayList()
        conversationListRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationListRecyclerView.adapter = ConversationListAdapter(conversationList)

        FirebaseHandler().getConversationList(object : OnLoadCompleteListener {
            override fun onComplete(conversationList: List<ChatBubble>) {
                conversationListRecyclerView.swapAdapter(ConversationListAdapter(conversationList), true)
            }
        })
    }

    private fun startMic() {
        aiService?.startListening()
    }

    private fun sendMessage(message: String) {
        conversationList.add(UserBubble(message.trim()))
        conversationListRecyclerView.swapAdapter(ConversationListAdapter(conversationList), true)
        chatBox.setText("")
        mode = idleMode

        aiRequest?.setQuery(message)
        doAsync {
            val response = aiDataService?.request(aiRequest)
            uiThread {
                val botResponse = response!!.result.fulfillment.speech
                if (!botResponse.isNullOrEmpty())
                    conversationList.add(BotBubble(botResponse))
                else
                    conversationList.add(BotBubble("Sorry. I am not programmed for that. :("))
                conversationListRecyclerView.swapAdapter(ConversationListAdapter(conversationList), true)
            }
        }

        FirebaseHandler().saveChat(conversationList)



    }

    @SuppressLint("RestrictedApi")
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 0)
    }

    @SuppressLint("RestrictedApi")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

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

            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(MainActivity::class.java.simpleName, "signInResult:failed code=" + e.statusCode)

            Toast.makeText(this, "Login Failed",Toast.LENGTH_LONG).show()
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(MainActivity::class.java.simpleName, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(MainActivity::class.java.simpleName, "signInWithCredential:success")
                        Toast.makeText(this, "Login Success",Toast.LENGTH_LONG).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(MainActivity::class.java.simpleName, "signInWithCredential:failure", task.exception)

                        Toast.makeText(this, "Login Failed",Toast.LENGTH_LONG).show()
                    }
                }
    }

    // Action Button animation
    private fun imageViewAnimatedChange(context: Context, imageView: ImageView, @DrawableRes newImageId: Int){
        val newImage = ContextCompat.getDrawable(context, newImageId)
        val animOut = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
        val animIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                imageView.setImageDrawable(newImage)
                imageView.startAnimation(animIn)
            }
            override fun onAnimationStart(p0: Animation?) {}
        })
        imageView.startAnimation(animOut)
    }

}
