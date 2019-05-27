package com.clearmind.animeland.register

import android.os.Bundle
import android.util.Log
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseActivity
import com.clearmind.animeland.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.ext.android.viewModel
import com.clearmind.animeland.MainActivity
import android.content.Intent
import androidx.lifecycle.Observer
import com.clearmind.animeland.model.authentication.AuthModel




class RegisterActivity: BaseActivity<ActivityRegisterBinding, RegisterViewModel>(), RegisterNavigator {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    //Google Sign In Client
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    //Firebase Auth
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
        Log.i("onCreate","onCreate activity")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        supportActionBar!!.hide()

        initObservers()

    }

    private fun initObservers(){
        viewModel.form.getLiveDataLoginForm().observe(this,
            Observer<AuthModel> { loginModel ->
                visibleLoaderDialog()
                doRegister(loginModel.email,loginModel.password)

            })
    }

    override fun goBack() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doRegister(email: String?, password: String?) {
        mAuth.createUserWithEmailAndPassword(email!!,password!!)
            .addOnCompleteListener{ task ->
                visibleLoaderDialog()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    //val user = mAuth.currentUser
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    showSimpleDialog("Error","Ha ocurrido un error intentelo de nuevo más tarde.")
                }
            }
    }

}