package com.clearmind.animeland.login

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseActivity
import com.clearmind.animeland.core.di.Constants
import com.clearmind.animeland.databinding.ActivityLoginBinding
import com.clearmind.animeland.home.HomeActivity
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.model.authentication.AuthModel
import com.clearmind.animeland.register.RegisterActivity
import com.clearmind.animeland.usecase.AuthUseCase
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity: BaseActivity<ActivityLoginBinding, LoginViewModel>(),LoginNavigator {

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var callbackManager: CallbackManager

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
        initViews()
        initObservers()
        initListeners()
    }

    private fun initListeners() {
        mActivityTasksBinding?.googleSignInButton?.setOnClickListener {
            initSignIn()
        }
        mActivityTasksBinding?.signInFacebookButton?.setReadPermissions("email")
        // Callback registration
        mActivityTasksBinding?.signInFacebookButton?.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
                loginResult?.accessToken?.let { initFacebookSignIn(it) }
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        } )
    }

    private fun initFacebookSignIn(accessToken: AccessToken) {
        viewModel.signInWithFacebookCredentials(accessToken)

        /*viewModel.authenticatedUserLiveData.observe(this) { authenticatedUser ->
            when (val tasksState = authenticatedUser!!) {
                is LoginViewModel.LoginState.LOADING -> showProgressDialog()
                is LoginViewModel.LoginState.Success -> {
                    hideProgressDialog()
                    if (tasksState.profileModel.isNew) {
                        createNewUser(tasksState.profileModel)
                    } else {
                        goToMainActivity(tasksState.profileModel)
                    }
                }
            }
        }*/
    }

    private fun initValues(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        callbackManager = CallbackManager.Factory.create()
    }

    private fun initViews() {
        val ssSignUp = SpannableString(getString(R.string.label_sign_up))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                goToRegisterView()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ssSignUp.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        label_action_to_sign_up.text = ssSignUp
        label_action_to_sign_up.movementMethod = LinkMovementMethod.getInstance()
        label_action_to_sign_up.highlightColor = Color.TRANSPARENT

        val spannableStringForgotPass = SpannableString(getString(R.string.label_forgot_password))
        val clickSpanForgotPass: ClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                Toast.makeText(this@LoginActivity, "Forgot Password", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableStringForgotPass.setSpan(clickSpanForgotPass, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView_forgot_password.text = spannableStringForgotPass
        textView_forgot_password.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun initObservers(){
        viewModel.form.getLiveDataLoginForm().observe(this,
            Observer<AuthModel> { loginModel ->
                //visibleLoaderDialog()
                doLogin(loginModel)

            })

        viewModel.authenticatedUserLiveData.observe(this, Observer<LoginViewModel.LoginState> { value ->
            when (val loginState = value!!) {
                is LoginViewModel.LoginState.Error -> {
                    hideProgressDialog()
                    when(loginState.failure){
                        is AuthUseCase.GetAuthFailure.FirebaseSignIn -> showSimpleDialog(getString(R.string.app_name), "SignIn Firebase Error")
                        is AuthUseCase.GetAuthFailure.FireStoreSignIn -> showSimpleDialog(getString(R.string.app_name), "SignIn FireStore Error")
                        is AuthUseCase.GetAuthFailure.EmailSignIn -> showSimpleDialog(getString(R.string.app_name), "EmailSignIn Error")
                        is AuthUseCase.GetAuthFailure.FacebookSignIn -> showSimpleDialog(getString(R.string.app_name), "FacebookSignIn Error")

                        else -> showSimpleDialog(getString(R.string.app_name), "Error Generico")
                    }

                    //showSimpleDialog(getString(R.string.app_name), loginState.failure.exception.message!!)
                }
                is LoginViewModel.LoginState.LOADING -> showProgressDialog()
                is LoginViewModel.LoginState.Success -> {
                    hideProgressDialog()
                    if (loginState.profileModel.isNew) {
                        createNewUser(loginState.profileModel)
                    } else {
                        goToMainActivity(loginState.profileModel)
                    }
                }
                is LoginViewModel.LoginState.FireStoreSuccess -> {
                    toastMessage("Welcome ".plus(loginState.profileModel.name))
                    goToMainActivity(loginState.profileModel)
                }
                is LoginViewModel.LoginState.EmailSuccess -> {
                    toastMessage("Welcome ".plus(loginState.profileModel.name))
                    goToMainActivity(loginState.profileModel)
                }
                is LoginViewModel.LoginState.FacebookSuccess -> {
                    hideProgressDialog()
                    if (loginState.profileModel.isNew) {
                        createNewUser(loginState.profileModel)
                    } else {
                        goToMainActivity(loginState.profileModel)
                    }
                }
            }
        })
    }

    private fun doLogin(authModel: AuthModel) {
        viewModel.signInAuthCredentials(authModel)
    }

    override fun initSignIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun goToRegisterView() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        viewModel.signInWithMultiplesCredentials(credential)
    }

    private fun createNewUser(authenticatedProfile: ProfileModel) {
        viewModel.createUser(authenticatedProfile)
    }

    private fun goToMainActivity(profile: ProfileModel) {
        hideProgressDialog()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(Constants.USER, profile)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        //Result for Google sign in Client
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode ==  Activity.RESULT_OK) {
                try {
                    // Google Sign In successful
                    //val user = FirebaseAuth.getInstance().currentUser
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        firebaseAuthWithGoogle(account)
                    }
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                    Toast.makeText(this, "Ha ocurrido un error, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.i(TAG, "ResultCode RESULT_FAIL")
                Toast.makeText(this, "Ha ocurrido un error, inténtelo de nuevo más tarde.", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached() {

    }
}