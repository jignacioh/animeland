package com.clearmind.animeland.updateprofile

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.clearmind.animeland.core.base.BaseViewModel
import com.clearmind.animeland.core.di.Failure
import com.clearmind.animeland.setting.SettingNavigator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class UpdateUserViewModel() : BaseViewModel<SettingNavigator>(), CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.Main
    val user = FirebaseAuth.getInstance().currentUser

    sealed class UpdateUserState {
        object None : UpdateUserState()
        object Loading : UpdateUserState()
    }


    val state = MutableLiveData<UpdateUserState>().apply {
        this.value = UpdateUserState.None
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {

        //tasksLiveData.postValue( repository.getTasks())

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        Log.i("onDestroy","onDestroy viewModel")
    }

    override fun handleFailure(failure: Failure) {

    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}