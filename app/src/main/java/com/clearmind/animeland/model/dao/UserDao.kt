package com.clearmind.animeland.model.dao

import androidx.room.*
import com.clearmind.animeland.model.User


@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)


    @Query("SELECT * from user WHERE uid= :uid")
    fun getItemById(uid:Int):User

    @Query("UPDATE user SET last_longitude=:longitude AND last_latitude=:latitude WHERE uid = :uid")
    fun update(latitude : Double?, longitude : Double?,uid : Int)

    @Transaction
    fun upsert(obj: User) {
        val id = insertAll(obj)
        if (id.equals(-1)) {
            update(obj.lastLatitude,obj.lastLongitude,obj.uid)
        }
    }

}
