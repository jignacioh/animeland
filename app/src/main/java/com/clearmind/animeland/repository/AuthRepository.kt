package com.clearmind.animeland.repository

import androidx.lifecycle.MutableLiveData
import com.clearmind.animeland.model.auth.Profile
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.model.auth.ProfileResponse
import com.clearmind.animeland.model.authentication.AuthModel
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun firebaseSignInWithMultiplesCredentials(otherAuthCredential: AuthCredential) : AuthResult?

    suspend fun createUserInFirestoreIfNotExists(authenticatedProfile : ProfileModel) : ProfileModel

    suspend fun insertUserInRoom(profile : Profile, profileModel: ProfileModel)

    suspend fun facebookSignInWithCredentials(accessToken: AccessToken) : AuthResult?

    suspend fun emailSignInCredentials(loginModel: AuthModel): ProfileResponse

    fun checkIfUserIsAuthenticatedInFirebase() : ProfileResponse //MutableLiveData<ProfileResponse>

    suspend fun addUserToLiveData(uid: String) : ProfileResponse

    suspend fun requestProfileData(uid: String): ProfileResponse

    suspend fun insertProfileInRoom(profile : Profile)

    suspend fun getProfileById(uid: String): Profile?

    suspend fun deleteProfileInDB(uid : String)

    suspend fun emailAndPassSignInCredentials(loginModel: AuthModel): FirebaseUser
}