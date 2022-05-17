package com.example.gavarstateuniversityapp.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignUpFragment : Fragment() {

    private lateinit var name:EditText
    private lateinit var email:EditText
    private lateinit var pass :EditText
    private lateinit var pass2:EditText
    private lateinit var ok   :Button
    private lateinit var viewModel: AuthViewModel
    private lateinit var firebaseAuth: FirebaseAuth


    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    fun checkEmail(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

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

        name  = view.findViewById(R.id.name)
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


        val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})";
        fun isEmailValid(email: String): Boolean {
            return EMAIL_REGEX.toRegex().matches(email);
        }


        fun createNewUser() {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val db = Firebase.firestore

            val userDate = hashMapOf(
                "name"  to name.text.toString(),
                "score" to 0,
            )

            db.collection("users").document(firebaseUser!!.uid)
                .set(userDate)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

        }//createNewUser


        ok.setOnClickListener {

            var emailBool = false
            var passBool  = false
            var nameBool      = false

            if (isEmailValid(email.text.toString()))
                emailBool = true
            else
                email.error = "No correct mail address"
            if (passValid(pass))
                passBool = true
            if (name.text.toString().isNotEmpty())
                nameBool = true
            else
                name.error = "Text area is empty!"


            if(emailBool && passBool && nameBool) {
                if (email.text.isNotEmpty() && pass.text.toString() == pass2.text.toString()) {
                    firebaseAuth
                        .createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText( context, "send", Toast.LENGTH_SHORT )
                                    .show()
                            } else {
                                Toast.makeText( context, it.exception.toString(), Toast.LENGTH_SHORT )
                                    .show()
                            }
                        }
                    createNewUser()
                    findNavController()
                        .navigate(
                            SignUpFragmentDirections
                                .actionSignUpFragmentToHomeFragment()
                        )

                } else { Toast.makeText( context, "Error", Toast.LENGTH_SHORT).show() }
            }

        }//btn


    }
}


fun passValid(pass:EditText):Boolean{
    val errorText = when {
        /* Rule 1 */
        !pass.text.contains(Regex("[A-Z]")) -> {
            pass.error = "Password must contain one capital letter"
            false
        }
        /* Rule 2 */
        !pass.text.contains(Regex("[0-9]")) -> {
            pass.error = "Password must contain one digit"
            false
        }
        /* Rule 3, not counting space as special character */
        !pass.text.contains(Regex("[^a-zA-Z0-9 ]")) -> {
            pass.error = "Password must contain one special character"
            false
        }
        else -> true
    }
    return errorText
}
