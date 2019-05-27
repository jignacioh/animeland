package com.clearmind.animeland.login

interface LoginNavigator {

    fun showAction(state : Boolean)

    fun showError()

    fun goSignIn()

    fun goToRegisterView()

}