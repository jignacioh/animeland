package com.clearmind.animeland.model.dao

import androidx.room.*
import com.clearmind.animeland.model.User
import com.clearmind.animeland.model.auth.Profile

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg profiles: Profile)

    @Query("UPDATE Profile SET last_longitude=:longitude AND last_latitude=:latitude WHERE uid = :uid")
    fun update(latitude : Double?, longitude : Double?,uid : String)

    @Query("SELECT * FROM Profile as p WHERE p.uid = :id")
    fun getById(id: String): Profile

    @Query("DELETE FROM Profile WHERE uid = :uid ")
    fun deleteById(uid: String): Int


    @Transaction
    fun upsert(obj: Profile) {
        val id = insert(obj)
        if (id.equals(-1)) {
            update(obj.lastLatitude,obj.lastLongitude,obj.uid)
        }
    }
}
