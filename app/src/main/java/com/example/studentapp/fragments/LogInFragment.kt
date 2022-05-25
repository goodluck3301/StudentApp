package com.example.gavarstateuniversityapp.fragments

import android.annotation.SuppressLint
import android.media.Image
import android.opengl.Visibility
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.studentapp.GeneralFunctions
import com.example.studentapp.R
import com.example.studentapp.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LogInFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding:FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        binding = FragmentLogInBinding.inflate(inflater)
        return binding.root
    //return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toSignUp = view.findViewById<TextView>(R.id.toSignUp)
        val showLogin = view.findViewById<ImageView>(R.id.showLogin)
        val email     = view.findViewById<EditText>(R.id.emailLogin)
        val pass      = view.findViewById<EditText>(R.id.passwdLogin)
        val ok        = view.findViewById<Button>  (R.id.okLogin)


        toSignUp.setOnClickListener {
           findNavController()
               .navigate(
                   LogInFragmentDirections
                       .actionLogInFragmentToSignUpFragment()
               )
        }//toSignUp

        ok.setOnClickListener {
            textIsNotEmpty(email,pass)

            var internet  = false
            if (context?.let { it1 -> GeneralFunctions.checkForInternet(it1) } == true) {
                internet = true
            } else
                Toast.makeText(
                    context,
                    "Համացանցը չի գտնվել :(",
                    Toast.LENGTH_LONG
                ).show()

            if (internet && email.text.isNotEmpty() && pass.text.isNotEmpty()) {
                firebaseAuth
                    .signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            binding.progressLogin.visibility = View.VISIBLE
                            val user = firebaseAuth.currentUser
                            readUserData(user!!.uid)
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                            findNavController()
                                .navigate(
                                    LogInFragmentDirections
                                        .actionLogInFragmentToGeneralFragment()
                                )
                        }else { binding.progressLogin.visibility = View.GONE }
                    }
                binding.progressLogin.visibility = View.GONE
            }
        }// btn Ok
        
        var showHideBool = false
        showLogin.setOnClickListener {
            if (!showHideBool) {
                showHideBool = true
                pass.inputType = InputType.TYPE_CLASS_TEXT
                showLogin.setImageDrawable(resources.getDrawable(R.drawable.ic_focused_visibility_off_24))
            }else {
                showHideBool = false
                pass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showLogin.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_24))
            }
        }//show/hide password
    } //onViewCreated

    private fun readUserData(userId: String) {
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

    private fun textIsNotEmpty(email:EditText, pass:EditText) {
        if (email.text.isEmpty())
            email.error = "Text area is Empty."
        if (pass.text.isEmpty())
            pass.error  = "Text area is Empty."
    }

}

