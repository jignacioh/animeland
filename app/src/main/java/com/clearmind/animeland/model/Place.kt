package com.clearmind.animeland.model

import com.google.firebase.database.PropertyName

class Place {
    @PropertyName("Id")
    var Id:Int?=null
    @PropertyName("Description")
    var Description:String?=null
    var photos : ArrayList<String> = arrayListOf()

    constructor():this(0,"", arrayListOf()) {

    }

    constructor(Id: Int?, Description: String?,photos : ArrayList<String>) {
        this.Description = Description
        this.Id = Id
        this.photos=photos
    }
}