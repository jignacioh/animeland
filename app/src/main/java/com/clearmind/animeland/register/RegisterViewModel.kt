package com.clearmind.animeland.register

import android.util.Log
import com.clearmind.animeland.core.base.BaseViewModel
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.clearmind.animeland.core.di.Failure
import com.clearmind.animeland.form.auth.LoginForm
import com.clearmind.animeland.login.LoginViewModel
import com.clearmind.animeland.usecase.AuthUseCase
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.model.authentication.AuthModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import kotlin.math.log


class RegisterViewModel(private val getAuthUseCase: AuthUseCase): BaseViewModel<RegisterNavigator>(), CoroutineScope{

    override val coroutineContext = Job() + Dispatchers.IO
    var form = LoginForm()

    var authenticatedUserLiveData : MutableLiveData<LoginViewModel.LoginState> = MutableLiveData()
    var createdUserLiveData: MutableLiveData<ProfileModel> = MutableLiveData()

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
             if (et.text.isNotEmpty() && !focused) {
                 form.isPasswordValid(true)
             }
         }
         Log.d("viewmodel", "createUserWithEmail:success")
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

    fun signInWithEmailCredentials(loginModel: AuthModel) {
        //authenticatedUserLiveData = getAuthRepositoryImpl.emailSignInCredentials(loginModel)
        //getAuthUseCase.execFirebaseRegister(loginModel).either(::handleFailure, ::handleSuccess)
        authenticatedUserLiveData.postValue(LoginViewModel.LoginState.LOADING)
        async(coroutineContext) {
            getAuthUseCase.execFirebaseRegister(loginModel).let {
                launch(  Job() + Dispatchers.Main) {
                    it.either(::handleFailure, ::handleSuccess)
                }
            }
        }
    }

    fun createUser(authenticatedProfile: ProfileModel) {
        //createdUserLiveData = getAuthRepositoryImpl.createUserInFirestoreIfNotExists(authenticatedProfile)
        //getAuthUseCase.execFireStoreCreate(authenticatedProfile).either(::handleFailure, ::handleSuccessFirestore)
        async(coroutineContext) {
            getAuthUseCase.execFireStoreCreate(authenticatedProfile).let {
                launch(  Job() + Dispatchers.Main) {
                    it.either(::handleFailure, ::handleSuccessFirestore)
                }
            }
        }
    }

    private fun handleSuccess(loginState: LoginViewModel.LoginState) {
        authenticatedUserLiveData.postValue(loginState)
    }

    private fun handleSuccessFirestore(loginState: LoginViewModel.LoginState) {
        authenticatedUserLiveData.postValue(loginState)
        //createdUserLiveData.postValue(mutableLiveData)
    }

    override fun handleFailure(failure: Failure) {
        getNavigator()!!.showFailureRegister(failure.exception.message!!)
        Log.e("ERROR",failure.exception.message!!)
    }
}