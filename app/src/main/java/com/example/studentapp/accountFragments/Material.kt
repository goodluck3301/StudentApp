package com.example.studentapp.accountFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentapp.objects.GeneralFunctions
import com.example.studentapp.adapter.MaterialsAdapter
import com.example.studentapp.database.materialdb.MaterialDatabase
import com.example.studentapp.database.materialdb.MaterialsData
import com.example.studentapp.databinding.FragmentBooksBinding
import com.example.studentapp.dialogs.AddMaterialDialog
import com.example.studentapp.objects.Data.TopDataList
import com.example.studentapp.objects.Data.contextFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


class Material : Fragment() {

    private lateinit var binding: FragmentBooksBinding
    private lateinit var adapter: MaterialsAdapter
    private lateinit var localDb: MaterialDatabase
    private val dialog = AddMaterialDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksBinding.inflate(inflater)
        contextFragment = context
        localDb = context?.let { MaterialDatabase.getDatabase(it) }!!
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.getFilter().filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)
                return true
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar4.visibility = View.VISIBLE
            if (!GeneralFunctions.check1) {
                binding.progressBar4.visibility = View.VISIBLE
                GeneralFunctions.check1 = true
                startAdapter()
                adapter.notifyDataSetChanged()
                binding.progressBar4.visibility = View.GONE
            } else {
                binding.progressBar4.visibility = View.GONE
                startAdapter()
                adapter.notifyDataSetChanged()
            }
        }

        binding.addButtom.setOnClickListener {
            dialog.show(parentFragmentManager, "tag")
            getMaterials()
            adapter.notifyDataSetChanged()
        }



        binding.refreshMaterial.setOnRefreshListener {
            refreshAdapter()
        }

    }//

    fun refreshAdapter() {
        if (context?.let { GeneralFunctions.checkForInternet(it) } == true) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                checkMaterials()
                TopDataList.clear()
                getMaterials()
            }
        }else {
            Toast.makeText(context, "?????????????? ?????????????????? ??????????????????????????", Toast.LENGTH_SHORT).show()
            binding.refreshMaterial.isRefreshing = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startAdapter() {
        TopDataList.sortBy { it.materialid }
        TopDataList.reverse()
        adapter = context?.let {
            MaterialsAdapter(it, TopDataList) {
                refreshAdapter()
            }
        }!!
        binding.materialRec.adapter = adapter
        binding.materialRec.layoutManager = LinearLayoutManager(context)
        adapter.notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getMaterials() {
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("materials")
                .get()
                .addOnSuccessListener { result ->
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val list = mutableListOf<MaterialsData>()
                            for (document in result) {
                                list.add(
                                    MaterialsData(
                                        document.get("materialImgURI").toString(),
                                        document.get("materialURL").toString(),
                                        document.get("materialTitle").toString(),
                                        document.get("materialDesc").toString(),
                                        document.get("id").toString().toInt(),
                                        document.get("idUser").toString(),
                                    )
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    if ((localDb.materialDao()
                                            .isNotExists(document.get("materialTitle").toString()))
                                    ) {
                                        localDb.materialDao().insertData(
                                            MaterialsData(
                                                document.get("materialImgURI").toString(),
                                                document.get("materialURL").toString(),
                                                document.get("materialTitle").toString(),
                                                document.get("materialDesc").toString(),
                                                document.get("id").toString().toInt(),
                                                document.get("idUser").toString(),
                                            )
                                        )
                                    }
                                }
                            }
                            TopDataList = list
                            binding.materialRec.adapter!!.notifyDataSetChanged()
                            startAdapter()
                            binding.refreshMaterial.isRefreshing = false
                            binding.materialRec.adapter!!.notifyDataSetChanged()
                        } catch (e: Exception) {
                        }
                    }
                }
        }
    }//


    private fun checkMaterials() {
        var listDb = mutableListOf<MaterialsData>()
        var listFirestore = mutableListOf<MaterialsData>()
        val db = Firebase.firestore
        Firebase.auth.currentUser?.let {
            db.collection("materials")
                .get()
                .addOnSuccessListener { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        for (document in result) {
                            listFirestore.add(
                                MaterialsData(
                                    document.get("materialImgURI").toString(),
                                    document.get("materialURL").toString(),
                                    document.get("materialTitle").toString(),
                                    document.get("materialDesc").toString(),
                                    document.get("id").toString().toInt(),
                                    document.get("idUser").toString(),
                                )
                            )
                        }
                        listDb = localDb.materialDao().getAll().toMutableList()
                        val difference = listDb.minus(listFirestore)
                        difference.forEach {
                            localDb.materialDao().delete(it)
                        }
                    }
                }
        }
    }
}