package com.example.studentapp.database.questionsdbimport android.content.Contextimport androidx.room.Databaseimport androidx.room.Roomimport androidx.room.RoomDatabase@Database(entities = [QuestionsData::class], version = 1)abstract class QuestionsDatabase : RoomDatabase() {    abstract fun questionDao(): QuestionsDao    companion object {        private var INSTANCE: QuestionsDatabase? = null        fun getDatabase(context: Context): QuestionsDatabase {            val tempInstance = INSTANCE            if (tempInstance != null) {                return tempInstance            }            synchronized(this) {                val instance = Room.databaseBuilder(                    context.applicationContext,                    QuestionsDatabase::class.java,                    "questions_database"                ).build()                INSTANCE = instance                return instance            }        }    }}