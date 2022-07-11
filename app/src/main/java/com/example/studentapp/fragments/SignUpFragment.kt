package com.example.gavarstateuniversityapp.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.studentapp.objects.GeneralFunctions.checkForInternet
import com.example.studentapp.R
import com.example.studentapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        binding = FragmentSignUpBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toLoginPage.setOnClickListener {
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragmentContainerView, LogInFragment()).commit()
            }
        }

        binding.okS.setOnClickListener {

            var internet = false
            var emailBool = false
            var passBool = false
            var nameBool = false

            if (context?.let { it1 -> checkForInternet(it1) } == true) {
                internet = true
            } else
                Toast.makeText(
                    context, "Համացանցը չի գտնվել :(", Toast.LENGTH_LONG
                ).show()
            if (isEmailValid(binding.emailS.text.toString()))
                emailBool = true
            else
                binding.emailS.error = "Սխալ Էլ․ փոստի անվանում"
            if (passValid(binding.passwdS))
                passBool = true
            else {
                binding.show.visibility = View.INVISIBLE
            }
            if (binding.name.text.toString().isNotEmpty())
                nameBool = true
            else
                binding.name.error = "Լրացրեք!"

            if (emailBool && passBool && nameBool && internet) {
                if (binding.emailS.text.isNotEmpty() && binding.passwdS.text.toString() == binding.repeatPaswdS.text.toString()) {
                    firebaseAuth
                        .createUserWithEmailAndPassword(
                            binding.emailS.text.toString(),
                            binding.passwdS.text.toString()
                        )
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                addDataRealDatabase(
                                    binding.name.text.toString(),
                                    binding.emailS.text.toString()
                                )
                                binding.progressbarSignUP.visibility = View.VISIBLE
                                createNewUser()
                                fragmentManager?.beginTransaction()?.apply {
                                    replace(R.id.fragmentContainerView, LogInFragment()).commit()
                                }
                                isEmailSent()
                            } else {
                                binding.progressbarSignUP.visibility = View.GONE
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Դուք այս էլ․ փոստով արդեն ունեք պրոֆիլ :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    binding.progressbarSignUP.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }//btn

        var showHideBool1 = false
        binding.show.setOnClickListener {

            if (!showHideBool1) {
                showHideBool1 = true
                binding.passwdS.inputType = InputType.TYPE_CLASS_TEXT
             //   binding.show.setImageDrawable(resources.getDrawable(R.drawable.ic_focused_visibility_off_24))
                binding.show.visibility=View.GONE
            } else {
                showHideBool1 = false
                binding.passwdS.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            //    binding.show.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_24))
            }
        }//show1


        var showHideBool2 = false
        binding.show2.setOnClickListener {
            if (!showHideBool2) {
                showHideBool2 = true
                binding.repeatPaswdS.inputType = InputType.TYPE_CLASS_TEXT
             //   binding.show2.setImageDrawable(resources.getDrawable(R.drawable.ic_focused_visibility_off_24))
                binding.show2.visibility=View.GONE
            } else {
                showHideBool2 = false
             //   binding.repeatPaswdS.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
             //   binding.show2.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_24))
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
            "name" to binding.name.text.toString(),
            "email" to binding.emailS.text.toString(),
            "score" to 0,
            "userURLtoImage" to "",
            "idUser" to firebaseAuth.uid.toString(),
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot written with ID: ") }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }//createNewUser

    private fun passValid(pass: EditText): Boolean {
        val errorText = when {
            !pass.text.contains(Regex("[A-Z]")) -> {
                pass.error = "Password must contain one capital letter"
                false
            }
            !pass.text.contains(Regex("[0-9]")) -> {
                pass.error = "Password must contain one digit"
                false
            }
            !pass.text.contains(Regex("[^a-zA-Z0-9 ]")) -> {
                pass.error = "Password must contain one special character"
                false
            }
            else -> true
        }
        return errorText
    }//fun password Validation

    private fun addDataRealDatabase(name: String, email: String) {
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["accountImage"] = ""
        hashMap["email"] = email
        hashMap["namel"] = name
        hashMap["userType"] = "user"

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener { }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun isEmailSent(): Boolean {
        var emailSent = false
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
            emailSent = it.isSuccessful
        }
        Toast.makeText(context, "Ձեր էլ․ փոստին ուղարկվել է այն հաստատելու հղումը", Toast.LENGTH_LONG).show()
        return emailSent
    }
}


