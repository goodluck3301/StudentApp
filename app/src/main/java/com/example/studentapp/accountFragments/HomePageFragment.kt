package com.example.studentapp.accountFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentContainer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.studentapp.GeneralFunctions
import com.example.studentapp.R
import com.example.studentapp.adapter.TopUsersAdapter
import com.example.studentapp.databinding.FragmentHomeBinding
import com.example.studentapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.*


class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: TopUsersAdapter
    private var allUserList = mutableListOf<UserModel>()
    private var newUserList = mutableListOf<UserModel>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readDataFirestore()

        CoroutineScope(Dispatchers.Main).launch {
            if (!GeneralFunctions.check && context?.let { GeneralFunctions.checkForInternet(it) } == true) {
                GeneralFunctions.check = true
                binding.progressBar3.visibility = View.VISIBLE
                delay(5000)
                binding.progressBar3.visibility = View.GONE
                newUserList.reverse()
                startAdapter()
            } else
                newUserList.reverse()
            startAdapter()
        }

        if (context?.let { GeneralFunctions.checkForInternet(it) } == true)
            Toast.makeText(
                context,
                "Կապ չի գտնվել, հնարավոր է տվյալների ոչ լիարժեք բեռնում :(",
                Toast.LENGTH_LONG
            ).show()
    }

    private fun startAdapter() {
        adapter = TopUsersAdapter(context, newUserList)
        binding.topUserRecycleView.layoutManager = LinearLayoutManager(context)
        binding.topUserRecycleView.adapter = adapter
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun readDataFirestore() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    //  CoroutineScope(Dispatchers.IO).launch {
                    allUserList = mutableListOf()
                    for (document in result) {
                        allUserList += (
                                UserModel(
                                    document.get("name").toString(),
                                    document.get("score").toString(),
                                    document.get("userURLtoImage").toString(),
                                )
                                )
                        allUserList.sortBy { it.userScore.toInt() }
                    }
                    // }
                    var newList = mutableListOf<UserModel>()
                    allUserList.forEachIndexed { i, e ->
                        if (i <= 9) {
                            newList.add(e)
                        }
                    }
                    newUserList = newList
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }
    }// readDataFirestore()


}