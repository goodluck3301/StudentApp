package com.example.studentapp.questionsimport android.content.Contextimport com.example.studentapp.Rimport com.example.studentapp.database.MaterialsDataimport com.example.studentapp.models.MaterialModelimport com.example.studentapp.models.Questionsimport com.example.studentapp.models.QuestionsTypeModelobject Data {    var questionsTypeList = mutableListOf<QuestionsTypeModel>(        QuestionsTypeModel(0, "Աշխարհագրություն", R.drawable.geography),        QuestionsTypeModel(1, "Քիմիա", R.drawable.chemistry),        QuestionsTypeModel(2, "Ֆիզիկա", R.drawable.physics),        QuestionsTypeModel(3, "Մաթեմատիկա", R.drawable.mathematic),        QuestionsTypeModel(4, "Հայոց Պատմություն", R.drawable.history),        QuestionsTypeModel(5, "Ինֆորմատիկա", R.drawable.infromatic),    )    lateinit var qListFromDatabase:MutableList<Questions>    var TopDataList = mutableListOf<MaterialsData>()    var index = 0  var newMaterial = mutableListOf<MaterialsData>()    var contextFragment: Context? = null}