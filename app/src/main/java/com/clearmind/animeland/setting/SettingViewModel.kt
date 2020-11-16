package com.clearmind.animeland.setting

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.clearmind.animeland.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class SettingViewModel() : BaseViewModel<SettingNavigator>() , CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.Main
    val user = FirebaseAuth.getInstance().currentUser

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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        Log.i("onDestroy","onDestroy viewModel")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}