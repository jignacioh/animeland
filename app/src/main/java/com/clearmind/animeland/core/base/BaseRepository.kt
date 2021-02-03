package com.clearmind.animeland.core.base

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


open class BaseRepository{

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {
        val result : Result<T> = safeApiResult(call,errorMessage)
        val data: T?
        data = when(result) {
            is Result.Success ->
                result.data
            is Result.SuccessEmpty ->
                null
            is Result.Error -> {
                Log.d("Repository", "$errorMessage y Exception - ${result.exception}")
                throw result.exception
            }
        }
        return data
    }

    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>, errorMessage: String) : Result<T> {
        val response = call.invoke()
        if(response.isSuccessful) {
            if (response.code()==204) return Result.SuccessEmpty
            return Result.Success(response.body()!!)
        }
        response.errorBody()?.string()?.let {
            if(it.isNotEmpty())
                return Result.Error(BaseException(it)) }
        return Result.Error(IOException("Error, Custom ERROR - $errorMessage"))
    }

    sealed class Result<out T: Any> {
        data class Success<out T : Any>(val data: T) : Result<T>()
        data class Error(val exception: Exception) : Result<Nothing>()
        object SuccessEmpty : Result<Nothing>()
    }
    /*
    suspend fun <T : Any> getResult(call: Call<T>): Result<T> = suspendCoroutine {
        /*val data = call.execute()?.body()

        if (data != null) {
            it.resume(Result.Success(data))
        } else {
            it.resume(Error(NullPointerException()))
        }*/
        call.enqueue(object : Callback<T> {

            override fun onFailure(call: Call<T>?, error: Throwable?) = it.resume(Result.Error(error))

            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                response?.body()?.run { it.resume(Result.Success(this)) }
                response?.errorBody()?.run { it.resume(Result.Error(HttpException(response))) }
            }
        })
    }*/


    suspend fun <T : Mappable<R>, R : Any> Call<T>.getResult(): Result<T> = suspendCoroutine {
        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>?, error: Throwable?) {
                it.resume(Result.Error(Exception("de")))
            }

            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                response?.body()?.run { it.resume(Result.Success(this)) }
                response?.errorBody()?.run { it.resume(Result.Error(HttpException(response))) }
            }
        })
    }

    interface Mappable<out T : Any> {
        fun mapToResult(): Result<T>
    }

}