package com.clearmind.animeland.register

import com.clearmind.animeland.R

class LoginErrorFields {
    private var email: Int? = R.string.error_no_mail
    private var password: Int? = R.string.error_no_pass

    fun getEmail(): Int? {
        return email
    }

    fun setEmail(email: Int?) {
        this.email = email
    }

    fun getPassword(): Int? {
        return password
    }

    fun setPassword(password: Int?) {
        this.password = password
    }
}