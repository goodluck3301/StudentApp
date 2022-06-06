package com.example.gavarstateuniversityapp.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentapp.GeneralFunctions.getMaterialsGeneral
import com.example.studentapp.R
import com.example.studentapp.database.MaterialDatabase
import com.example.studentapp.database.MaterialsData
import com.example.studentapp.database.questionsdb.QuestionsData
import com.example.studentapp.database.questionsdb.QuestionsDatabase
import com.example.studentapp.questions.Data
import com.example.studentapp.questions.Data.TopDataList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GetStarted : Fragment() {

    private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var localDb: MaterialDatabase
    private lateinit var questionsDb: QuestionsDatabase

    @NonNull
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        localDb = context?.let { MaterialDatabase.getDatabase(it) }!!
        questionsDb = context?.let { QuestionsDatabase.getDatabase(it) }!!

        getQuestions()
        getMaterials()
        getMaterialsGeneral(requireContext())
        return inflater.inflate(R.layout.fragment_get_started, container, false)
    }

    @NonNull
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        firebaseAuth = FirebaseAuth.getInstance()

        val welcome = view.findViewById<TextView>(R.id.textView)
        val image = view.findViewById<ImageView>(R.id.imageView)

        val imageAn = ObjectAnimator
            .ofFloat(image, "translationY", -180f, 0f) //150f,-80f

        imageAn.interpolator = AnticipateOvershootInterpolator()
        imageAn.repeatCount = 0
        imageAn.duration = 5000

        val welcome1 = ObjectAnimator
            .ofFloat(welcome, "translationY", 180f, 0f) //150f,-80f

        welcome1.interpolator = AnticipateOvershootInterpolator()
        welcome1.repeatCount = 0
        welcome1.duration = 5000

        welcome1.start()
        imageAn.start()

    }//onViewCreated()

    @NonNull
    override fun onResume() {
        super.onResume()

        getQuestions()
        getMaterials()

        CoroutineScope(Dispatchers.IO).launch {
            TopDataList = (
                    localDb
                        .materialDao()
                        .getAll()
                    ).toMutableList()
        }
        if (firebaseAuth.currentUser != null) {
            activityScope.launch {
                getMaterials()
                delay(5000)
                findNavController()
                    .navigate(
                        GetStartedDirections
                            .actionGetStartedToGeneralFragment()
                    )
            }
        } else {
            activityScope.launch {
                getMaterials()
                delay(5000)
                findNavController()
                    .navigate(
                        GetStartedDirections
                            .actionGetStartedToSignUpFragment()
                    )
            }
        }//else
    }//

    @NonNull
    private fun getMaterials() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("materials")
                .get()
                .addOnSuccessListener { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in result) {
                            if ((localDb.materialDao()
                                    .isNotExists(document.get("materialTitle").toString()))
                            ) {
                                localDb.materialDao().insertData(
                                    MaterialsData(
                                        document.get("materialImgURI").toString(),
                                        document.get("materialURL").toString(),
                                        document.get("materialTitle").toString(),
                                        document.get("materialDesc").toString(),
                                        document.get("id").toString().toInt()
                                    )
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }
    }// getMaterials()*/

    @NonNull
    private fun getQuestions() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("questions")
                .get()
                .addOnSuccessListener { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in result) {
                            if ((questionsDb.questionDao()
                                    .isNotExists(document.get("question").toString()))
                            ) {
                                questionsDb.questionDao().insertData(
                                    QuestionsData(
                                        document.get("id").toString(),
                                        document.get("question").toString(),
                                        document.get("optionOne").toString(),
                                        document.get("optionTwo").toString(),
                                        document.get("optionThree").toString(),
                                        document.get("optionFour").toString(),
                                        document.get("correctOption").toString(),
                                    )
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }
    }


}

