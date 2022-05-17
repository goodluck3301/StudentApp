package com.example.gavarstateuniversityapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.studentapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LogInFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toSignUp = view.findViewById<TextView>(R.id.toLoginPage)

        val email    = view.findViewById<EditText>(R.id.emailLogin)
        val pass     = view.findViewById<EditText>(R.id.passwdLogin)
        val ok       = view.findViewById<Button>  (R.id.okLogin)

/*
        if (firebaseAuth.currentUser != null) {
            readUserData(firebaseAuth.currentUser!!.uid)
        }

 */

        toSignUp.setOnClickListener {
           findNavController()
               .navigate(
                   LogInFragmentDirections
                       .actionLogInFragmentToSignUpFragment()
               )
        }//toSignUp

        fun readUserData(userId: String) {
            val db = Firebase.firestore
           db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    Log.e("TAG","success")
                }
                .addOnFailureListener { exception ->
                    Log.e("TAG", "Error getting documents $exception")
                }
        }

        ok.setOnClickListener {
            firebaseAuth
                .signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        readUserData(user!!.uid)
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        findNavController()
                            .navigate(
                                LogInFragmentDirections
                                    .actionLogInFragmentToHomeFragment()
                            )
                    }
                }
        }//Ok


    }

}