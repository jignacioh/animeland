package com.clearmind.animeland.home

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.clearmind.animeland.core.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : BaseViewModel<MainNavigator>(),LifecycleObserver {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    fun goToLoginActivity() {
        getNavigator()!!.goSignOut()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val currentUser = auth.currentUser
        getNavigator()!!.updateUI(currentUser)
    }

}