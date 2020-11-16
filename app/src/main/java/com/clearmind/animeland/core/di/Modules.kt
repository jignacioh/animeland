package com.clearmind.animeland.core.di

import androidx.work.Constraints
import androidx.work.NetworkType
import com.clearmind.animeland.home.MainViewModel
import com.clearmind.animeland.login.LoginViewModel
import com.clearmind.animeland.register.RegisterViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


val appModule = module {


    // Executor
    //single { CoroutinesExecutor() }

    // Repository
    //single { TasksRepositoryImpl() }

    // Use Case
    //single { TasksUseCaseImpl(getTasksRepositoryImpl = get(),getCoroutinesExecutor = get()) }

    // ViewModel
    //viewModel{ UiViewModel(get()) }
    viewModel { LoginViewModel() }
    viewModel { RegisterViewModel() }
    viewModel { MainViewModel() }


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

