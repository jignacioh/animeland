package com.clearmind.animeland.login

import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.clearmind.animeland.core.di.Failure
import com.clearmind.animeland.core.base.BaseViewModel
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.model.authentication.AuthModel
import com.clearmind.animeland.form.auth.LoginForm
import com.clearmind.animeland.usecase.AuthUseCase
import com.facebook.AccessToken
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.*


class LoginViewModel(private val getAuthUseCase: AuthUseCase): BaseViewModel<LoginNavigator>(), CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO

    var form = LoginForm()
    var authenticatedUserLiveData : MutableLiveData<LoginState> = MutableLiveData()

    private var onFocusEmail : View.OnFocusChangeListener?= null
    private var onFocusPassword : View.OnFocusChangeListener?= null

    sealed class LoginState {
        object LOADING : LoginState()
        data class Error(val failure: Failure) : LoginState()
        data class Success(val profileModel: ProfileModel) : LoginState()
        data class FireStoreSuccess(val profileModel: ProfileModel) : LoginState()
        data class EmailSuccess(val profileModel: ProfileModel) : LoginState()
        data class FacebookSuccess(val profileModel: ProfileModel) : LoginState()
    }


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
        //Log.d("viewmodel", "createUserWithEmail:success")
    }

    fun goToHomeActivity() {
        form.onClick()
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

    fun signInWithMultiplesCredentials(credential: AuthCredential) {
        authenticatedUserLiveData.postValue(LoginState.LOADING)
        viewModelScope.launch {
            getAuthUseCase.execFirebaseSignIn(credential).either(::handleFailure, ::handleSuccess)
        }
        /*val params = AuthUseCase.Params(credential)
        getAuthUseCase.invoke(viewModelScope,params) {
            it.either(::handleFailureSignIn, ::handleSuccess)
        }*/
    }

    fun createUser(authenticatedProfile: ProfileModel) {
        async(coroutineContext) {
            getAuthUseCase.execFireStoreCreate(authenticatedProfile).let {
                it.either(::handleFailure, ::handleSuccess)
            }
        }
    }

    fun signInWithFacebookCredentials(token: AccessToken) {
        authenticatedUserLiveData.value = LoginState.LOADING
        async(coroutineContext) {
            getAuthUseCase.execFacebookSignInWithCredentials(token).let {
                it.either(::handleFailure, ::handleSuccess)
            }
        }
    }

    fun signInAuthCredentials(authModel: AuthModel) {
        authenticatedUserLiveData.value = LoginState.LOADING
        async(coroutineContext) {
            getAuthUseCase.execSignInWithCredentials(authModel).let {
                it.either(::handleFailure, ::handleSuccess)
            }
        }
    }

    private fun handleSuccess(loginState: LoginState) {
        authenticatedUserLiveData.postValue(loginState)
    }

    override fun handleFailure(failure: Failure) {
        authenticatedUserLiveData.postValue(LoginState.Error(failure))
    }

    fun getLiveProfile() = authenticatedUserLiveData

}