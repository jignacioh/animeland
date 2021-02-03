package com.clearmind.animeland.repository

import android.util.Log
import com.clearmind.animeland.core.base.BaseRepository
import com.clearmind.animeland.core.di.Constants
import com.clearmind.animeland.core.platform.NetworkHandler
import com.clearmind.animeland.model.auth.Profile
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.model.auth.ProfileResponse
import com.clearmind.animeland.model.authentication.AuthModel
import com.clearmind.animeland.model.dao.ProfileDao
import com.clearmind.animeland.utils.HelperClass.logErrorMessage
import com.facebook.AccessToken
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class AuthRepositoryImpl(private val networkHandler: NetworkHandler, private val profileDao: ProfileDao) : BaseRepository(), AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val rootRef = FirebaseFirestore.getInstance()
    private val usersRef = rootRef.collection("USERS")
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override suspend fun firebaseSignInWithMultiplesCredentials(otherAuthCredential: AuthCredential): AuthResult? {
        return when (networkHandler.isNetworkAvailable()) {
            true -> firebaseAuth.signInWithCredential(otherAuthCredential).await()
            false -> null
        }
    }

    override suspend fun createUserInFirestoreIfNotExists(authenticatedProfile: ProfileModel): ProfileModel {
        return try{
            val uidRef = usersRef.document(authenticatedProfile.uid)
            val data = uidRef.get().await()
            if (!data.exists()) {
                uidRef.set(authenticatedProfile).await()
                authenticatedProfile.isCreated = true
                authenticatedProfile
            } else {
                authenticatedProfile.isCreated = false
                authenticatedProfile
            }
        }catch (e : Exception){
            authenticatedProfile.isCreated = false
            authenticatedProfile
        }
    }

    override suspend fun insertUserInRoom(profile: Profile, profileModel: ProfileModel) {
        profileDao.insert(profile)
        try {
            database.child(Constants.USERS).child(profile.uid).setValue(profile)
            usersRef.document(profile.uid).set(profileModel)
        } catch (e : Exception) {
            Log.e("WARNING", e.message!!)
        }
    }

    override suspend fun deleteProfileInDB(uid: String) {
        profileDao.deleteById(uid)
    }

    override suspend fun facebookSignInWithCredentials(accessToken: AccessToken): AuthResult? {
        /*return try {
            val credential = FacebookAuthProvider.getCredential(accessToken.token)
            val data = firebaseAuth.signInWithCredential(credential).await()
            data
        } catch (e: Exception) {
            //val profileResponse = ProfileResponse(1001, e.message!!)
            logErrorMessage(e.message)
            //authenticatedUserMutableLiveData.value = profileResponse
            null
        }*/

        return when (networkHandler.isNetworkAvailable()) {
            true -> {
                val credential = FacebookAuthProvider.getCredential(accessToken.token)
                val data = firebaseAuth.signInWithCredential(credential).await()
                data
            }
            false -> null
        }
    }

    override suspend fun emailSignInCredentials(loginModel: AuthModel): ProfileResponse {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(loginModel.email!!, loginModel.password!!).await()
            val firebaseUser = firebaseAuth.currentUser
            val isNewUser = result.additionalUserInfo!!.isNewUser
            return if (firebaseUser != null) {
                val uid = firebaseUser.uid
                val name = firebaseUser.displayName
                val email = firebaseUser.email
                ProfileResponse(uid, name, email, false, isNewUser, false, 0.0, 0.0, null, 1000, "Successful")
            } else {
                ProfileResponse.error
            }
        } catch (e: Exception) {
            logErrorMessage(e.message)
            return ProfileResponse.error
        }
    }

    override suspend fun emailAndPassSignInCredentials(loginModel: AuthModel): FirebaseUser {
        firebaseAuth.signInWithEmailAndPassword(loginModel.email!!, loginModel.password!!).await()
        return firebaseAuth.currentUser ?: throw FirebaseAuthException("", "")
    }

    override fun checkIfUserIsAuthenticatedInFirebase(): ProfileResponse {
        val firebaseUser = firebaseAuth.currentUser
        val profileResponse: ProfileResponse
        profileResponse = if (firebaseUser == null) {
            ProfileResponse.error
        } else {
            val uid = firebaseUser.uid
            val name = firebaseUser.displayName
            val email = firebaseUser.email
            val isAuthenticated = true
            val photo = firebaseUser.photoUrl.toString()
            ProfileResponse(uid, name, email,  isAuthenticated, false, false, 0.0 ,0.0, photo, 1000, "Successful")
        }
        return profileResponse
    }

    override suspend fun addUserToLiveData(uid: String): ProfileResponse {
        val profileResponse: ProfileResponse?
        return try {
            val document = usersRef.document(uid).get().await()
            profileResponse = if (document.exists()){
                document.toObject(ProfileResponse::class.java)
            } else {
                ProfileResponse(1001, "Data no exist")
            }
            profileResponse!!.isAuthenticated = true
            profileResponse
        }catch (exception: Exception){
            val response = ProfileResponse(1001, exception.message!!)
            response
        }
    }

    override suspend fun getProfileById(uid: String): Profile? {
        return profileDao.getById(uid)
    }

    override suspend fun insertProfileInRoom(profile: Profile) {
        profileDao.insert(profile)
        database.child("USERS").child(profile.uid).setValue(profile)
    }

    override suspend fun requestProfileData(uid: String): ProfileResponse {
        val profileResponse: ProfileResponse?
        return try {
            val document = usersRef.document(uid).get().await()
            profileResponse = if (document.exists()){
                document.toObject(ProfileResponse::class.java)
            } else {
                ProfileResponse(1001, "Data no exist")
            }
            profileResponse!!
        }catch (exception: Exception){
            val response = ProfileResponse(1001, exception.message!!)
            response
        }
    }
}