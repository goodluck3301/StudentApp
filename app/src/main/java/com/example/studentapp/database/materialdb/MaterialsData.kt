package com.example.studentapp.database.materialdbimport androidx.room.ColumnInfoimport androidx.room.Entityimport androidx.room.PrimaryKey@Entity(tableName = "materials")class MaterialsData(    @ColumnInfo(name = "materialImgURI") val materialImgURI: String?,    @ColumnInfo(name = "materialURL") val materialURL: String?,    @ColumnInfo(name = "materialTitle") val materialTitle: String?,    @ColumnInfo(name = "materialDesc") val materialDesc: String?,    @ColumnInfo(name = "materialid") val materialid: Int,    @ColumnInfo(name = "userID") val userID: String?,) {    @PrimaryKey(autoGenerate = true)    var id: Int? = null    override fun equals(other: Any?): Boolean {        return super.equals(other)    }}