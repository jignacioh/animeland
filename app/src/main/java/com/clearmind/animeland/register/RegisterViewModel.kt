package com.clearmind.animeland.register

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.clearmind.animeland.core.base.BaseViewModel
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText





class RegisterViewModel: BaseViewModel<RegisterNavigator>() {

   // var tasksLiveData = MutableLiveData<MutableList<*>>()
    var form = LoginForm()

    private var onFocusEmail: OnFocusChangeListener?= null
    private var onFocusPassword: OnFocusChangeListener?= null

     init {
         onFocusEmail = OnFocusChangeListener { view, focused ->
             val et = view as EditText
             if (et.text.isNotEmpty() && !focused) {
                 form.isEmailValid(true)
             }
         }

         onFocusPassword = OnFocusChangeListener { view, focused ->
             val et = view as EditText
             if (et.text.isNotEmpty()&& !focused) {
                 form.isPasswordValid(true)
             }
         }
         Log.d("viewmodel", "createUserWithEmail:success")
     }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {

        //tasksLiveData.postValue( repository.getTasks())

    }
    /*
    fun actionRegister(vMail){
        getNavigator()!!.doRegister()
    }*/

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        Log.i("onDestroy","onDestroy viewModel")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("onCleared","onCleared viewModel")
    }
    companion object {
        @BindingAdapter("error")
        @JvmStatic
        fun setError(textInputEditText: TextInputEditText, strOrResId: Any?) {
            if (strOrResId!=null) {

                if (strOrResId is Int) {
                    textInputEditText.error = textInputEditText.context.getString(strOrResId)
                } else {
                    textInputEditText.error = strOrResId as String
                }
            }
        }

        @BindingAdapter("onFocus")
        @JvmStatic
        fun bindFocusChange(textInputEditText: TextInputEditText, onFocusChangeListener: OnFocusChangeListener) {
            if (textInputEditText.onFocusChangeListener == null) {
                textInputEditText.onFocusChangeListener = onFocusChangeListener
            }
        }
    }

    fun getEmailOnFocusChangeListener(): OnFocusChangeListener? {
        return onFocusEmail
    }
    fun getPasswordOnFocusChangeListener():OnFocusChangeListener? {
        return onFocusPassword
    }

    fun onRegisterClick() {
        form.onClick()
    }

}