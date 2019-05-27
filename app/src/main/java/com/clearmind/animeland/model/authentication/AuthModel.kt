package com.clearmind.animeland.model.authentication

 class AuthModel() {


    var email : String?=null
    var password : String? = null

     constructor(email: String, password: String): this(){
         this.email = email
         this.password = password
     }
}