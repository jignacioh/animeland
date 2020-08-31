package com.clearmind.animeland.login

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.clearmind.animeland.core.base.BaseViewModel
import com.clearmind.animeland.register.LoginForm
import com.google.firebase.auth.FirebaseAuth
import android.app.Application
import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.clearmind.animeland.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText


class LoginViewModel() : BaseViewModel<LoginNavigator>() {

    var form = LoginForm()

    private var onFocusEmail: View.OnFocusChangeListener?= null
    private var onFocusPassword: View.OnFocusChangeListener?= null

    init {

        onFocusEmail = View.OnFocusChangeListener { view, focused ->
            val et = view as EditText
            if (et.text.isNotEmpty() && !focused) {
                form.isEmailValid(true)
            }
        }

        onFocusPassword = View.OnFocusChangeListener { view, focused ->
            val et = view as EditText
            if (et.text.isNotEmpty()&& !focused) {
                form.isPasswordValid(true)
            }
        }
        Log.d("viewmodel", "createUserWithEmail:success")
    }

    fun goToHomeActivity() {
        form.onClick()
    }

    fun goToRegisterActivity(){
        getNavigator()!!.goToRegisterView()
    }
    fun loginWithSocialNetwork(){
        getNavigator()!!.goSignIn()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {

        //tasksLiveData.postValue( repository.getTasks())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy(){
        Log.i("onDestroy","onDestroy viewModel")
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
        fun bindFocusChange(textInputEditText: TextInputEditText, onFocusChangeListener: View.OnFocusChangeListener) {
            if (textInputEditText.onFocusChangeListener == null) {
                textInputEditText.onFocusChangeListener = onFocusChangeListener
            }
        }
    }

    fun getEmailOnFocusChangeListener(): View.OnFocusChangeListener? {
        return onFocusEmail
    }
    fun getPasswordOnFocusChangeListener(): View.OnFocusChangeListener? {
        return onFocusPassword
    }
}