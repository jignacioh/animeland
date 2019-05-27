package com.clearmind.animeland.register

interface RegisterNavigator {

    fun goBack()
    fun doRegister(email: String?, password: String?)
}