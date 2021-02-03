package com.clearmind.animeland.model.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.io.Serializable

class ProfileModel: Serializable {

    lateinit var uid: String
    var name: String? = null
    var email: String? = null

    var isAuthenticated: Boolean = false
    var isNew: Boolean = false
    var isCreated: Boolean = false

    var lastLongitude: Double? = 0.0
    var lastLatitude: Double? = 0.0
    var photo: String? = null

    constructor()

    constructor(email: String){
        this.email = email
    }

    constructor(profile: ProfileResponse) {
        this.uid = profile.uid
        this.name = profile.name
        this.isNew = profile.isNew
        this.email = profile.email
        this.isAuthenticated = profile.isAuthenticated
        this.isCreated = profile.isCreated
        this.lastLongitude = profile.lastLongitude
        this.lastLatitude = profile.lastLatitude
        this.photo = profile.photo
    }

    constructor(firebaseUser: FirebaseUser, authResult: AuthResult) {
        this.uid = firebaseUser.uid
        this.name = firebaseUser.displayName
        this.email = firebaseUser.email
        this.isNew = authResult.additionalUserInfo!!.isNewUser
        this.isAuthenticated = false
        this.isCreated = false
        this.lastLongitude = 0.0
        this.lastLatitude = 0.0
        this.photo = firebaseUser.photoUrl.toString()
    }

    constructor(profile: Profile) {
        this.uid = profile.uid
        this.name = profile.name
        this.email = profile.email
        this.isNew = profile.isNew
        this.isAuthenticated = profile.isAuthenticated
        this.isCreated = profile.isCreated
        this.lastLongitude = profile.lastLongitude
        this.lastLatitude = profile.lastLatitude
        this.photo = profile.photo
    }

}