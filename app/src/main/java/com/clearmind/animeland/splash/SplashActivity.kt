package com.clearmind.animeland.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.clearmind.animeland.R
import com.clearmind.animeland.home.HomeActivity
import com.clearmind.animeland.login.LoginActivity
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*


class SplashActivity : AppCompatActivity() {

    private var activityScope = CoroutineScope(Dispatchers.Main)
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    //Firebase Auth
    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    companion object {
        const val RC_SIGN_IN = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        activityScope.launch {
            delay(3000)
            navigateNextScreen()
        }
    }

    private fun navigateNextScreen() {
        user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val nombre = it.displayName
            val email = it.email
            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
            intent.putExtra("correo",email)
            intent.putExtra("nombre",nombre)
            startActivity(intent)
            finish()
        }?:run {
            val homeIntent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(homeIntent)
            finish()
        }
        /*mAuthListener = FirebaseAuth.AuthStateListener {
            firebaseAuth: FirebaseAuth ->
            user = firebaseAuth.currentUser
            if(user != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            }
        }*/

        // Choose authentication providers
        /*val providers: List<IdpConfig> = listOf(
                EmailBuilder().build(),
                PhoneBuilder().build(),
                GoogleBuilder().build(),
                FacebookBuilder().build())
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_symbol)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN)*/

    }

    private fun initValues(){

    }

    @ExperimentalCoroutinesApi
    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.e("ERROR", "splash 1")
            }
        }
    }
}