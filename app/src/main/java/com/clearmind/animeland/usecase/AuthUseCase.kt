package com.clearmind.animeland.usecase

import com.clearmind.animeland.core.di.Failure
import com.clearmind.animeland.core.di.Either
import com.clearmind.animeland.core.executor.CoroutinesExecutor
import com.clearmind.animeland.repository.AuthRepositoryImpl
import com.clearmind.animeland.core.base.BaseResponse
import com.clearmind.animeland.login.LoginViewModel
import com.clearmind.animeland.model.auth.Profile
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.model.auth.ProfileResponse
import com.clearmind.animeland.model.authentication.AuthModel
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.clearmind.animeland.splash.SplashViewModel.SplashState

class AuthUseCase(val getAuthRepositoryImpl: AuthRepositoryImpl, val getCoroutinesExecutor: CoroutinesExecutor): UseCase<LoginViewModel.LoginState, AuthUseCase.Params>(getCoroutinesExecutor) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
    data class Params(val param: AuthCredential)

    data class GetAuthFailure(val error: Exception) : Failure.FeatureFailure(error){
        class FirebaseSignIn: FeatureFailure()
        class FireStoreSignIn: FeatureFailure()
        class EmailSignIn: FeatureFailure()
        class FacebookSignIn: FeatureFailure()
        class EmailRegister: FeatureFailure()
    }

    override suspend fun run(params: Params): Either<Failure,  LoginViewModel.LoginState> {
        return try {
            val responseEntity = getAuthRepositoryImpl.firebaseSignInWithMultiplesCredentials(params.param)
            responseEntity?.let {
                val isNewUser = it.additionalUserInfo!!.isNewUser
                val firebaseUser = firebaseAuth.currentUser
                if (isNewUser){
                    val modelToPresent = mapToPresent(firebaseUser, it)
                    Either.Right(LoginViewModel.LoginState.Success(modelToPresent!!))
                }else{
                    val responseFireStore = getAuthRepositoryImpl.addUserToLiveData(firebaseUser!!.uid)
                    if (responseFireStore.codeResponse == BaseResponse.RESPONSE_OK){
                        val modelToPresent = mapToPresent(responseFireStore)
                        if (modelToPresent != null) {
                            modelToPresent.isNew = false
                            Either.Right(LoginViewModel.LoginState.Success(modelToPresent))
                        }else {
                            Either.Left(GetAuthFailure.FireStoreSignIn())
                        }
                    }else {
                        Either.Left(GetAuthFailure.FireStoreSignIn())
                    }
                }
            }?:run {
                Either.Left(GetAuthFailure.FirebaseSignIn())
            }
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.FirebaseSignIn())
        }
    }

    suspend fun execFirebaseSignIn(credential: AuthCredential): Either<Failure, LoginViewModel.LoginState>   {
        return try {
            val responseEntity = getAuthRepositoryImpl.firebaseSignInWithMultiplesCredentials(credential)
            responseEntity?.let {
                val isNewUser = it.additionalUserInfo!!.isNewUser
                val firebaseUser = firebaseAuth.currentUser
                if (isNewUser){
                    val modelToPresent = mapToPresent(firebaseUser, it)
                    Either.Right(LoginViewModel.LoginState.Success(modelToPresent!!))
                }else{
                    val responseFireStore = getAuthRepositoryImpl.addUserToLiveData(firebaseUser!!.uid)
                    if (responseFireStore.codeResponse == BaseResponse.RESPONSE_OK){
                        val modelToPresent = mapToPresent(responseFireStore)
                        if (modelToPresent != null) {
                            modelToPresent.isNew = false
                            Either.Right(LoginViewModel.LoginState.Success(modelToPresent))
                        }else {
                            Either.Left(GetAuthFailure.FireStoreSignIn())
                        }
                    }else {
                        Either.Left(GetAuthFailure.FireStoreSignIn())
                    }
                }
            }?:run {
                Either.Left(GetAuthFailure.FirebaseSignIn())
            }
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.FirebaseSignIn())
        }
    }


    suspend fun execFirebaseRegister(authModel: AuthModel): Either<Failure, LoginViewModel.LoginState>   {
        return try {
            val responseEntity = getAuthRepositoryImpl.emailSignInCredentials(authModel)
            if (responseEntity.codeResponse == BaseResponse.RESPONSE_OK){
                val modelToPresent = mapToPresent(responseEntity)
                Either.Right(LoginViewModel.LoginState.Success(modelToPresent!!))
            } else {
                Either.Left(GetAuthFailure.EmailSignIn())
            }
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.EmailRegister())
        }
    }

    suspend fun execFireStoreCreate(profileModel: ProfileModel): Either<Failure, LoginViewModel.LoginState>  {
        return try {
            val modelToPresent = getAuthRepositoryImpl.createUserInFirestoreIfNotExists(profileModel)
            Either.Right(LoginViewModel.LoginState.FireStoreSuccess(modelToPresent))
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.FireStoreSignIn())
        }
    }

    suspend fun execFacebookSignInWithCredentials(token: AccessToken): Either<Failure, LoginViewModel.LoginState> {
        return try {
            val responseEntity = getAuthRepositoryImpl.facebookSignInWithCredentials(token)
            responseEntity?.let {
                val firebaseUser = firebaseAuth.currentUser
                val modelToPresent = mapToPresent(firebaseUser, it)
                Either.Right(LoginViewModel.LoginState.Success(modelToPresent!!))
            }?:run {
                Either.Left(GetAuthFailure.FacebookSignIn())
            }
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.FacebookSignIn())
        }
    }

    suspend fun execSignInWithCredentials(authModel: AuthModel): Either<Failure, LoginViewModel.LoginState> {
        return try {
            val firebaseUser = getAuthRepositoryImpl.emailAndPassSignInCredentials(authModel)
            val responseEntity = getAuthRepositoryImpl.addUserToLiveData(firebaseUser.uid)
            val dtoEntity = getAuthRepositoryImpl.getProfileById(firebaseUser.uid)

            when {
                dtoEntity != null -> {
                    val objectToPresent = mapToPresent(dtoEntity)
                    Either.Right(LoginViewModel.LoginState.Success(objectToPresent!!))
                }
                responseEntity.codeResponse == BaseResponse.RESPONSE_OK -> {
                    val objectToPresent = mapToPresent(responseEntity)
                    Either.Right(LoginViewModel.LoginState.Success(objectToPresent!!))
                }
                else -> {
                    Either.Left(GetAuthFailure.EmailSignIn())
                }
            }
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.EmailSignIn())
        }
    }

    suspend fun execDatabaseOperation(profileModel: ProfileModel) {
        getAuthRepositoryImpl.insertUserInRoom(mapToRequest(profileModel), profileModel)
    }

    suspend fun execSignOutOperation(uid: String) {
        getAuthRepositoryImpl.deleteProfileInDB(uid)
    }

    private fun mapToPresent(responseEntity: ProfileResponse): ProfileModel? {
        return ProfileModel(responseEntity)
    }

    private fun mapToPresent(profile: Profile): ProfileModel? {
        return ProfileModel(profile)
    }

    private fun mapToPresentation(profile: Profile): ProfileModel {
        return ProfileModel(profile)
    }

    private fun mapToPresentation(profileResponse: ProfileResponse): ProfileModel {
        return ProfileModel(profileResponse)
    }

    private fun mapToPresent(firebaseUser: FirebaseUser?, authResult: AuthResult?): ProfileModel? {
        return ProfileModel(firebaseUser!!, authResult!!)
    }

    private fun mapToRequest(profileModel: ProfileModel): Profile {
        return Profile(profileModel.uid, profileModel.name, profileModel.email,
                profileModel.isAuthenticated, profileModel.isNew, profileModel.isCreated,
                profileModel.lastLongitude, profileModel.lastLatitude, profileModel.photo)
    }

    fun execUserIsAuthenticatedInFirebase(): Either<Failure, SplashState.SuccessVerifySignIn> {
        return try {
            val responseEntity = getAuthRepositoryImpl.checkIfUserIsAuthenticatedInFirebase()
            responseEntity.let {
                if (it.codeResponse == BaseResponse.RESPONSE_OK){
                    val modelToPresent = mapToPresent(it)
                    Either.Right(SplashState.SuccessVerifySignIn(modelToPresent!!))
                } else {
                    Either.Left(GetAuthFailure.FirebaseSignIn())
                }
            }
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.FirebaseSignIn())
        }
    }

    suspend fun execAddUserToLiveData(uid: String): Either<Failure, SplashState.SuccessValidSignIn> {
        return try {
            val responseEntity = getAuthRepositoryImpl.addUserToLiveData(uid)
            val dtoEntity = getAuthRepositoryImpl.getProfileById(uid)

            when {
                dtoEntity != null -> {
                    dtoEntity.isNew = false
                    dtoEntity.isAuthenticated = true
                    dtoEntity.isCreated = true
                    val objectToPresent = mapToPresent(dtoEntity)
                    Either.Right(SplashState.SuccessValidSignIn(objectToPresent!!))
                }
                responseEntity.codeResponse == 1000 -> {
                    responseEntity.isAuthenticated = true
                    responseEntity.isCreated = true
                    responseEntity.isNew = false
                    val objectToPresent = mapToPresent(responseEntity)
                    Either.Right(SplashState.SuccessValidSignIn(objectToPresent!!))
                }
                else -> {
                    Either.Left(GetAuthFailure.FirebaseSignIn())
                }
            }
        } catch (exp: Exception) {
            Either.Left(GetAuthFailure.FirebaseSignIn())
        }
    }

    suspend fun getProfileInformation(): Either<Failure, ProfileModel> {
        return try {
            val responseEntity = getAuthRepositoryImpl.requestProfileData(firebaseAuth.uid!!)
            val dtoEntity = getAuthRepositoryImpl.getProfileById(firebaseAuth.uid!!)

            when {
                dtoEntity != null -> {
                    val objectToPresent = mapToPresentation(dtoEntity)
                    accessToken?.let {
                        if (objectToPresent.photo != null && objectToPresent.photo!!.isNotEmpty()){
                            objectToPresent.photo = objectToPresent.photo.toString().plus("?type=large&width=300&height=300").plus("&access_token=").plus(it.token)
                        }
                    }
                    Either.Right(objectToPresent)
                }
                responseEntity.codeResponse == BaseResponse.RESPONSE_OK -> {
                    val objectToPresent = mapToPresentation(responseEntity)
                    accessToken?.let {
                        if (objectToPresent.photo != null && objectToPresent.photo!!.isNotEmpty()){
                            objectToPresent.photo = objectToPresent.photo.toString().plus("?type=large&width=300&height=300").plus("&access_token=").plus(it.token)
                        }
                    }
                    Either.Right(objectToPresent)
                }
                else -> {
                    Either.Left(GetAuthFailure(Exception("Error Generico: GoToAuthScreen")))
                }
            }

        } catch (exp: Exception) {
            Either.Left(GetAuthFailure(exp))
        }
    }

}