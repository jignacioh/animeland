package com.clearmind.animeland.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.clearmind.animeland.CoroutineTestRule
import com.clearmind.animeland.LifeCycleTestOwner
import com.clearmind.animeland.MainCoroutineScopeRule
import com.clearmind.animeland.TestCoroutineRule
import com.clearmind.animeland.core.di.Either
import com.clearmind.animeland.model.auth.ProfileModel
import com.clearmind.animeland.usecase.AuthUseCase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    private lateinit var loginViewModel: LoginViewModel

    @Mock
    private lateinit var getAuthUseCase: AuthUseCase

    @Mock
    private lateinit var mAuth: FirebaseAuth

    @Mock
    private lateinit var authCredential: AuthCredential

    @Mock
    private lateinit var authObserver: Observer<LoginViewModel.LoginState>

    @Captor
    private lateinit var captor: ArgumentCaptor<LoginViewModel.LoginState>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        //MockitoAnnotations.initMocks(this) // required for the "@Mock" annotation
        loginViewModel = LoginViewModel(getAuthUseCase)
    }

    @Test
    fun `when calling for authentication then return loading`() {
        testCoroutineRule.runBlockingTest {
            loginViewModel.authenticatedUserLiveData.observeForever(authObserver)
            loginViewModel.signInWithMultiplesCredentials(authCredential)
            verify(authObserver).onChanged(LoginViewModel.LoginState.LOADING)
        }
    }


    @Test
    fun `when response results ok then return an profile object successfully`() {
        val model = ProfileModel("abc@x.y")
        testCoroutineRule.runBlockingTest {
            loginViewModel.authenticatedUserLiveData.observeForever(authObserver)
            whenever(getAuthUseCase.execFirebaseSignIn(authCredential)).thenAnswer{
                Either.Right(LoginViewModel.LoginState.Success(model))
            }
            loginViewModel.signInWithMultiplesCredentials(authCredential)
            assertNotNull(loginViewModel.getLiveProfile().value)
            assertEquals(LoginViewModel.LoginState.Success(model), loginViewModel.getLiveProfile().value)
        }
    }

    @Test
    fun `when response results fails then return an normal error`() {
        val exception = AuthUseCase.GetAuthFailure.FirebaseSignIn()
        testCoroutineRule.runBlockingTest {
            loginViewModel.authenticatedUserLiveData.observeForever(authObserver)
            whenever(getAuthUseCase.execFirebaseSignIn(authCredential)).thenAnswer {
                Either.Left(exception)
            }
            loginViewModel.signInWithMultiplesCredentials(authCredential)
            assertNotNull(loginViewModel.getLiveProfile().value)
            assertEquals(LoginViewModel.LoginState.Error(exception), loginViewModel.getLiveProfile().value)
        }
    }

    @After
    fun tearDown(){
        loginViewModel.authenticatedUserLiveData.removeObserver(authObserver)
    }
}