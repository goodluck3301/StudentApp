package com.example.studentapp.accountFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentapp.GeneralFunctions
import com.example.studentapp.adapter.MaterialsAdapter
import com.example.studentapp.database.MaterialDatabase
import com.example.studentapp.database.MaterialsData
import com.example.studentapp.databinding.FragmentBooksBinding
import com.example.studentapp.questions.Questions
import com.example.studentapp.questions.Questions.TopDataList
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class Material : Fragment() {

    private lateinit var binding: FragmentBooksBinding
    private lateinit var adapter: MaterialsAdapter
    private lateinit var localDb: MaterialDatabase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBooksBinding.inflate(inflater)
        localDb = context?.let { MaterialDatabase.getDatabase(it) }!!
        getMaterials()
        /*CoroutineScope(Dispatchers.IO).launch {
            val newList = (
                    localDb
                        .materialDao()
                        .getAll()
                    ).toMutableList()

            newList.forEach {
                if (!TopDataList.contains(it)) {
                    TopDataList.add(it)
                }
            }

        }*/
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar4.visibility = View.VISIBLE

            if (!GeneralFunctions.check1) {
                binding.progressBar4.visibility = View.VISIBLE
                GeneralFunctions.check1 = true
                // delay(5000)
                startAdapter()
                adapter.notifyDataSetChanged()
                binding.progressBar4.visibility = View.GONE
            } else {
                binding.progressBar4.visibility = View.GONE
                startAdapter()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun startAdapter() {
        TopDataList.sortBy { it.id }
        adapter = context?.let { MaterialsAdapter(it, TopDataList) }!!
        binding.materialRec.adapter = adapter
        binding.materialRec.layoutManager = LinearLayoutManager(context)
    }

    private fun getMaterials() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("materials")
                .get()
                .addOnSuccessListener { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in result) {
                            //  TopDataList = mutableListOf()
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