package com.example.studentapp.accountFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentapp.QuizAnswerFragment
import com.example.studentapp.R
import com.example.studentapp.adapter.QuizAdapter
import com.example.studentapp.database.questionsdb.QuestionsData
import com.example.studentapp.database.questionsdb.QuestionsDatabase
import com.example.studentapp.databinding.FragmentQuizPageBinding
import com.example.studentapp.questions.Questions
import com.example.studentapp.questions.Questions.qListFromDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.lang.Exception

class QuizPageFragment : Fragment() {

    private lateinit var binding: FragmentQuizPageBinding
    private lateinit var adapter: QuizAdapter
    private lateinit var questionsDb: QuestionsDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizPageBinding.inflate(inflater)
        questionsDb = context?.let { QuestionsDatabase.getDatabase(it) }!!
        getQuestions()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            adapter = context?.let {
                QuizAdapter(Questions.questionsTypeList) {
                  //  qListFromDatabase = Questions.emptyList
                    CoroutineScope(Dispatchers.IO).launch {
                        qListFromDatabase = questionsDb.questionDao()
                            .getItemAnswer(Questions.index.toString()) as MutableList<com.example.studentapp.models.Questions>
                    }
                    val fragment = QuizAnswerFragment()
                    fragmentManager?.beginTransaction()?.apply {
                        replace(R.id.fragmentContainerView2, fragment)
                        isAddToBackStackAllowed
                        commit()
                    }
                }
            }!!
            binding.recView.layoutManager = LinearLayoutManager(context)
            binding.recView.adapter = adapter
        }
    }

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
    }// getMaterials()

}