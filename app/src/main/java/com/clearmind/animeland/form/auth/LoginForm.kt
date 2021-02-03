package com.clearmind.animeland.form.auth

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.clearmind.animeland.model.authentication.AuthModel
import androidx.lifecycle.MutableLiveData
import com.clearmind.animeland.BR
import com.clearmind.animeland.R


class LoginForm: BaseObservable() {
    private val fields = AuthModel()
    private val errors = LoginErrorFields()
    private val liveDataLoginForm = MutableLiveData<AuthModel>()

    @Bindable
    fun isValid() : Boolean {
        var valid = isEmailValid(false)
        valid = isPasswordValid(false) && valid
        notifyPropertyChanged(BR.emailError)
        notifyPropertyChanged(BR.passwordError)
        return valid
    }

    fun getLoginFields(): MutableLiveData<AuthModel> {
        return liveDataLoginForm
    }
    fun getFields(): AuthModel {
        return fields
    }

    fun isEmailValid(setMessage: Boolean) : Boolean{
        val email = fields.email
        if (email != null && email.length > 5) {
            val indexOfAt = email.indexOf("@")
            val indexOfDot = email.lastIndexOf(".")
            return if (indexOfAt in 1 until indexOfDot && indexOfDot < email.length - 1) {
                errors.setEmail(null)
                notifyPropertyChanged(BR.valid)
                true
            } else {
                if (setMessage) {
                    errors.setEmail(R.string.error_format_invalid)
                    notifyPropertyChanged(BR.valid)
                }
                false
            }
        }
        if (setMessage) {
            errors.setEmail(R.string.error_too_short)
            notifyPropertyChanged(BR.valid)
        }

        return false
    }
    fun isPasswordValid(setMessage: Boolean) : Boolean{
        val password = fields.password
        return if (password != null && password.length > 5) {
            errors.setPassword(null)
            notifyPropertyChanged(BR.valid)
            true
        } else {
            if (setMessage) {
                errors.setPassword(R.string.error_pass_too_short)
                notifyPropertyChanged(BR.valid)
            }
            false
        }
    }

    fun onClick() {
        if (isValid()) {
            liveDataLoginForm.value = fields
        }
    }
    @Bindable
    fun getEmailError(): Int? {
        return errors.getEmail()
    }

    @Bindable
    fun getPasswordError(): Int? {
        return errors.getPassword()
    }
    fun getLiveDataLoginForm(): MutableLiveData<AuthModel> {
        return liveDataLoginForm
    }


}