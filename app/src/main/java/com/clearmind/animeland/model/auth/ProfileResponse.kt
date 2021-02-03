package com.clearmind.animeland.model.auth

import com.clearmind.animeland.core.base.BaseResponse
import com.clearmind.animeland.extensions.empty

data class ProfileResponse(var uid: String = "",
                           var name: String? = null,
                           var email: String? = null,

                           var isAuthenticated: Boolean = false,
                           var isNew: Boolean = false,
                           var isCreated: Boolean = false,

                           var lastLongitude: Double? = 0.0,
                           var lastLatitude: Double? = 0.0,
                           var photo: String? = null): BaseResponse() {

    companion object{
        val empty = ProfileResponse(String.empty(), String.empty(), String.empty(), false, false, false, 0.0, 0.0, String.empty(), BaseResponse.RESPONSE_OK, String.empty())
        val error = ProfileResponse(String.empty(), String.empty(), String.empty(), false, false, false, 0.0, 0.0, String.empty(), BaseResponse.RESPONSE_KO, String.empty())

    }

    constructor(uid: String, name: String?, email: String?,
                isAuthenticated: Boolean, isNew: Boolean, isCreated: Boolean,
                lastLongitude: Double?, lastLatitude: Double?, photo: String?,
                codeResponse: Int, messageResponse: String): this(codeResponse = codeResponse, messageResponse = messageResponse) {

        this.uid = uid
        this.name = name
        this.email = email
        this.isAuthenticated = isAuthenticated
        this.isNew = isNew
        this.isCreated = isCreated
        this.lastLongitude = lastLongitude
        this.lastLatitude = lastLatitude
        this.photo = photo
    }

    constructor(codeResponse: Int, messageResponse: String): this(){
        this.codeResponse = codeResponse
        this.messageResponse = messageResponse
    }
}