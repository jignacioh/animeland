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
import androidx.lifecycle.Observer
import com.clearmind.animeland.home.HomeActivity
import com.clearmind.animeland.home.MainActivity
import com.clearmind.animeland.model.authentication.AuthModel
import com.clearmind.animeland.register.RegisterActivity
import com.clearmind.animeland.splash.SplashActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider




class LoginActivity: BaseActivity<ActivityLoginBinding, LoginViewModel>(),LoginNavigator {

    companion object {
        private const val TAG = "LoginActivity"
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

        initValues()
        initObservers()
    }

    private fun initValues(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initObservers(){
        viewModel.form.getLiveDataLoginForm().observe(this,
            Observer<AuthModel> { loginModel ->
                visibleLoaderDialog()
                doLogin(loginModel.email,loginModel.password)

            })
    }

    private fun doLogin(email: String?, password: String?) {
        showProgressDialog()
        mAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails
                    Log.w(TAG, "Sign:failure", task.exception)
                    Toast.makeText(this, "Error al ingresar, revise su correo y/o contraseña.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }


    override fun onStart() {
        super.onStart()
        // Check if user is in non-null and update UI.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)

    }

    private fun updateUI(currentUser: FirebaseUser?) {
        hideProgressDialog()
        //If exist user then go to home
        if(currentUser != null){

            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun goSignIn() {
        //val signInIntent = mGoogleSignInClient.signInIntent
        //startActivityForResult(signInIntent, RC_SIGN_IN)

        // Choose authentication providers
        val providers: List<AuthUI.IdpConfig> = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build())
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                SplashActivity.RC_SIGN_IN)
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
                    updateUI(null)
                    showSimpleDialog("Error","Ha ocurrido un error, inténtelo de nuevo más tarde.")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result for Google sign in Client
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In successful
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Ha ocurrido un error, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}