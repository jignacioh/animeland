package com.clearmind.animeland.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "user")
data class User ( @ColumnInfo(name = "user_name") var username:String?="",
                  @ColumnInfo(name = "user_mail") var usermail:String?="",
                    @ColumnInfo(name = "last_longitude") var lastLongitude:Double?=0.0,
                    @ColumnInfo(name = "last_latitude") var lastLatitude:Double?=0.0) {
    @PrimaryKey var uid: Int = 0

   /* @ColumnInfo(name = "user_name") var username:String?=null
    @ColumnInfo(name = "user_mail") var usermail:String?=null
    @ColumnInfo(name = "last_longitude") var lastLongitude:Double? = null
    @ColumnInfo(name = "last_latitude") var lastLatitude:Double? =null
*/
    /*
    constructor(username: String?, usermail: String?) {
        this.username = username
        this.usermail = usermail
    }
    constructor(username: String?, usermail: String?, lastLongitude: Double?, lastLatitude: Double?) {
        this.username = username
        this.usermail = usermail
        this.lastLongitude = lastLongitude
        this.lastLatitude = lastLatitude
    }*/

}