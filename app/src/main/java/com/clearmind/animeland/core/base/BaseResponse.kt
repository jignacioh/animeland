package com.clearmind.animeland.core.base

open class BaseResponse(open var codeResponse: Int = 1000, var messageResponse: String = "OK"){
    companion object{
        const val RESPONSE_OK :Int = 1000
        const val RESPONSE_KO :Int = 1001
    }
}