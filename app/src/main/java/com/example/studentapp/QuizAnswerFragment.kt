package com.example.studentapp

import android.os.Bundle
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.studentapp.databinding.FragmentQuizAnswerBinding
import com.example.studentapp.databinding.FragmentQuizPageBinding
import com.example.studentapp.models.Questions


class QuizAnswerFragment : Fragment() {

    private lateinit var binding: FragmentQuizAnswerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQuizAnswerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(context, "Quiz page", Toast.LENGTH_LONG).show()
    }


}