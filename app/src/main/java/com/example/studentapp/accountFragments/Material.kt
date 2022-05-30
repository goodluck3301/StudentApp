package com.example.studentapp.accountFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentapp.R
import com.example.studentapp.adapter.MaterialsAdapter
import com.example.studentapp.database.MaterialDatabase
import com.example.studentapp.database.MaterialsData
import com.example.studentapp.databinding.FragmentBooksBinding
import com.example.studentapp.models.MaterialModel
import com.example.studentapp.models.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class Material : Fragment() {

    private lateinit var binding: FragmentBooksBinding
    private lateinit var adapter: MaterialsAdapter
    private lateinit var localDb: MaterialDatabase
    private var dataList = mutableListOf<MaterialsData>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBooksBinding.inflate(inflater)
        localDb = context?.let { MaterialDatabase.getDatabase(it) }!!
        GlobalScope.launch(Dispatchers.IO) { getMaterials() }

        CoroutineScope(Dispatchers.IO).launch {
            dataList += (
                    localDb
                        .materialDao()
                        .getAll()
                    ).toMutableList()
        }

        adapter = context?.let { MaterialsAdapter(it, dataList) }!!
        binding.materialRec.adapter = adapter
        binding.materialRec.layoutManager = LinearLayoutManager(context)


        return binding.root

    }

    private fun getMaterials() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("materials")
                .get()
                .addOnSuccessListener { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in result) {
                            dataList = mutableListOf()
                            if ((localDb.materialDao()
                                    .isNotExists(document.get("materialTitle").toString()))
                            ) {
                                localDb.materialDao().insertData(
                                    MaterialsData(
                                        document.get("materialImgURI").toString(),
                                        document.get("materialURL").toString(),
                                        document.get("materialTitle").toString(),
                                        document.get("materialDesc").toString(),
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
    }// getMaterials()

    override fun onResume() {
        super.onResume()

    }

}