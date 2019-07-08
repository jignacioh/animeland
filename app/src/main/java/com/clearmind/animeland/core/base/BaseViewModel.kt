package com.clearmind.animeland.core.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*

import java.lang.ref.WeakReference

abstract class BaseViewModel<N> : ViewModel(), LifecycleObserver {
    private val mIsLoading = ObservableBoolean(false)

    private var mNavigator: WeakReference<N>? = null



    fun getIsLoading(): ObservableBoolean {
        return mIsLoading
    }

    fun setIsLoading(isLoading: Boolean) {
        mIsLoading.set(isLoading)
    }

    fun getNavigator(): N? {
        return mNavigator?.get()
    }

    fun setNavigator(navigator: N) {
        this.mNavigator = WeakReference(navigator)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun removeObserver(){

    }

}
