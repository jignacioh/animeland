package com.clearmind.animeland.login

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.clearmind.animeland.core.base.BaseViewModel
import com.clearmind.animeland.register.LoginForm
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel() : BaseViewModel<LoginNavigator>() {

    var form = LoginForm()

    fun goToHomeActivity() {
        getNavigator()!!.goSignIn()
    }

    fun goToRegisterActivity(){
        getNavigator()!!.goToRegisterView()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {

        //tasksLiveData.postValue( repository.getTasks())

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        Log.i("onDestroy","onDestroy viewModel")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("onCleared","onCleared viewModel")
    }
}