package com.example.studentapp.databaseimport androidx.room.Daoimport androidx.room.Deleteimport androidx.room.Insertimport androidx.room.Query@Daointerface MaterialsDao {    @Query("SELECT * FROM materials ORDER BY materialid")    fun getAll(): List<MaterialsData>    @Query("SELECT NOT EXISTS(SELECT * FROM materials WHERE materialTitle = :materialTitle )")    fun isNotExists(materialTitle:String?):Boolean    @Insert    fun insertData(arts: MaterialsData?)    @Delete    fun delete(art: MaterialsData)}