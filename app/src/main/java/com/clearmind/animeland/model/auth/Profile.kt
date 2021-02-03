package com.clearmind.animeland.model.auth

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull
import java.io.Serializable


@Entity(tableName = "Profile")
data class Profile(
        @NotNull @PrimaryKey var uid: String,
        @ColumnInfo(name = "user_name") var name: String? = null,
        @ColumnInfo(name = "user_email") var email: String? = null,

        @Exclude var isAuthenticated: Boolean = false,

        @Exclude var isNew: Boolean = false,

        @Exclude var isCreated: Boolean = false,

        @ColumnInfo(name = "last_longitude") var lastLongitude: Double? = 0.0,
        @ColumnInfo(name = "last_latitude") var lastLatitude: Double? = 0.0,
        @ColumnInfo(name = "photo") var photo: String? = null) : Parcelable {


    constructor(parcel: Parcel): this(
        parcel.readString().toString(),
        name = parcel.readString(),
        email = parcel.readString(),
        isAuthenticated = parcel.readByte() != 0.toByte(),
        isNew = parcel.readByte() != 0.toByte(),
        isCreated = parcel.readByte() != 0.toByte(),
        lastLongitude = parcel.readDouble(),
        lastLatitude = parcel.readDouble(),
        photo = parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeByte(if (isAuthenticated) 1 else 0)
        parcel.writeByte(if (isNew) 1 else 0)
        parcel.writeByte(if (isCreated) 1 else 0)
        parcel.writeDouble(lastLatitude!!)
        parcel.writeDouble(lastLongitude!!)
        parcel.writeString(photo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }


}