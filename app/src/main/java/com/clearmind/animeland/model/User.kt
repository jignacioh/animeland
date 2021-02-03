package com.clearmind.animeland.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "user")
data class User ( @ColumnInfo(name = "user_name") var username: String?="",
                  @ColumnInfo(name = "user_mail") var usermail: String?="",
                    @ColumnInfo(name = "last_longitude") var lastLongitude: Double?=0.0,
                    @ColumnInfo(name = "last_latitude") var lastLatitude: Double?=0.0) {
    @PrimaryKey var uid: Int = 0

}