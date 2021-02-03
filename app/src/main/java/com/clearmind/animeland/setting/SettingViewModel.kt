package com.clearmind.animeland.setting

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.clearmind.animeland.core.di.Failure
import com.clearmind.animeland.core.base.BaseViewModel
import com.clearmind.animeland.usecase.AuthUseCase
import com.clearmind.animeland.model.auth.ProfileModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class SettingViewModel(val getAuthUseCase: AuthUseCase) : BaseViewModel<SettingNavigator>() , CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO
    val user = FirebaseAuth.getInstance().currentUser
    var profileLiveData : MutableLiveData<ProfileModel> = MutableLiveData()

    sealed class SettingState {
        object None : SettingState()
        object Loading : SettingState()
    }


    val state = MutableLiveData<SettingState>().apply {
        this.value = SettingState.None
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {

        //tasksLiveData.postValue( repository.getTasks())

    }

    fun signOutCredentials(uid: String) {
        launch(coroutineContext) {
            getAuthUseCase.execSignOutOperation(uid)
        }
    }

    fun loadProfileInformation() {
        //authLoadingStateLiveData.value = LoginViewModel.LoginState.LOADING
        async(coroutineContext) {
            getAuthUseCase.getProfileInformation().let {
                launch(coroutineContext) {
                    it.either(::handleFailureProfile, ::handleSuccessProfile)
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        Log.i("onDestroy","onDestroy viewModel")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    private fun handleSuccessProfile(profileInfoModel: ProfileModel) {
        profileLiveData.postValue(profileInfoModel)
    }

    private fun handleFailureProfile(failure: Failure) {
        Log.e("ERROR",failure.exception.message!!)
        //profileLiveData.value = LoginViewModel.LoginState.ERROR

    }

    override fun handleFailure(failure: Failure) {

    }
}