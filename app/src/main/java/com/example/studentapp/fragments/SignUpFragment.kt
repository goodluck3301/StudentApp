package com.example.gavarstateuniversityapp.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentapp.GeneralFunctions
import com.example.studentapp.GeneralFunctions.checkForInternet
import com.example.studentapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class SignUpFragment : Fragment() {

    private lateinit var show1:ImageView
    private lateinit var show2:ImageView

    private lateinit var name:EditText
    private lateinit var email:EditText
    private lateinit var pass :EditText
    private lateinit var pass2:EditText
    private lateinit var ok   :Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        show1 = view.findViewById(R.id.show)
        show2 = view.findViewById(R.id.show2)

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





        //asetsstudio

        ok.setOnClickListener {

            var internet  = false
            var emailBool = false
            var passBool  = false
            var nameBool  = false

            if (context?.let { it1 -> checkForInternet(it1) } == true) {
                internet = true
            } else
                Toast.makeText(
                    context,
                    "Համացանցը չի գտնվել :(",
                    Toast.LENGTH_LONG
                ).show()

            if (isEmailValid(email.text.toString()))
                emailBool = true
            else
                email.error = "No correct mail address"
            if (passValid(pass))
                passBool = true
            else {
                show1.visibility = View.INVISIBLE
            }
            if (name.text.toString().isNotEmpty())
                nameBool = true
            else
                name.error = "Text area is empty!"


            if(emailBool && passBool && nameBool && internet) {
                if (email.text.isNotEmpty() && pass.text.toString() == pass2.text.toString()) {
                    firebaseAuth
                        .createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText( context, "send", Toast.LENGTH_SHORT )
                                    .show()
                                createNewUser()
                                findNavController()
                                    .navigate(
                                        SignUpFragmentDirections
                                            .actionSignUpFragmentToGeneralFragment()
                                    )
                            } else {
                                Toast.makeText( context, it.exception.toString(), Toast.LENGTH_SHORT )
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }//btn

        var showHideBool1 = false
        show1.setOnClickListener {

            if (!showHideBool1) {
                showHideBool1 = true
                pass.inputType = InputType.TYPE_CLASS_TEXT
                show1.setImageDrawable(resources.getDrawable(R.drawable.ic_focused_visibility_off_24))
            }else {
                showHideBool1 = false
                pass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                show1.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_24))
            }
        }//show1


        var showHideBool2 = false
        show2.setOnClickListener {
            if (!showHideBool2) {
                showHideBool2 = true
                pass2.inputType = InputType.TYPE_CLASS_TEXT
                show2.setImageDrawable(resources.getDrawable(R.drawable.ic_focused_visibility_off_24))
            }else {
                showHideBool2 = false
                pass2.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                show2.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_24))
            }
        }//show2

    }

    private fun isEmailValid(email: String): Boolean {
        val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return EMAIL_REGEX.toRegex().matches(email);
    }

    private fun createNewUser() {
        val db = Firebase.firestore
        val user = hashMapOf(
            "name" to name.text.toString(),
            "email" to email.text.toString(),
            "userURLtoImage" to "https://p7.hiclipart.com/preview/652/446/341/computer-icons-login-avatar-avatar.jpg",
            "score" to 0,
            "idUser" to firebaseAuth.uid
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot written with ID: ") }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }//createNewUser

    private fun passValid(pass:EditText):Boolean{
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
    }//fun password Validation

}//class fragment


