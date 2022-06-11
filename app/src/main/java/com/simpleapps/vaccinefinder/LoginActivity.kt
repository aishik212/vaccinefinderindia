package com.simpleapps.vaccinefinder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.simpleapps.vaccinefinder.databinding.LoginLayoutBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class LoginActivity : AppCompatActivity() {
    lateinit var inflate: LoginLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = LoginLayoutBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        inflate.clayout.visibility = GONE
        inflate.llayout.visibility = VISIBLE
        inflate.slayout.visibility = GONE

        inflate.glogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("463178197083-9lnhl15oq9mvnj5ppui2kq6hpa4qtojg.apps.googleusercontent.com")
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        inflate.sbtn.setOnClickListener {
            inflate.clayout.visibility = GONE
            inflate.slayout.visibility = VISIBLE
            inflate.llayout.visibility = GONE
            if (BuildConfig.DEBUG) {
//                inflate.glogin.performClick()
            }
            if (BuildConfig.DEBUG) {
                inflate.nameTv.editText?.setText("Aishik")
                inflate.emailTv.editText?.setText(currentUser?.email)
            }
        }
        inflate.signup.setOnClickListener {
            var name = inflate.nameTv.editText?.text
            var email = currentUser?.email
            var gndr = "Male"
            if (inflate.gndrBtn.checkedButtonId == R.id.femalebtn) {
                gndr = "Female"
            }
            var type = "Patient"
            if (inflate.typeBtn.checkedButtonId == R.id.dbtn) {
                type = "Doctor"
            }
            signUp(name, email, gndr, type)
            Log.d("texts", "onCreate: " + name + " " + email + " " + gndr + " " + type)
        }
        inflate.lbtn.setOnClickListener {
            inflate.clayout.visibility = GONE
            inflate.slayout.visibility = GONE
            inflate.llayout.visibility = VISIBLE
            if (BuildConfig.DEBUG) {
//                inflate.glogin.performClick()
            }
            if (FirebaseAuth.getInstance().currentUser != null) {
//                inflate.glogin.performClick()
            }

        }
        if (BuildConfig.DEBUG) {
//            inflate.lbtn.performClick()
        }
        if (FirebaseAuth.getInstance().currentUser != null) {
            inflate.glogin.performClick()
        }
    }

    private fun signUp(name: Editable?, email: String?, gndr: String, type: String) {
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType: MediaType? = "application/json".toMediaTypeOrNull()
        val s =
            "{\r\n    \"name\":\"$name\",\r\n    \"type\":\"$type\",\r\n    \"gender\":\"$gndr\",\r\n    \"email\":\"$email\"\r\n}"
        val body: RequestBody = s.toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url("https://ap-south-1.aws.data.mongodb-api.com/app/application-0-btquy/endpoint/add/user?secret=tanmoy")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build()
        Thread {
            val response = client.newCall(request).execute()
            Log.d("texts", "signUp: " + response.isSuccessful)
            Log.d("texts", "signUp: " + response.message)
            Log.d("texts", "signUp: " + response.body?.string())
            Log.d("texts", "signUp: " + response.code)
            if (response.code == 201) {
                checkUser()
            }
        }.start()
    }

    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("texts", "onActivityResult: " + e.localizedMessage)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        if (auth == null) {
            auth = FirebaseAuth.getInstance()
        }
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    currentUser = auth?.currentUser
                    checkUser()
                } else {
                }
            }?.addOnFailureListener {

            }
    }

    private fun checkUser() {
        val client = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url("https://ap-south-1.aws.data.mongodb-api.com/app/application-0-btquy/endpoint/get/users?secret=tanmoy")
            .build()
        Thread {
            val response: Response = client.newCall(request).execute()
            Log.d("texts", "checkUser: ")
            if (response.isSuccessful) {
                val string1 = response.body?.string()
                val aub = AUB.fromJson(string1.toString())
                var num = 0
                aub.iterator().forEach {
                    if (it.email != null && it.email == currentUser?.email && num == 0) {
                        Log.d("texts", "checkUser: $it")
                        currentUserData = it
                        num++
                    }
                }
                if (num == 1) {
                    if (currentUserData != null && currentUserData!!.name != null && currentUserData!!.email != null) {
                        finish()
                    }
                } else {
                    runOnUiThread {
                        inflate.sbtn.performClick()
                    }
                }
            }
        }.start()
    }


    companion object {
        @JvmStatic
        var auth: FirebaseAuth? = null

        @JvmStatic
        var currentUser: FirebaseUser? = null

        @JvmStatic
        var currentUserData: Users? = null
    }

}
