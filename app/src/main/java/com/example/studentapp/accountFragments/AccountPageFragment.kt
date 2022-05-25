package com.example.studentapp.accountFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.studentapp.R
import com.example.studentapp.database.UserDatabase
import com.example.studentapp.database.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


@DelicateCoroutinesApi
class AccountPageFragment : Fragment() {

    lateinit var text:TextView
    private lateinit var database: UserDatabase
    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = context?.let { UserDatabase.getDatabase(it) }!!
        return inflater.inflate(R.layout.fragment_account_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text = view.findViewById(R.id.accelerate)

        CoroutineScope(Dispatchers.IO).launch {
            readDataFirestore()
            //getDatabaseInfo()
        }

        CoroutineScope(Dispatchers.Main).launch {
            text.text = username
        }
    }// onViewCreated()

    private fun getDatabaseInfo () {
        CoroutineScope(Dispatchers.IO).launch {
            val sigInUser = database.userDao().getAll()
            username = sigInUser.last().nameUser.toString()
        }
    }

    private fun readDataFirestore() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    var usersInfo: UserInfo
                    for (document in result) {
                        if ((document.get("idUser")
                                .toString()) == (FirebaseAuth.getInstance().uid).toString()
                        ) {

                            //text.text = document.get("name").toString()
                            usersInfo = UserInfo(
                                document.get("name").toString(),
                                document.get("score").toString(),
                                document.get("email").toString(),
                                document.get("userURLtoImage").toString()
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                if (database.userDao()
                                        .isNotExists(document.get("email").toString())
                                ) {
                                    database.userDao().insertData(usersInfo)
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { exception -> Log.w("TAG", "Error getting documents.", exception) }
        }
    }
}