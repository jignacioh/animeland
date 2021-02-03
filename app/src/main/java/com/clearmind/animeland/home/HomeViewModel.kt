package com.clearmind.animeland.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.clearmind.animeland.core.di.Failure
import com.clearmind.animeland.core.base.BaseViewModel
import com.clearmind.animeland.usecase.AuthUseCase
import com.clearmind.animeland.model.auth.ProfileModel
import kotlinx.coroutines.*

class HomeViewModel(val getAuthUseCase: AuthUseCase): BaseViewModel<HomeNavigator>(), CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO

    var isUserAuthenticatedLiveData: MutableLiveData<ProfileModel> = MutableLiveData()
    var userLiveData: MutableLiveData<ProfileModel> = MutableLiveData()

    var state = MutableLiveData<SplashState>().apply {
        this.value = SplashState.None
    }

    sealed class SplashState {
        object None : SplashState()
        object ErrorSplash : SplashState()
        object LoadingSplash : SplashState()
        object SuccessSplash : SplashState()
    }

    fun persistUserAuthenticated(profileModel: ProfileModel) {
        launch(coroutineContext) {
            getAuthUseCase.execDatabaseOperation(profileModel)
        }
    }

    private fun handleSuccessAddUserToLiveData(mutableLiveData: ProfileModel) {
        userLiveData.postValue(mutableLiveData)
    }

    private fun handleSuccess(mutableLiveData: ProfileModel) {
        isUserAuthenticatedLiveData.postValue(mutableLiveData)
    }

    override fun handleFailure(failure: Failure) {
        Log.e("ERROR",failure.exception.message!!)
        getNavigator()?.doSomething()
    }

    private fun handleFailureAuthenticated(failure: Failure) {
        Log.e("ERROR",failure.exception.message!!)
        getNavigator()?.doSomething()
    }
}