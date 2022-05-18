package com.example.gavarstateuniversityapp.fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GetStarted : Fragment() {

    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_started, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val welcome = view.findViewById<TextView>(R.id.textView)
        val image   = view.findViewById<ImageView>(R.id.imageView)


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

    override fun onResume() {
        super.onResume()
            activityScope.launch {
                delay(4500)
                findNavController()
                    .navigate(
                        GetStartedDirections
                            .actionGetStartedToSignUpFragment()
                    )
            }
        }
}

