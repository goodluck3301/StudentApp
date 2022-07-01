package com.example.studentapp.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentapp.accountFragments.Material
import com.example.studentapp.adapter.MaterialsAdapter
import com.example.studentapp.database.MaterialsData
import com.example.studentapp.databinding.FragmentAddMaterialDialogBinding
import com.example.studentapp.questions.Data.TopDataList
import com.example.studentapp.questions.Data.contextFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class AddMaterialDialog : DialogFragment() {

    private lateinit var binding: FragmentAddMaterialDialogBinding
    private var selectPhotoUri: Uri? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private val mStorageRef = FirebaseStorage.getInstance().reference
    lateinit var adapter:MaterialsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddMaterialDialogBinding.inflate(inflater)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    private var checkImage = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addMaterialImager.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        binding.cancleDdata.setOnClickListener { dialog?.dismiss() }
        binding.sendDdata.setOnClickListener {
            if (addMaterialValidation()) {
                sendData()
                dialog?.dismiss()
            } else
                Toast
                    .makeText(
                        context,
                        "Լրացրեք բաց թողնված հատվածը :(",
                        Toast.LENGTH_SHORT)
                    .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUri = data.data
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.addMaterialImager.setImageDrawable(bitmapDrawable)
            checkImage = true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMaterials(upLoadUri: Uri) {
        val db = Firebase.firestore
        var list = mutableListOf<Int>()
        db.collection("materials")
            .get()
            .addOnSuccessListener { result ->
                list = mutableListOf()
                for (document in result) {
                    list += document.get("id").toString().toInt()
                }
                list.sort()
            }
        var photoUrl: String
        val imageFileName = "materialsImage/material${System.currentTimeMillis()}.png"
        val upLoadTask = mStorageRef.child(imageFileName)
        upLoadTask.putFile(upLoadUri).addOnCompleteListener { Task1 ->
            if (Task1.isSuccessful) {
                upLoadTask.downloadUrl.addOnCompleteListener { Task2 ->
                    if (Task2.isSuccessful) {
                        photoUrl = Task2.result.toString()

                        val material = hashMapOf(
                            "materialTitle" to binding.addMaterialTitle.text.toString(),
                            "materialDesc" to binding.addMaterialDesc.text.toString(),
                            "materialURL" to binding.addMaterialUrl.text.toString(),
                            "materialImgURI" to photoUrl,
                            "id" to list.last() + 1,
                            "idUser" to firebaseAuth.uid.toString(),
                        )

                        db.collection("materials")
                            .add(material)
                            .addOnSuccessListener {
                                adapter=  contextFragment?.let { it1 -> MaterialsAdapter(it1, TopDataList){} }!!
                                TopDataList +=
                                    MaterialsData(
                                        photoUrl,
                                        binding.addMaterialUrl.text.toString(),
                                        binding.addMaterialTitle.text.toString(),
                                        binding.addMaterialDesc.text.toString(),
                                        list.last() + 1,
                                        firebaseAuth.uid.toString(),
                                    )
                                Log.d(ContentValues.TAG, "Successes")
                                adapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error", e) }
                    }
                }
            }
        }
    }//add user

    private fun sendData() {
        selectPhotoUri?.let { addMaterials(it) }
    }

    private fun addMaterialValidation(): Boolean {
        return binding.addMaterialUrl.text.isNotEmpty()
                && binding.addMaterialTitle.text.isNotEmpty()
                && binding.addMaterialDesc.text.isNotEmpty()
    }
}//class