package com.example.studentapp.accountFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentapp.GeneralFunctions
import com.example.studentapp.adapter.TopUsersAdapter
import com.example.studentapp.database.MaterialDatabase
import com.example.studentapp.database.MaterialsData
import com.example.studentapp.databinding.FragmentHomeBinding
import com.example.studentapp.models.UserModel
import com.example.studentapp.questions.Data
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: TopUsersAdapter
    private var allUserList = mutableListOf<UserModel>()
    private lateinit var localDb: MaterialDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        localDb = context?.let { MaterialDatabase.getDatabase(it) }!!
        readDataFirestore()
        getMaterials()
        CoroutineScope(Dispatchers.IO).launch {
            Data.TopDataList = (
                    localDb
                        .materialDao()
                        .getAll()
                    ).toMutableList()
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            if (!GeneralFunctions.check && context?.let { GeneralFunctions.checkForInternet(it) } == true) {
                GeneralFunctions.check = true
                binding.progressBar3.visibility = View.VISIBLE
                delay(5000)
                binding.progressBar3.visibility = View.GONE
                startAdapter()
            } else
                startAdapter()
        }

        if (context?.let { GeneralFunctions.checkForInternet(it) } == false)
            Toast.makeText(
                context,
                "Կապ չի գտնվել, հնարավոր է տվյալների ոչ լիարժեք բեռնում :(",
                Toast.LENGTH_LONG
            ).show()
    }

    private fun startAdapter() {
        allUserList.sortByDescending { it.userScore.toInt() }
        adapter = TopUsersAdapter(context, allUserList)
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
                  var allUserList11 = mutableListOf<UserModel>()
                    for (document in result) {
                        allUserList11 += (
                                UserModel(
                                    document.get("name").toString(),
                                    document.get("score").toString(),
                                    document.get("userURLtoImage").toString(),
                                )
                                )
                        allUserList.sortByDescending { it.userScore.toInt() }
                    }


                    allUserList11.forEachIndexed { i, e ->
                        if (i <= 9) {
                            allUserList.add(e)
                        }
                    }
                    allUserList11.sortByDescending { it.userScore.toInt() }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }
    }// readDataFirestore()*/

    private fun getMaterials() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("materials")
                .get()
                .addOnSuccessListener { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in result) {
                            if ((localDb.materialDao()
                                    .isNotExists(document.get("materialTitle").toString()))
                            ) {
                                localDb.materialDao().insertData(
                                    MaterialsData(
                                        document.get("materialImgURI").toString(),
                                        document.get("materialURL").toString(),
                                        document.get("materialTitle").toString(),
                                        document.get("materialDesc").toString(),
                                        document.get("id").toString().toInt()
                                    )
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }
        }
    }// getMaterials()*/
}