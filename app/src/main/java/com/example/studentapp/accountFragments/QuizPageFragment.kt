package com.example.studentapp.accountFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentapp.QuizAnswerFragment
import com.example.studentapp.R
import com.example.studentapp.adapter.QuizAdapter
import com.example.studentapp.databinding.FragmentGeneralBinding
import com.example.studentapp.databinding.FragmentQuizPageBinding
import com.example.studentapp.fragments.GeneralFragment
import com.example.studentapp.fragments.GeneralFragmentDirections
import com.example.studentapp.questions.Questions


class QuizPageFragment : Fragment() {

    lateinit var binding: FragmentQuizPageBinding
    lateinit var adapter: QuizAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        adapter = QuizAdapter(Questions.questionsTypeList) {
            val fragment = QuizAnswerFragment()
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragmentContainerView2, fragment)
                isAddToBackStackAllowed
                commit()
            }

        }

        binding = FragmentQuizPageBinding.inflate(inflater)
        return binding.root
        //return inflater.inflate(R.layout.fragment_quiz_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = adapter


    }

}