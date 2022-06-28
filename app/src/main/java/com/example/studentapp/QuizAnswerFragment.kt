package com.example.studentapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.studentapp.accountFragments.QuizPageFragment
import com.example.studentapp.databinding.FragmentQuizAnswerBinding
import com.example.studentapp.models.Questions
import com.example.studentapp.questions.Data.qListFromDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class QuizAnswerFragment : Fragment() {

    private lateinit var binding: FragmentQuizAnswerBinding
    private var list = mutableListOf<Questions>()
    private var mCurrentPosition: Int = 1
    private var mSelectedOptionPosition: Int = 0
    private var score: Int = 0
    private var getScore by Delegates.notNull<Int>()
    private lateinit var showScore: View
    private lateinit var docId: String
    private var check = true
    private var checkSubmit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        CoroutineScope(Dispatchers.IO).launch {
            readDataFirestore()
        }
        qListFromDatabase.shuffle()
        qListFromDatabase.forEachIndexed { i, e ->
            if (i <= 9)
                list.add(e)
        }

        list.shuffle()
        binding = FragmentQuizAnswerBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setQuestion()

        binding.tvOptionOne.setOnClickListener {
            if (check)
                selectedOptionView(binding.tvOptionOne, 1)
        }

        binding.tvOptionTwo.setOnClickListener {
            if (check)
                selectedOptionView(binding.tvOptionTwo, 2)
        }

        binding.tvOptionThree.setOnClickListener {
            if (check)
                selectedOptionView(binding.tvOptionThree, 3)
        }

        binding.tvOptionFour.setOnClickListener {
            if (check)
                selectedOptionView(binding.tvOptionFour, 4)
        }

        binding.btnSubmit.setOnClickListener {
            if (checkSubmit) {
                check = !check
                val scoreShowText = "Դուք հավաքել եք $score միավոր 10-ից"

                if (mCurrentPosition == 10) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val hashMap = hashMapOf<String, Any>()
                        hashMap["score"] = (score + getScore).toString()
                        updateUserInfo(hashMap)
                    }
                    showScore =
                        LayoutInflater
                            .from(context)
                            .inflate(R.layout.score_view, null)
                    var message = ""
                    when (score) {
                        in 0..4 -> message += "$scoreShowText\n Վատ արդյունք"
                        in 5..7 -> message += "$scoreShowText\n Միջին արդյունք"
                        in 8..9 -> message += "$scoreShowText\n Լավ արդյունք"
                        10 -> message += "$scoreShowText\n Գերազանց արդյունք"
                    }

                    val mBuilder = AlertDialog.Builder(context)
                        .setView(showScore)
                        .setTitle("Ամփոփում")
                        .setMessage(message)
                        .setIcon(R.drawable.signvec)
                    val mAlertDialog = mBuilder.show()

                    mAlertDialog.findViewById<Button>(R.id.okayBtn).setOnClickListener {
                        mAlertDialog.dismiss()
                    }
                }
                if (mSelectedOptionPosition == 0) {
                    mCurrentPosition++

                    when {
                        mCurrentPosition <= list.size -> {
                            setQuestion()
                            checkSubmit = false
                        }
                        else -> {
                            Toast.makeText(
                                context,
                                "Դուք ավարտել եք թեստը", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {

                    val question = list[mCurrentPosition - 1]
                    if (question.correctOption != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                    } else {
                        score++
                    }
                    answerView(question.correctOption, R.drawable.correct_option_border_bg)
                    if (mCurrentPosition == list.size) {
                        binding.btnSubmit.text = "Ավարտել"
                        val fragment = QuizPageFragment()
                        fragmentManager?.beginTransaction()?.apply {
                            replace(R.id.fragmentContainerView2, fragment)
                            isAddToBackStackAllowed
                            commit()
                        }
                    } else {
                        binding.btnSubmit.text = "Հաջորդը"
                    }
                    mSelectedOptionPosition = 0
                }
            } else {
                Toast.makeText(context, "Ընտրեք որևէ պատասխան.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        try {
            val question = list[mCurrentPosition - 1]

            if (question.optionThree == null) {
                binding.tvOptionThree.visibility = View.GONE
            }else
                binding.tvOptionThree.visibility = View.VISIBLE
            if (question.optionFour == null) {
                binding.tvOptionFour.visibility = View.GONE
            }else
                binding.tvOptionFour.visibility = View.VISIBLE

            defaultOptionsView()
            if (mCurrentPosition == list.size) {
                binding.btnSubmit.text = "Ավարտել"
            } else {
                binding.btnSubmit.text = "Հաստատել"
            }

            binding.progressBar.progress = mCurrentPosition
            binding.tvProgress.text = "$mCurrentPosition" + "/" + binding.progressBar.max

            binding.tvQuestion.text = question.question
            binding.tvOptionOne.text = question.optionOne
            binding.tvOptionTwo.text = question.optionTwo
            binding.tvOptionThree.text = question.optionThree
            binding.tvOptionFour.text = question.optionFour

        } catch (e: Exception) { }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        checkSubmit = true
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNum
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = context?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.selected_option_border_bg
            )
        }
    }

    private fun defaultOptionsView() {

        val options = ArrayList<TextView>()
        options.add(0, binding.tvOptionOne)
        options.add(1, binding.tvOptionTwo)
        options.add(2, binding.tvOptionThree)
        options.add(3, binding.tvOptionFour)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.default_option_border_bg) }
        }
    }


    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> {
                binding.tvOptionOne.background = context?.let {
                    ContextCompat.getDrawable(it, drawableView)
                }
            }
            2 -> {
                binding.tvOptionTwo.background = context?.let {
                    ContextCompat.getDrawable(it, drawableView)
                }
            }
            3 -> {
                binding.tvOptionThree.background = context?.let {
                    ContextCompat.getDrawable(it, drawableView)
                }
            }
            4 -> {
                binding.tvOptionFour.background = context?.let {
                    ContextCompat.getDrawable(it, drawableView)
                }
            }
        }
    }//answerView()

    private fun readDataFirestore() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if ((document.get("idUser")
                                .toString()) == (FirebaseAuth.getInstance().uid).toString()
                        ) {
                            docId = document.id
                            getScore = document.get("score").toString().toInt()
                        }
                    }
                }
        }
            ?.addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }// readDataFirestore()


    private fun updateUserInfo(newUserData: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            if (docId.isNotEmpty()) {
                val db = Firebase.firestore
                db.collection("users")
                    .document(docId)
                    .set(newUserData, SetOptions.merge())
            }
        }// updateUserInfo

}