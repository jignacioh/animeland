package com.clearmind.animeland.core.di

import androidx.work.Constraints
import androidx.work.NetworkType
import com.clearmind.animeland.core.executor.CoroutinesExecutor
import com.clearmind.animeland.core.platform.NetworkHandler
import com.clearmind.animeland.home.HomeViewModel
import com.clearmind.animeland.home.MainViewModel
import com.clearmind.animeland.updateprofile.UpdateUserViewModel
import com.clearmind.animeland.usecase.AuthUseCase
import com.clearmind.animeland.repository.AuthRepositoryImpl
import com.clearmind.animeland.login.LoginViewModel
import com.clearmind.animeland.register.RegisterViewModel
import com.clearmind.animeland.setting.SettingViewModel
import com.clearmind.animeland.splash.SplashViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


val appModule = module {

    //Singles
    single { NetworkHandler(androidContext()) }

    //Database
    single { AppDatabase(get()) }

    // Executor
    single { CoroutinesExecutor() }

    // Repository
    single { AuthRepositoryImpl(networkHandler = get() , profileDao = get<AppDatabase>().profileDao()) }

    // Use Case
    //single { TasksUseCaseImpl(getTasksRepositoryImpl = get(),getCoroutinesExecutor = get()) }
    single { AuthUseCase(getAuthRepositoryImpl = get(), getCoroutinesExecutor = get()) }

    // ViewModel
    viewModel { SplashViewModel(getAuthUseCase = get()) }
    viewModel { LoginViewModel(getAuthUseCase = get()) }
    viewModel { RegisterViewModel(getAuthUseCase = get()) }
    viewModel { HomeViewModel(getAuthUseCase = get()) }
    viewModel { MainViewModel() }
    viewModel { SettingViewModel(getAuthUseCase = get()) }
    viewModel { UpdateUserViewModel() }
    //single<UiViewModel.HelloRepository> { UiViewModel.HelloRepositoryImpl() }
}

val appModules = listOf(appModule)


/*val providers = arrayListOf(
    AuthUI.IdpConfig.GoogleBuilder().build()
)*/


object RetrofitFactory {
    const val BASE_URL = "http://192.168.8.105"

    fun makeRetrofitService(): RetrofitService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(RetrofitService::class.java)
    }

}

val constraints: Constraints = Constraints.Builder()
    .setRequiresCharging(true)
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

