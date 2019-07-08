package com.clearmind.animeland.home

import com.google.firebase.auth.FirebaseUser

interface MainNavigator {

    fun goSignOut()
    fun updateUI(user: FirebaseUser?)
}