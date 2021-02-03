package com.clearmind.animeland.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.clearmind.animeland.core.di.Failure
import kotlinx.coroutines.*
import com.clearmind.animeland.core.base.BaseViewModel
import com.clearmind.animeland.login.LoginViewModel
import com.clearmind.animeland.usecase.AuthUseCase
import com.clearmind.animeland.model.auth.ProfileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SplashViewModel(val getAuthUseCase: AuthUseCase): BaseViewModel<SplashNavigator>(), CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO

    var isUserAuthenticatedLiveData: MutableLiveData<SplashState> = MutableLiveData()

    var state = MutableLiveData<SplashState>().apply {
        this.value = SplashState.None
    }

    sealed class SplashState {
        object None : SplashState()
        object LoadingSplash : SplashState()
        data class ErrorSplash(val failure: Failure) : SplashState()
        data class SuccessValidSignIn(val profileModel: ProfileModel) : SplashState()
        data class SuccessVerifySignIn(val profileModel: ProfileModel) : SplashState()
    }

    fun checkIfUserIsAuthenticated() {
        state.value = SplashState.LoadingSplash
        async(coroutineContext) {
            getAuthUseCase.execUserIsAuthenticatedInFirebase().either(::handleFailureAuthenticated, ::handleSuccess)
        }
    }

    fun setUid(uid: String) {
        async(coroutineContext) {
            getAuthUseCase.execAddUserToLiveData(uid).either(::handleFailure, ::handleSuccessAddUserToLiveData)
        }
    }

    private fun handleSuccessAddUserToLiveData(state: SplashState) {
        isUserAuthenticatedLiveData.postValue(state)
    }

    private fun handleSuccess(state: SplashState) {
        isUserAuthenticatedLiveData.postValue(state)
    }

    override fun handleFailure(failure: Failure) {
        Log.e("ERROR",failure.exception.message!!)
        isUserAuthenticatedLiveData.postValue(SplashState.ErrorSplash(failure))
        //getNavigator()?.navigateToAuthScreen()
    }

    private fun handleFailureAuthenticated(failure: Failure) {
        Log.e("ERROR",failure.exception.message!!)
        isUserAuthenticatedLiveData.postValue(SplashState.ErrorSplash(failure))
        //getNavigator()?.navigateToAuthScreen()
    }
}