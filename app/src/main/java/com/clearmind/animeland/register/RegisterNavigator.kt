package com.clearmind.animeland.register

interface RegisterNavigator {

    fun doRegister(email: String?, password: String?)

    fun showFailureRegister(message: String)
}