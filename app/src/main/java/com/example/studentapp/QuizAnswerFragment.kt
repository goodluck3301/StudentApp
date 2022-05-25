package com.example.studentapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.studentapp.accountFragments.QuizPageFragment
import com.example.studentapp.databinding.FragmentQuizAnswerBinding
import com.example.studentapp.databinding.FragmentQuizPageBinding
import com.example.studentapp.models.Questions


class QuizAnswerFragment : Fragment() {

    private lateinit var binding: FragmentQuizAnswerBinding
    private lateinit var list: MutableList<Questions>
    private var mCurrentPosition: Int = 1
    private var mSelectedOptionPosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        list = com.example.studentapp.questions.Questions.list[com.example.studentapp.questions.Questions.index]
        list.shuffle()
        binding = FragmentQuizAnswerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setQuestion()

        binding.tvOptionOne.setOnClickListener {

                selectedOptionView(binding.tvOptionOne, 1)

        }
        binding.tvOptionTwo.setOnClickListener {

                selectedOptionView(binding.tvOptionTwo, 2)

        }
        binding.tvOptionThree.setOnClickListener {

                selectedOptionView(binding.tvOptionThree, 3)

        }
        binding.tvOptionFour.setOnClickListener {

                selectedOptionView(binding.tvOptionFour, 4)

        }
        binding.btnSubmit.setOnClickListener {
            if (mSelectedOptionPosition == 0) {
                mCurrentPosition++

                when {
                    mCurrentPosition <= list!!.size -> {
                        setQuestion()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Դուք հաջողությամբ ավարտել եք թեստը", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                val question = list?.get(mCurrentPosition - 1)
                if (question!!.correctOption != mSelectedOptionPosition) {
                    answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                }
                answerView(question.correctOption, R.drawable.correct_option_border_bg)
                if (mCurrentPosition == list!!.size) {
                    binding.btnSubmit.text = "Ավարտել"
                    val fragment = QuizPageFragment()
                    fragmentManager?.beginTransaction()?.apply {
                        replace(R.id.fragmentContainerView2, fragment)
                        isAddToBackStackAllowed
                        commit()
                    }

                } else {
                    binding.btnSubmit.text = "Հոջորդը"
                }
                mSelectedOptionPosition = 0
            }
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setQuestion() {

        val question = list!!.get(mCurrentPosition - 1)

        defaultOptionsView()
        if (mCurrentPosition == list!!.size) {
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
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
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
                    ContextCompat.getDrawable(
                        it, drawableView
                    )
                }
            }
            2 -> {
                binding.tvOptionTwo.background = context?.let {
                    ContextCompat.getDrawable(
                        it, drawableView
                    )
                }
            }
            3 -> {
                binding.tvOptionThree.background = context?.let {
                    ContextCompat.getDrawable(
                        it, drawableView
                    )
                }
            }
            4 -> {
                binding.tvOptionFour.background = context?.let {
                    ContextCompat.getDrawable(
                        it, drawableView
                    )
                }
            }
        }
    }//answerView()


}