package com.example.gavarstateuniversityapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.studentapp.R
import kotlinx.coroutines.*



class GetStarted : Fragment() {

    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_started, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { }//onViewCreated()

    override fun onResume() {
        super.onResume()
        activityScope.launch {
            delay(4000)
            findNavController()
                .navigate(
                    GetStartedDirections
                        .actionGetStartedToSignUpFragment()
                )
        }
    }

}