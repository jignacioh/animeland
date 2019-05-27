package com.clearmind.animeland.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.clearmind.animeland.R
import com.clearmind.animeland.BR
import com.clearmind.animeland.core.base.BaseActivity
import com.clearmind.animeland.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.ext.android.viewModel
import android.widget.Toast
import com.clearmind.animeland.MainActivity
import com.clearmind.animeland.register.RegisterActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity: BaseActivity<ActivityLoginBinding, LoginViewModel>(),LoginNavigator {

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    //Google Sign In Client
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    //Firebase Auth
    private lateinit var mAuth: FirebaseAuth

    override val viewModel: LoginViewModel
    get() {
        val model: LoginViewModel by viewModel()
        return model
    }

    override val layoutId: Int
    get() = R.layout.activity_login
    override val bindingVariable: Int
    get() = BR.loginViewModel

    private var mActivityTasksBinding: ActivityLoginBinding? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityTasksBinding = viewDataBinding
        viewModel.setNavigator(this)
        lifecycle.addObserver(viewModel)
        Log.i("onCreate","onCreate activity")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        supportActionBar!!.hide()

        btn_log_in.setOnClickListener {
            goSignIn()
        }

       // supportActionBar!!.hide()

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)

    }

    private fun updateUI(currentUser: FirebaseUser?) {
        hideProgressDialog()
        if(currentUser != null){
            //Do your Stuff
            Toast.makeText(this,"Hello ${currentUser.displayName}",Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }else{
            Log.i(TAG, "No Google sign")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("onResume","onResume activity")
        //viewModel.tasksLiveData.observe(this,observerTask)
    }

    /*val observerTask = Observer<MutableList<Task>> { value ->
        tasks.addAll(value)
        mActivityTasksBinding!!.recyclerView.adapter!!.notifyDataSetChanged()
    }*/


    override fun showAction(state : Boolean) {

        Log.i("showAction", "doAction")
    }

    override fun showError() {

    }
    override fun onDestroy() {
        super.onDestroy()
        //viewModel.tasksLiveData.removeObserver(observerTask)
    }

    override fun goSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun goToRegisterView() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        showProgressDialog()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }


}