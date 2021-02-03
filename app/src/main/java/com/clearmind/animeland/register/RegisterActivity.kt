package com.clearmind.animeland.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseActivity
import com.clearmind.animeland.core.di.Constants
import com.clearmind.animeland.databinding.ActivityRegisterBinding
import com.clearmind.animeland.home.HomeActivity
import com.clearmind.animeland.login.LoginActivity
import com.clearmind.animeland.login.LoginViewModel
import com.clearmind.animeland.model.auth.Profile
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.model.authentication.AuthModel
import com.clearmind.animeland.usecase.AuthUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterActivity: BaseActivity<ActivityRegisterBinding, RegisterViewModel>(), RegisterNavigator {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    //Google Sign In Client
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    //Firebase Authentication
    private lateinit var mAuth: FirebaseAuth

    override val viewModel: RegisterViewModel
        get() {
            val model: RegisterViewModel by viewModel()
            return model
        }

    override val layoutId: Int
        get() = R.layout.activity_register
    override val bindingVariable: Int
        get() = BR.registerViewModel

    private var mActivityTasksBinding: ActivityRegisterBinding? = null

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
                firebaseAuthWithEmail(loginModel)
            })
    }

    override fun doRegister(email: String?, password: String?) {

        mAuth.createUserWithEmailAndPassword(email!!,password!!)
            .addOnCompleteListener{ task ->
                visibleLoaderDialog()
                if (task.isSuccessful) {
                    // Sign in success
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                } else {
                    // If sign in fails
                    showSimpleDialog("Error","Ha ocurrido un error inténtelo de nuevo más tarde.")
                }
            }
    }

    private fun firebaseAuthWithEmail(loginModel: AuthModel) {
        //visibleLoaderDialog()
        viewModel.signInWithEmailCredentials(loginModel)
        viewModel.authenticatedUserLiveData.observe(this) { authenticatedUser ->
            //visibleLoaderDialog()
            /*if (authenticatedUser.isNew) {
                createNewUser(authenticatedUser)
            } else {
                goToMainActivity(authenticatedUser)
            }*/

            when (val loginState = authenticatedUser!!) {
                is LoginViewModel.LoginState.Error -> {
                    hideProgressDialog()
                    when(loginState.failure){
                        is AuthUseCase.GetAuthFailure.FireStoreSignIn -> showSimpleDialog(getString(R.string.app_name), "SignIn FireStore Error")
                        is AuthUseCase.GetAuthFailure.EmailRegister -> showSimpleDialog(getString(R.string.app_name), "Email Register Error")
                        is AuthUseCase.GetAuthFailure.EmailSignIn -> showSimpleDialog(getString(R.string.app_name), "EmailSignIn Error")
                        else -> showSimpleDialog(getString(R.string.app_name), "Error Generico")
                    }

                    //showSimpleDialog(getString(R.string.app_name), loginState.failure.exception.message!!)
                }
                is LoginViewModel.LoginState.LOADING -> showProgressDialog()
                is LoginViewModel.LoginState.Success -> {
                    if (loginState.profileModel.isNew) {
                        createNewUser(loginState.profileModel)
                    } else {
                        hideProgressDialog()
                        goToMainActivity(loginState.profileModel)
                    }
                }
                is LoginViewModel.LoginState.FireStoreSuccess -> {
                    hideProgressDialog()
                    if (loginState.profileModel.isCreated) {
                        toastMessage("Welcome ".plus(loginState.profileModel.name))
                    } else {
                        toastMessage("Welcome again ".plus(loginState.profileModel.name))
                    }
                    goToMainActivity(loginState.profileModel)
                }
            }

        }
    }

    private fun createNewUser(authenticatedProfile: ProfileModel) {
        viewModel.createUser(authenticatedProfile)
        viewModel.createdUserLiveData.observe(this) { user ->
            if (user.isCreated) {
                toastMessage("Welcome ".plus(user.name))
            } else {
                toastMessage("Welcome again ".plus(user.name))
            }
            goToMainActivity(user)
        }
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

    override fun onFragmentAttached() {
        TODO("Not yet implemented")
    }

    override fun onFragmentDetached() {
        TODO("Not yet implemented")
    }

    override fun showFailureRegister(message: String) {
        hideProgressDialog()
        toastMessage(message)
    }

}