package com.example.gavarstateuniversityapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gavarstateuniversityapp.viewmodel.AuthViewModel
import com.example.studentapp.R
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {

    private lateinit var email:EditText
    private lateinit var pass :EditText
    private lateinit var pass2:EditText
    private lateinit var ok   :Button
    private lateinit var viewModel: AuthViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }// onCreate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = view.findViewById(R.id.emailS)
        pass  = view.findViewById(R.id.passwdS)
        pass2 = view.findViewById(R.id.repeatPaswdS)
        ok    = view.findViewById(R.id.okS)
        val toLoginPage = view.findViewById<TextView>(R.id.toLoginPage)


        toLoginPage.setOnClickListener{
            findNavController()
                .navigate(
                    SignUpFragmentDirections
                        .actionSignUpFragmentToLogInFragment()
                )
        }



        ok.setOnClickListener {

            if (email.text.isNotEmpty() && pass.text.toString() == pass2.text.toString()) {
                firebaseAuth
                    .createUserWithEmailAndPassword(email.text.toString(),pass.text.toString())
                    .addOnCompleteListener{
                        if (it.isSuccessful) {
                            Toast.makeText(
                                context,
                                "send",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                it.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                findNavController()
                    .navigate(
                        SignUpFragmentDirections
                            .actionSignUpFragmentToHomeFragment()
                    )
            }else {
            Toast.makeText(
                context,
                "Error",
                Toast.LENGTH_SHORT
            ).show()
            }

        }//btn


    }

}


