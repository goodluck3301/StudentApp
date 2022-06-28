package com.example.gavarstateuniversityapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.studentapp.GeneralFunctions
import com.example.studentapp.R
import com.example.studentapp.databinding.FragmentLogInBinding
import com.example.studentapp.fragments.GeneralFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater)
        try {
            firebaseAuth = FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toSignUp.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragmentContainerView, SignUpFragment()).commit()
            }
        }//toSignUp

        binding.okLogin.setOnClickListener {
            try {
                textIsNotEmpty(binding.emailLogin, binding.passwdLogin)

                var internet = false
                if (context?.let { it1 -> GeneralFunctions.checkForInternet(it1) } == true) {
                    internet = true
                } else
                    Toast.makeText(
                        context,
                        "Համացանցը չի գտնվել :(",
                        Toast.LENGTH_LONG
                    ).show()

                if (internet && binding.emailLogin.text.isNotEmpty() && binding.passwdLogin.text.isNotEmpty()) {
                    //////
                    if (isRegistred()) {
                        firebaseAuth
                            .signInWithEmailAndPassword(
                                binding.emailLogin.text.toString(),
                                binding.passwdLogin.text.toString()
                            )
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    fragmentManager?.beginTransaction()?.apply {
                                        replace(
                                            R.id.fragmentContainerView,
                                            GeneralFragment()
                                        ).commit()
                                    }
                                } else {
                                    binding.progressLogin.visibility = View.GONE
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Սխալ էլ․ փոստ կամ գաղտնաբառ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        binding.progressLogin.visibility = View.GONE
                    } else
                        Toast
                            .makeText(
                                context,
                                "Account is not verified",
                                Toast.LENGTH_LONG
                            )
                            .show()
                }
            } catch (e: Exception) {
                Toast
                    .makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }// btn Ok

        var showHideBool = false
        binding.showLogin.setOnClickListener {
            if (!showHideBool) {
                showHideBool = true
                binding.passwdLogin.inputType = InputType.TYPE_CLASS_TEXT
                binding.showLogin.setImageDrawable(resources.getDrawable(R.drawable.ic_focused_visibility_off_24))
            } else {
                showHideBool = false
                binding.passwdLogin.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.showLogin.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_24))
            }
        }//show/hide password
    } //onViewCreated

    private fun textIsNotEmpty(email: EditText, pass: EditText) {
        if (binding.emailLogin.text.isEmpty())
            binding.emailLogin.error = "Text area is Empty."
        if (binding.passwdLogin.text.isEmpty())
            binding.passwdLogin.error = "Text area is Empty."
    }

    fun isRegistred(): Boolean {
        return firebaseAuth.currentUser != null
    }
}

