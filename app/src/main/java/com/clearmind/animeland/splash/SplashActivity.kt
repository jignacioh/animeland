package com.clearmind.animeland.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.clearmind.animeland.BR
import com.clearmind.animeland.R
import com.clearmind.animeland.core.base.BaseActivity
import com.clearmind.animeland.core.di.Constants
import com.clearmind.animeland.databinding.ActivitySplashBinding
import com.clearmind.animeland.home.HomeActivity
import com.clearmind.animeland.login.LoginActivity
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.usecase.AuthUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(),
        SplashNavigator {

    override val layoutId: Int
        get() = R.layout.activity_splash

    override val bindingVariable: Int
        get() = BR.splashViewModel

    override val viewModel: SplashViewModel
        get() {
            val viewModel: SplashViewModel by viewModel()
            return viewModel
        }
    private var mActivitySplashBinding: ActivitySplashBinding? = null

    companion object {
        const val RC_SIGN_IN = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySplashBinding = viewDataBinding
        viewModel.setNavigator(this)
        lifecycle.addObserver(viewModel)
        initObservers()
    }

    private fun initObservers() {
        viewModel.isUserAuthenticatedLiveData.observe(this, Observer<SplashViewModel.SplashState> { value ->
            when (val loginState = value!!) {
                is SplashViewModel.SplashState.ErrorSplash -> {
                    hideProgressDialog()
                    when(loginState.failure){
                        is AuthUseCase.GetAuthFailure.FirebaseSignIn -> {
                            //showSimpleDialog(getString(R.string.app_name), "SignIn Firebase Error")
                            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else -> showSimpleDialog(getString(R.string.app_name), "Error Generico")
                    }
                }
                is SplashViewModel.SplashState.LoadingSplash -> showProgressDialog()
                is SplashViewModel.SplashState.SuccessVerifySignIn -> {
                    hideProgressDialog()
                    if (!loginState.profileModel.isAuthenticated) {
                        goToAuthInActivity()
                        finish()
                    } else {
                        getUserFromDatabase(loginState.profileModel.uid)
                    }
                }
                is SplashViewModel.SplashState.SuccessValidSignIn -> {
                    hideProgressDialog()
                    goToMainActivity(loginState.profileModel)
                    finish()
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        performIfUserIsAuthenticated()

    }

    private fun performIfUserIsAuthenticated() {
        viewModel.checkIfUserIsAuthenticated()
    }

    @ExperimentalCoroutinesApi
    override fun onPause() {
        super.onPause()
    }

    override fun onFragmentAttached() {
        TODO("Not yet implemented")
    }

    override fun onFragmentDetached() {
        TODO("Not yet implemented")
    }

    override fun navigateToAuthScreen() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun goToAuthInActivity() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun getUserFromDatabase(uid: String) {
        viewModel.setUid(uid)
    }

    private fun goToMainActivity(profile: ProfileModel) {
        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
        intent.putExtra(Constants.USER, profile)
        startActivity(intent)
    }
}